// Top-level build file; plugin versions are declared here (apply false) and
// applied without a version in the modules that use them.
plugins {
    id("com.android.application") version "8.7.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.1" apply false
    id("org.jetbrains.dokka") version "2.2.0" apply false
    id("org.owasp.dependencycheck") version "12.2.2" apply false
    id("com.github.ben-manes.versions") version "0.51.0"
}
