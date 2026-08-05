import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
    id("org.owasp.dependencycheck")
    id("com.github.ben-manes.versions")
    jacoco
}

android {
    namespace = "{{ cookiecutter.application_id }}"
    compileSdk = {{ cookiecutter.compile_sdk }}

    defaultConfig {
        applicationId = "{{ cookiecutter.application_id }}"
        minSdk = {{ cookiecutter.min_sdk }}
        targetSdk = {{ cookiecutter.target_sdk }}
        versionCode = 1
        versionName = "{{ cookiecutter.version }}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            enableUnitTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
        managedDevices {
            localDevices {
                create("pixel6api34") {
                    device = "Pixel 6"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.13")

    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

ktlint {
    version.set("1.5.0")
    android.set(true)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    source.setFrom(
        "src/main/kotlin",
        "src/test/kotlin",
        "src/androidTest/kotlin",
    )
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

dependencyCheck {
    formats = listOf("HTML", "JSON")
    failBuildOnCVSS = 7.0f
    suppressionFile = "$rootDir/config/dependency-check/suppressions.xml"
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter =
        listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/*\$Lambda$*.*",
            "**/*\$inlined$*.*",
        )

    val debugTree =
        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
            exclude(fileFilter)
        }

    classDirectories.setFrom(files(debugTree))
    sourceDirectories.setFrom(files("src/main/kotlin"))
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("outputs/unit_test_code_coverage/debugUnitTest/*.exec", "jacoco/testDebugUnitTest.exec")
        },
    )
}
