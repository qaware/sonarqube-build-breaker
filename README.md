# SonarQube build breaker

[![Build](https://github.com/qaware/sonarqube-build-breaker/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/qaware/sonarqube-build-breaker/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.qaware.tools.sonarqube-build-breaker/sonarqube-build-breaker/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.qaware.tools.sonarqube-build-breaker/sonarqube-build-breaker)

Breaks the build if the SonarQube quality gate of the project is red.

See [the blog post I wrote](https://blog.qaware.de/posts/sonar-qube-build-breaker/) for more details.

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

# Branch analysis

SonarQube supports two modes of branch analysis: 

Old versions of SonarQube supported appending the branch to the project key, e.g. if the project key is 
`de.qaware.tools.sonarqube-build-breaker:sonarqube-build-breaker` and you set `-Dsonar.branch=master` when running the analysis,
the project for the branch is `de.qaware.tools.sonarqube-build-breaker:sonarqube-build-breaker:master`.

If you have such projects, this is how you would run the Maven plugin:

```shell script
mvn --batch-mode --update-snapshots --non-recursive de.qaware.tools.sonarqube-build-breaker:sqbb-maven-plugin:sqbb -Dsqbb.sonarQubeUrl=$SONAR_URL -Dsqbb.sonarQubeToken=$SONAR_TOKEN -Dsqbb.branch=<branch> -Dsqbb.branchMode=projectKey
```

and the CLI:

```
java -jar cli.jar --branch=master --branch-mode=projectKey <your project key>
```

If you use newer versions of SonarQube, the `-Dsonar.branch` is deprecated and no longer works. You can either purchase a license if you want to support the
SonarQube developers or you can [install this plugin](https://github.com/mc1arke/sonarqube-community-branch-plugin).

Then you can use the `-Dsonar.branch.name` property when running the SonarQube analysis.

If you have such projects, this is how you would run the Maven plugin:

```shell script
mvn --batch-mode --update-snapshots --non-recursive de.qaware.tools.sonarqube-build-breaker:sqbb-maven-plugin:sqbb -Dsqbb.sonarQubeUrl=$SONAR_URL -Dsqbb.sonarQubeToken=$SONAR_TOKEN -Dsqbb.branch=<branch> -Dsqbb.branchMode=sonarQube
```

and the CLI:

```
java -jar cli.jar --branch=master --branch-mode=sonarQube <your project key>
```

# Changelog

See [this document](CHANGELOG.md).

# License

Licensed under [MIT](https://opensource.org/licenses/MIT), Copyright (c) 2019 - 2021 QAware GmbH

# Contributors

[See this page](https://github.com/qaware/sonarqube-build-breaker/graphs/contributors).

# Maintainer

Moritz Kammerer (moritz.kammerer@qaware.de | [@phxql](https://github.com/phxql/))
