# SonarQube build breaker

[![Build Status](https://travis-ci.org/qaware/sonarqube-build-breaker.svg?branch=master)](https://travis-ci.org/qaware/sonarqube-build-breaker)

Breaks the build if the SonarQube quality gate of the project is red.

## Components

* [Maven plugin](sqbb-maven-plugin/) - You can use this to run it in your Maven build.
* [CLI](cli/) - You can use this to run it in your CI pipeline as standalone application.
* [Library](library/) - A library which provides the building blocks. You only need this if you want to develop your own build breaker.

# Usage

## Recommended usage in the GitLab pipeline

Put this line directly after the SonarQube analysis:

```
mvn --batch-mode --update-snapshots --non-recursive de.qaware.tools.sonarqube-build-breaker:sqbb-maven-plugin:sqbb -Dsqbb.sonarQubeUrl=$SONAR_URL -Dsqbb.sonarQubeToken=$SONAR_TOKEN -Dsqbb.branch=$CI_BUILD_REF_NAME
```

This line will automatically download the newest SonarQube build breaker and run it.

The build breaker needs two environment variables set:

* `SONAR_TOKEN`
* `SONAR_URL`

These can be configured in the CI/CD settings in the GitLab project configuration.

# License

Licensed under [MIT](https://opensource.org/licenses/MIT), Copyright (c) 2019 QAware GmbH

# Maintainer

Moritz Kammerer (moritz.kammerer@qaware.de | [@phxql](https://github.com/phxql/))
