.PHONY: build build-typecheck bundle_install cicoverage citypecheck citest citypecoverage clean clean-coverage clean-typecheck clean-typecoverage coverage default gem_dependencies help lint overcommit post_cookiecutter_sync quality repl report-coverage report-coverage-to-codecov test typecheck typecoverage update_from_cookiecutter docs ktlint ktlint-format detekt
.DEFAULT_GOAL := default

define PRINT_HELP_PYSCRIPT
import re, sys

for line in sys.stdin:
	match = re.match(r'^([a-zA-Z_-]+):.*?## (.*)$$', line)
	if match:
		target, help = match.groups()
		print("%-20s %s" % (target, help))
endef
export PRINT_HELP_PYSCRIPT

help:
	@python -c "$$PRINT_HELP_PYSCRIPT" < $(MAKEFILE_LIST)

default: clean-typecoverage build typecheck typecoverage clean-coverage test coverage overcommit_branch quality lint ## run default typechecking, tests and quality

SOURCE_FILE_GLOBS = ['{config,lib,app,script,spec}/**/*.rb', 'ext/**/*.{c,rb}']

SOURCE_FILES := $(shell ruby -e "puts Dir.glob($(SOURCE_FILE_GLOBS))")

start: ## run code continously and watch files for changes
	./gradlew installDebug

build: bundle_install pip_install build-typecheck ## Assemble the debug APK
	./gradlew assembleDebug

build-typecheck: types.installed  ## Compile Kotlin (main + test sources); this is the type-checking gate
	./gradlew compileDebugKotlin compileDebugUnitTestKotlin compileDebugAndroidTestKotlin

types.installed: Gemfile.lock Gemfile.lock.installed ## Ensure typechecking dependencies are in place
	touch types.installed

docs: ## Generate Dokka API documentation
	./gradlew dokkaGenerateHtml

lint: detekt ktlint ## Run Android Lint, detekt, and ktlint
	./gradlew lint

ktlint: ## Check Kotlin formatting
	./gradlew ktlintCheck

ktlint-format: ## Auto-fix Kotlin formatting
	./gradlew ktlintFormat

detekt: ## Run detekt static analysis
	./gradlew detekt

clean-typecheck: ## Refresh the easily-regenerated information that type checking depends on
	rm -f types.installed
	./gradlew clean

realclean-typecheck: clean-typecheck ## Remove all type checking artifacts

realclean: clean realclean-typecheck
	rm -fr vendor/bundle .bundle/config
	rm -f .make/*
	rm -f *.installed

# Kotlin is statically typed, so compiling (build-typecheck) is itself the
# type-check gate -- there is no separate Sorbet/Solargraph-style checker to
# run. lint/detekt/ktlint (Android Lint, static analysis, and style
# checking) are the RuboCop-equivalent tools instead, and live under the
# `lint` target, same as RuboCop is kept out of `typecheck` in the Ruby
# family.
typecheck: build-typecheck ## validate types in code and configuration

citypecheck: typecheck ## Run type check from CircleCI

typecoverage: typecheck ## Run type checking and then ratchet coverage in metrics/

clean-typecoverage: ## Clean out type-related coverage previous results to avoid flaky results

citypecoverage: citypecheck ## Run type checking, ratchet coverage, and then complain if ratchet needs to be committed

config/env: config/env.1p  ## Create file suitable for docker-compose usage
	cat config/env.1p | cut -d= -f1 > config/env

requirements_dev.txt.installed: requirements_dev.txt
	pip install -q --disable-pip-version-check -r requirements_dev.txt
	touch requirements_dev.txt.installed

pip_install: requirements_dev.txt.installed ## Install Python dependencies

Gemfile.lock: Gemfile .bundle/config
	if [ ! -f Gemfile.lock ]; then \
	  bundle install; \
	else \
	  bundle lock; \
	fi

.bundle/config:
	touch .bundle/config

gem_dependencies: .bundle/config

# Ensure any Gemfile.lock changes, even pulled from git, ensure a
# bundle is installed.
Gemfile.lock.installed: Gemfile vendor/.keep
	touch Gemfile.lock.installed

vendor/.keep: Gemfile.lock .ruby-version
	make gem_dependencies
	bundle install
	touch vendor/.keep

bundle_install: Gemfile.lock.installed ## Install Ruby dependencies

clean: clean-typecoverage clean-typecheck clean-coverage ## remove all built artifacts

test: ## run unit tests quickly
	./gradlew testDebugUnitTest

citest: test ## Run unit tests from CircleCI

connected_test: ## Run instrumented tests against a Gradle Managed Device
	./gradlew pixel6api34DebugAndroidTest

citest_connected: connected_test ## Run instrumented tests from CircleCI

emulator: ## Launch the local AVD (see fix.sh for setup); installDebug/instrumented tests can then target it
	emulator -avd pixel6api34

overcommit: ## run precommit quality checks
	bin/overcommit --run

overcommit_branch: ## run precommit quality checks only on changed files
	bin/overcommit --run --diff origin/main

quality: overcommit ## run precommit quality checks

repl: ## Launch an interactive development shell
	@echo "Teach me how to make repl"
	@exit 1

clean-coverage: ## Clean out previous output of test coverage to avoid flaky results from previous runs

coverage: test report-coverage ## check code coverage

report-coverage: test ## Generate HTML/XML Jacoco coverage report
	./gradlew jacocoTestReport

report-coverage-to-codecov: report-coverage ## use codecov.io for PR-scoped code coverage reports
	@curl -Os https://uploader.codecov.io/latest/linux/codecov
	@chmod +x codecov
	# Codecov requires a repository upload token for private repos (set
	# CODECOV_TOKEN as a CircleCI project env var to enable real uploads;
	# see https://app.codecov.io/gh/<org>/<repo>/config/general). Fails
	# the build if a configured token is rejected, but is best-effort
	# (does not fail the build) when no token is set at all.
	@if [ -n "$$CODECOV_TOKEN" ]; then \
		./codecov --file app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml --token "$$CODECOV_TOKEN"; \
	else \
		./codecov --file app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml || true; \
	fi

update_apt: .make/apt_updated

.make/apt_updated:
	sudo DEBIAN_FRONTEND=noninteractive apt-get update -y
	touch .make/apt_updated

cicoverage: citest report-coverage-to-codecov ## check code coverage

update_from_cookiecutter: ## Bring in changes from template project used to create this repo
	bin/cookiecutter_project_upgrader.sh
	@$(MAKE) post_cookiecutter_sync

post_cookiecutter_sync: ## Ecosystem-specific steps after template sync
	./gradlew wrapper --gradle-version="$$(./gradlew --version | sed -n 's/^Gradle //p')" || true
	./gradlew dependencyUpdates --refresh-dependencies || true
	( make build && git add app/build.gradle.kts build.gradle.kts gradle/wrapper ) || true
