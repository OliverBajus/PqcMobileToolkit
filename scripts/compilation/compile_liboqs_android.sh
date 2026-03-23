#!/usr/bin/env bash
set -euo pipefail

# =========================
# Config
# =========================
USE_OPENSSL=true            # Set to false to compile without OpenSSL
OPENSSL_VERSION="3.5.5"
LIBOQS_VERSION="0.15.0"    # liboqs release tag (e.g. "0.15.0", "main" for latest)
ANDROID_API=21

# Build for device + emulator
ABIS=("arm64-v8a" "x86_64")

# Your NDK (confirmed)
ANDROID_NDK="${ANDROID_NDK:-$HOME/Library/Android/sdk/ndk/28.0.13004108}"

# Paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="${SCRIPT_DIR}/_build_android"
OPENSSL_SRC_DIR="${BUILD_DIR}/openssl_src"
LIBOQS_SRC_DIR="${BUILD_DIR}/liboqs_src"

# =========================
# Prerequisites check
# =========================
echo "==> Checking prerequisites..."
for cmd in cmake ninja git curl; do
  if ! command -v "${cmd}" &>/dev/null; then
    echo "ERROR: '${cmd}' is required but not found. Please install it first."
    exit 1
  fi
done

if [[ ! -d "${ANDROID_NDK}" ]]; then
  echo "ERROR: ANDROID_NDK not found at: ${ANDROID_NDK}"
  echo "  Set ANDROID_NDK env variable or install via Android Studio SDK Manager."
  exit 1
fi

NDK_HOST_TAG="$(ls "${ANDROID_NDK}/toolchains/llvm/prebuilt" | head -n 1)"
if [[ -z "${NDK_HOST_TAG}" ]]; then
  echo "ERROR: Could not detect NDK host tag in ${ANDROID_NDK}/toolchains/llvm/prebuilt"
  exit 1
fi

LLVM_PREBUILT="${ANDROID_NDK}/toolchains/llvm/prebuilt/${NDK_HOST_TAG}"
export PATH="${LLVM_PREBUILT}/bin:${PATH}"

export ANDROID_NDK_ROOT="${ANDROID_NDK}"
export ANDROID_NDK_HOME="${ANDROID_NDK}"

ANDROID_TOOLCHAIN_FILE="${ANDROID_NDK}/build/cmake/android.toolchain.cmake"

echo "==> Using NDK: ${ANDROID_NDK}"
echo "==> Host tag: ${NDK_HOST_TAG}"
echo "==> API: ${ANDROID_API}"
echo "==> liboqs: ${LIBOQS_VERSION}"
echo "==> OpenSSL: ${USE_OPENSSL} (version ${OPENSSL_VERSION})"
echo "==> ABIs: ${ABIS[*]}"

# Clean previous build
rm -rf "${BUILD_DIR}"
mkdir -p "${BUILD_DIR}"

# =========================
# Step 1: Fetch liboqs
# =========================
echo "==> Cloning liboqs ${LIBOQS_VERSION}"
git clone --depth 1 --branch "${LIBOQS_VERSION}" \
  https://github.com/open-quantum-safe/liboqs.git "${LIBOQS_SRC_DIR}"

# =========================
# Step 2: Fetch OpenSSL (if enabled)
# =========================
if [[ "${USE_OPENSSL}" == true ]]; then
  echo "==> Downloading OpenSSL ${OPENSSL_VERSION}"
  mkdir -p "${OPENSSL_SRC_DIR}"
  curl -L "https://www.openssl.org/source/openssl-${OPENSSL_VERSION}.tar.gz" \
    | tar -xz --strip-components=1 -C "${OPENSSL_SRC_DIR}"
else
  echo "==> Skipping OpenSSL (USE_OPENSSL=false)"
fi

# =========================
# Helpers
# =========================
openssl_target_for_abi() {
  case "$1" in
    arm64-v8a)   echo "android-arm64" ;;
    armeabi-v7a) echo "android-arm" ;;
    x86_64)      echo "android-x86_64" ;;
    *) echo "Unsupported ABI: $1" >&2; exit 1 ;;
  esac
}

cc_for_abi() {
  case "$1" in
    arm64-v8a)   echo "aarch64-linux-android${ANDROID_API}-clang" ;;
    armeabi-v7a) echo "armv7a-linux-androideabi${ANDROID_API}-clang" ;;
    x86_64)      echo "x86_64-linux-android${ANDROID_API}-clang" ;;
    *) echo "Unsupported ABI: $1" >&2; exit 1 ;;
  esac
}

cxx_for_abi() {
  case "$1" in
    arm64-v8a)   echo "aarch64-linux-android${ANDROID_API}-clang++" ;;
    armeabi-v7a) echo "armv7a-linux-androideabi${ANDROID_API}-clang++" ;;
    x86_64)      echo "x86_64-linux-android${ANDROID_API}-clang++" ;;
    *) echo "Unsupported ABI: $1" >&2; exit 1 ;;
  esac
}

# =========================
# Build loop
# =========================
for ABI in "${ABIS[@]}"; do
  echo "============================================================"
  echo "==> ABI: ${ABI}"
  echo "============================================================"

  LIBOQS_BUILD_DIR="${BUILD_DIR}/liboqs_build_${ABI}"
  LIBOQS_INSTALL_DIR="${BUILD_DIR}/liboqs_dist/${ABI}"

  export CC="$(cc_for_abi "${ABI}")"
  export CXX="$(cxx_for_abi "${ABI}")"
  export AR="llvm-ar"
  export RANLIB="llvm-ranlib"
  export STRIP="llvm-strip"

  # Build cmake args common to both modes
  CMAKE_ARGS=(
    -DCMAKE_TOOLCHAIN_FILE="${ANDROID_TOOLCHAIN_FILE}"
    -DANDROID_ABI="${ABI}"
    -DANDROID_PLATFORM="android-${ANDROID_API}"
    -DANDROID_STL="c++_shared"
    -DCMAKE_BUILD_TYPE=Release
    -DBUILD_SHARED_LIBS=ON
    -DOQS_BUILD_ONLY_LIB=ON
    -DOQS_ENABLE_KEM_HQC=ON
    -DOQS_DIST_BUILD=ON
    -DCMAKE_INSTALL_PREFIX="${LIBOQS_INSTALL_DIR}"
  )

  if [[ "${USE_OPENSSL}" == true ]]; then
    # -------------------------
    # Build OpenSSL (static)
    # -------------------------
    OPENSSL_BUILD_DIR="${BUILD_DIR}/openssl_build_${ABI}"
    OPENSSL_INSTALL_DIR="${BUILD_DIR}/openssl_dist/${ABI}"
    OPENSSL_TARGET="$(openssl_target_for_abi "${ABI}")"

    echo "==> Building OpenSSL (${OPENSSL_TARGET}) for ${ABI}"
    rm -rf "${OPENSSL_BUILD_DIR}" "${OPENSSL_INSTALL_DIR}"
    mkdir -p "${OPENSSL_BUILD_DIR}" "${OPENSSL_INSTALL_DIR}"

    pushd "${OPENSSL_SRC_DIR}" >/dev/null
    make clean >/dev/null 2>&1 || true

    ./Configure "${OPENSSL_TARGET}" \
      -D__ANDROID_API__="${ANDROID_API}" \
      no-shared no-tests \
      -fPIC \
      --prefix="${OPENSSL_INSTALL_DIR}" \
      --openssldir="${OPENSSL_INSTALL_DIR}/ssl"

    make -j"$(sysctl -n hw.ncpu)"
    make install_sw
    popd >/dev/null

    # Add OpenSSL cmake flags
    CMAKE_ARGS+=(
      -DOQS_USE_OPENSSL=ON
      -DOPENSSL_ROOT_DIR="${OPENSSL_INSTALL_DIR}"
      -DOPENSSL_INCLUDE_DIR="${OPENSSL_INSTALL_DIR}/include"
      -DOPENSSL_CRYPTO_LIBRARY="${OPENSSL_INSTALL_DIR}/lib/libcrypto.a"
      -DOPENSSL_SSL_LIBRARY="${OPENSSL_INSTALL_DIR}/lib/libssl.a"
    )
  else
    CMAKE_ARGS+=(-DOQS_USE_OPENSSL=OFF)
  fi

  # -------------------------
  # Build liboqs
  # -------------------------
  echo "==> Building liboqs (.so) for ${ABI} (OpenSSL=${USE_OPENSSL})"
  rm -rf "${LIBOQS_BUILD_DIR}" "${LIBOQS_INSTALL_DIR}"
  mkdir -p "${LIBOQS_BUILD_DIR}" "${LIBOQS_INSTALL_DIR}"

  pushd "${LIBOQS_BUILD_DIR}" >/dev/null
  cmake -G Ninja "${LIBOQS_SRC_DIR}" "${CMAKE_ARGS[@]}"

  ninja
  ninja install
  popd >/dev/null

  echo ""
  echo "ABI DONE: ${ABI}"
  echo "  liboqs.so: ${LIBOQS_INSTALL_DIR}/lib/liboqs.so"
done

echo "============================================================"
echo "ALL SUCCESS! (OpenSSL=${USE_OPENSSL})"
echo "============================================================"
echo "Outputs:"
for ABI in "${ABIS[@]}"; do
  echo "  - ${BUILD_DIR}/liboqs_dist/${ABI}/lib/liboqs.so"
done
