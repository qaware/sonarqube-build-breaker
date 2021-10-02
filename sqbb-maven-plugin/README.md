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

* `sqbb.skip`: Skip. If `true`, the plugin will do nothing.
* `sqbb.sonarQubeUrl`: URL to the SonarQube instance. If not set, falls back to `sonar.host.url`. Defaults to `http://localhost:9000`.
* `sqbb.sonarQubeToken`: Authentication token for the SonarQube instance. If not set, falls back to `sonar.login`.
* `sqbb.sonarQubeUsername`: Authentication username for the SonarQube instance. If not set, falls back to `sonar.login`.
* `sqbb.sonarQubePassword`: Authentication password for the SonarQube instance. If not set, falls back to `sonar.password`.
* `sqbb.projectKey`: SonarQube project key. Defaults to `sonar.projectKey` if this key exists or to `${project.groupId}:${project.artifactId}`.
* `sqbb.branch`: If set, gets appended to the `projectKey`. Falls back to `sonar.branch.name`.
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
                <sonarUsername>...</sonarUsername>
                <sonarPassword>...</sonarPassword>
                <projectKey>...</projectKey>
                <branch>...</branch>
            </configuration>
        </plugin>
    </plugins>
</build>
```