# SonarQube Build Breaker Maven plugin

This maven plugin checks the pending SonarQube analysis tasks for a given project. It waits until a pending analysis is done. It
then breaks the build if:

* the analysis failed (can happen if the database is corrupt, SonarQube issues, etc.)
* if the quality gate of the project is in error state (red)

## Usage

Run it after your `mvn sonar:sonar` command:

```
mvn de.qaware.tools.sonarqube-build-breaker:sqbb-maven-plugin:sqbb -Dsqbb.sonarQubeUrl=... -Dsqbb.sonarQubeToken=...
```

### Goals

* `sqbb`: Run the SonarQube build breaker

### Configuration

* `sqbb.sonarQubeUrl`: URL to the SonarQube instance. Required.
* `sqbb.sonarQubeToken`: Authentication token for the SonarQube instance. Required.
* `sqbb.projectKey`: SonarQube project key. Defaults to `${project.groupId}:${project.artifactId}`
* `sqbb.branch`: If set, gets appended to the `projectKey`.
* `sqbb.branchMode`: Branch mode. Can be `projectKey` or `sonarQube`. Defaults to `projectKey`.

You can also configure the plugin like so:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>de.qaware.tools.sonarqube-build-breaker</groupId>
            <artifactId>sqbb-maven-plugin</artifactId>
            <version>INSERT_LATEST_VERSION_HERE</version>
            <configuration>
                <sonarQubeUrl>...</sonarQubeUrl>
                <sonarQubeToken>...</sonarQubeToken>
                <projectKey>...</projectKey>
                <branch>...</branch>
            </configuration>
        </plugin>
    </plugins>
</build>
```