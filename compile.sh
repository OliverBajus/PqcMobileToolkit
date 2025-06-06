# ARM 64-bit (arm64-v8a) - Most Android devices
rm -rf build-arm64-v8a && mkdir build-arm64-v8a && cd build-arm64-v8a
cmake -GNinja -DBUILD_SHARED_LIBS=ON -DPLATFORM=Android -DCMAKE_SYSTEM_NAME=Android \
      -DCMAKE_ANDROID_NDK=$ANDROID_HOME/ndk/25.2.9519653 -DCMAKE_SYSTEM_VERSION=21 \
      -DCMAKE_ANDROID_ARCH_ABI=arm64-v8a -DOQS_USE_OPENSSL=OFF ..
ninja
cd ..

# x86_64 (64-bit) - Simulators
rm -rf build-x86_64 && mkdir build-x86_64 && cd build-x86_64
cmake -GNinja -DBUILD_SHARED_LIBS=ON -DPLATFORM=Android -DCMAKE_SYSTEM_NAME=Android \
      -DCMAKE_ANDROID_NDK=$ANDROID_HOME/ndk/25.2.9519653 -DCMAKE_SYSTEM_VERSION=21 \
      -DCMAKE_ANDROID_ARCH_ABI=x86_64 -DOQS_USE_OPENSSL=OFF ..
ninja
cd ..


$ANDROID_HOME/ndk/28.0.13004108