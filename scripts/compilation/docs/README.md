# liboqs Android Compile Script

Cross-compiles [liboqs](https://github.com/open-quantum-safe/liboqs) as a shared library (`liboqs.so`) for Android, optionally linked with OpenSSL.

## Prerequisites

Install these before running the script:

- **Android NDK** (via Android Studio SDK Manager or standalone)
- **CMake** (`brew install cmake`)
- **Ninja** (`brew install ninja`)
- **Git**
- **curl**

The script checks for all of these at startup and will exit with a clear error if anything is missing.

## Configuration

Edit the variables at the top of `compile_liboqs_android.sh`:

| Variable | Default                                       | Description                                                    |
|---|-----------------------------------------------|----------------------------------------------------------------|
| `USE_OPENSSL` | `false`                                       | Set to `true` to compile liboqs with OpenSSL                   |
| `OPENSSL_VERSION` | `3.5.5`                                       | OpenSSL version to download (ignored when `USE_OPENSSL=false`) |
| `LIBOQS_VERSION` | `0.15.0`                                      | liboqs git tag to clone from GitHub                            |
| `ANDROID_API` | `21`                                          | Minimum Android API level                                      |
| `ABIS` | `arm64-v8a, x86_64, armeabi-v7a`              | Target architectures to build                                  |
| `ANDROID_NDK` | `$HOME/Library/Android/sdk/ndk/28.0.13004108` | Path to NDK (override via env variable)                        |

## Usage

```bash
cd scripts
./compile_liboqs_android.sh
```

The script automatically clones liboqs and (if enabled) downloads OpenSSL. Each run starts with a clean build directory.

## Build Outputs

After a successful build, the outputs are in `scripts/_build_android/liboqs_dist/`:

```
_build_android/liboqs_dist/
  <ABI>/
    lib/
      liboqs.so          <-- shared library
    include/
      oqs/
        oqs.h            <-- main header
        kem.h
        sig.h
        ...              <-- algorithm-specific headers
```

For each ABI (e.g. `arm64-v8a`, `x86_64`):

- **Shared library**: `_build_android/liboqs_dist/<ABI>/lib/liboqs.so`
- **Include headers**: `_build_android/liboqs_dist/<ABI>/include/oqs/`

### Where to put the outputs in this project

Copy the built files into the JNI module:

- **`.so` files** go to `libqos-android/jni/jniLibs/<ABI>/liboqs.so`
- **Headers** go to `libqos-android/jni/include/oqs/`

The `_build_android/` directory is gitignored and is deleted on each new build.
