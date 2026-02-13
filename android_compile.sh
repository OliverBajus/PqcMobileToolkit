#!/usr/bin/env bash
set -euo pipefail

# =========================
# Config
# =========================
OPENSSL_VERSION="3.5.5"
ANDROID_API=21

# Build for device + emulator
ABIS=("arm64-v8a" "x86_64", "armeabi-v7a")

# Your NDK (confirmed)
ANDROID_NDK="${ANDROID_NDK:-$HOME/Library/Android/sdk/ndk/28.0.13004108}"

# Paths
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="${ROOT_DIR}/_build_android"
OPENSSL_SRC_DIR="${BUILD_DIR}/openssl_src"

# =========================
# Sanity checks
# =========================
if [[ ! -d "${ANDROID_NDK}" ]]; then
  echo "ERROR: ANDROID_NDK not found at: ${ANDROID_NDK}"
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
echo "==> OpenSSL: ${OPENSSL_VERSION}"
echo "==> ABIs: ${ABIS[*]}"
echo "==> Root: ${ROOT_DIR}"

mkdir -p "${BUILD_DIR}"

# =========================
# Step 1: Fetch OpenSSL once
# =========================
if [[ ! -d "${OPENSSL_SRC_DIR}" ]]; then
  echo "==> Downloading OpenSSL ${OPENSSL_VERSION}"
  mkdir -p "${OPENSSL_SRC_DIR}"
  curl -L "https://www.openssl.org/source/openssl-${OPENSSL_VERSION}.tar.gz" \
    | tar -xz --strip-components=1 -C "${OPENSSL_SRC_DIR}"
else
  echo "==> OpenSSL source already present: ${OPENSSL_SRC_DIR}"
fi

# =========================
# Helpers
# =========================
openssl_target_for_abi() {
  case "$1" in
    arm64-v8a) echo "android-arm64" ;;
    x86_64)    echo "android-x86_64" ;;
    *) echo "Unsupported ABI: $1" >&2; exit 1 ;;
  esac
}

cc_for_abi() {
  case "$1" in
    arm64-v8a) echo "aarch64-linux-android${ANDROID_API}-clang" ;;
    x86_64)    echo "x86_64-linux-android${ANDROID_API}-clang" ;;
    *) echo "Unsupported ABI: $1" >&2; exit 1 ;;
  esac
}

cxx_for_abi() {
  case "$1" in
    arm64-v8a) echo "aarch64-linux-android${ANDROID_API}-clang++" ;;
    x86_64)    echo "x86_64-linux-android${ANDROID_API}-clang++" ;;
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

  OPENSSL_BUILD_DIR="${BUILD_DIR}/openssl_build_${ABI}"
  OPENSSL_INSTALL_DIR="${BUILD_DIR}/openssl_dist/${ABI}"
  LIBOQS_BUILD_DIR="${BUILD_DIR}/liboqs_build_${ABI}"
  LIBOQS_INSTALL_DIR="${BUILD_DIR}/liboqs_dist/${ABI}"

  OPENSSL_TARGET="$(openssl_target_for_abi "${ABI}")"
  export CC="$(cc_for_abi "${ABI}")"
  export CXX="$(cxx_for_abi "${ABI}")"
  export AR="llvm-ar"
  export RANLIB="llvm-ranlib"
  export STRIP="llvm-strip"

  # -------------------------
  # Step 2: Build OpenSSL (static)
  # -------------------------
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

  # -------------------------
  # Step 3: Build liboqs (.so) linked with OpenSSL static
  # -------------------------
  echo "==> Building liboqs (.so) with OpenSSL for ${ABI}"
  rm -rf "${LIBOQS_BUILD_DIR}" "${LIBOQS_INSTALL_DIR}"
  mkdir -p "${LIBOQS_BUILD_DIR}" "${LIBOQS_INSTALL_DIR}"

  pushd "${LIBOQS_BUILD_DIR}" >/dev/null
  cmake -G Ninja "${ROOT_DIR}" \
    -DCMAKE_TOOLCHAIN_FILE="${ANDROID_TOOLCHAIN_FILE}" \
    -DANDROID_ABI="${ABI}" \
    -DANDROID_PLATFORM="android-${ANDROID_API}" \
    -DANDROID_STL="c++_shared" \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_SHARED_LIBS=ON \
    -DOQS_BUILD_ONLY_LIB=ON \
    -DOQS_ENABLE_KEM_HQC=ON \
    -DOQS_USE_OPENSSL=ON \
    -DOPENSSL_ROOT_DIR="${OPENSSL_INSTALL_DIR}" \
    -DOPENSSL_INCLUDE_DIR="${OPENSSL_INSTALL_DIR}/include" \
    -DOPENSSL_CRYPTO_LIBRARY="${OPENSSL_INSTALL_DIR}/lib/libcrypto.a" \
    -DOPENSSL_SSL_LIBRARY="${OPENSSL_INSTALL_DIR}/lib/libssl.a" \
    -DOQS_DIST_BUILD=ON \
    -DCMAKE_INSTALL_PREFIX="${LIBOQS_INSTALL_DIR}"

  ninja
  ninja install
  popd >/dev/null

  echo ""
  echo "✅ ABI DONE: ${ABI}"
  echo "  liboqs.so: ${LIBOQS_INSTALL_DIR}/lib/liboqs.so"
done

echo "============================================================"
echo "✅ ALL SUCCESS!"
echo "============================================================"
echo "Outputs:"
for ABI in "${ABIS[@]}"; do
  echo "  - ${BUILD_DIR}/liboqs_dist/${ABI}/lib/liboqs.so"
done
