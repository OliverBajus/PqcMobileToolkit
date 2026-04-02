plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
    jacoco
}

android {
    namespace = "io.github.oliverbajus.liboqs_android"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("x86_64")
            abiFilters.add("armeabi-v7a")
        }
    }

    buildTypes {
        debug {
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs", layout.buildDirectory.dir("native-libs").get().asFile.path)
        }
    }

    externalNativeBuild {
        ndkBuild {
            // Tells Gradle to put outputs from external native
            // builds in the path specified below.
            buildStagingDirectory("./outputs/ndk-build")
            path("jni/Android.mk")
            // Verbose: https://stackoverflow.com/a/44578257/8524651
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    ndkVersion = "28.0.13004108"
}

mavenPublishing {
    coordinates(
        groupId = "io.github.oliverbajus",
        artifactId = "liboqs-android",
        version = "0.1.0"
    )

    pom {
        name.set("liboqs-android")
        description.set("Android (Kotlin/Java) bindings for the Open Quantum Safe liboqs post-quantum cryptography library built on liboqs-java wrapper")
        url.set("https://github.com/OliverBajus/PqcMobileToolkit")
        inceptionYear.set("2025")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("oliverbajus")
                name.set("Oliver Bajus")
                url.set("https://github.com/OliverBajus")
            }
        }

        scm {
            url.set("https://github.com/OliverBajus/PqcMobileToolkit")
            connection.set("scm:git:git://github.com/OliverBajus/PqcMobileToolkit.git")
            developerConnection.set("scm:git:ssh://git@github.com/OliverBajus/PqcMobileToolkit.git")
        }
    }

    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

dependencies {
    implementation(libs.androidx.annotation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}