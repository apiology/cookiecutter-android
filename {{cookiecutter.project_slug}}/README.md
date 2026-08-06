# {{ cookiecutter.project_name }}

[![CircleCI](https://circleci.com/gh/apiology/{{cookiecutter.project_slug}}.svg?style=svg)](https://circleci.com/gh/apiology/{{cookiecutter.project_slug}})

{{ cookiecutter.project_short_description }}

## Prerequisites

None — `./fix.sh` bootstraps everything: a JDK, the Android SDK
command-line tools (platform-tools, build-tools, and the
`{{ cookiecutter.compile_sdk }}` platform), Ruby (for Overcommit-based git
hooks), and Python (for the shared lint tooling).

```
./fix.sh
```

## Building

```
make build           # assembleDebug
make test            # unit tests (./gradlew testDebugUnitTest)
make connected_test  # instrumented tests against a Gradle Managed Device
make lint            # Android Lint
make ktlint          # Kotlin style check (ktlintCheck)
make detekt          # Kotlin static analysis
make coverage        # Jacoco HTML/XML report
make docs            # Dokka API docs
make quality         # full Overcommit hook suite (includes the above)
```

[Gradle Managed Device](https://developer.android.com/studio/test/gmd):
a Gradle-provisioned emulator instance, created and torn down
automatically by the `connected_test` task itself, so CI does not need
a manually-managed emulator or device.

Or drive Gradle directly with `./gradlew <task>`.

## Installing on a device

With a device or emulator connected (`adb devices` shows it):

```
./gradlew installDebug
```

On macOS, `./fix.sh` sets up a local emulator (`make emulator` to launch
it). CI does not need one; see the Gradle Managed Device note under
[Building](#building) above.

## Project layout

- `app/` — the single Gradle module. `MainActivity` renders a Compose
  "Hello, {{ cookiecutter.project_name }}" screen.
- `app/src/test/` — JUnit4 + MockK unit tests.
- `app/src/androidTest/` — instrumented Compose UI tests, run via
  `make connected_test` (see [Building](#building) above).
- `config/detekt/detekt.yml`, `config/dependency-check/suppressions.xml` —
  quality tool configuration.

## Contributions

This project, as with all others, rests on the shoulders of a broad
ecosystem supported by many volunteers doing thankless work, along
with specific contributors.

In particular I'd like to call out:

* [Audrey Roy Greenfeld](https://github.com/audreyfeldroy) for the
  cookiecutter tool and associated examples, which keep my many
  projects building with shared boilerplate with a minimum of fuss.
