# SonarQube Build Breaker CLI

This CLI checks the pending SonarQube analysis tasks for a given project. It waits until a pending analysis is done. It
then breaks the build if:

* the analysis failed (can happen if the database is corrupt, SonarQube issues, etc.)
* if the quality gate of the project is in error state (red)

A broken build is signalled with the exit code `1`. If everything is okay, the exit code is `0`.

## Usage

The CLI tool expects the URL to the SonarQube instance in an environment variable named `SONAR_URL` and a SonarQube access
token in the environment variable `SONAR_TOKEN`.

The key of the project which should be checked is provided as the last commandline argument.

You can enable debug mode by adding `--debug` at the end of the commandline.

```
usage: java -jar cli.jar <project key>
 -b,--branch <arg>         Sets the branch
 -bm,--branch-mode <arg>   Sets the branch mode. Supported modes:
                           projectKey, sonarQube. Default: projectKey
 -d,--debug                Enables debug mode
```

### Example

```
SONAR_URL="<url to the SonarQube instance>"
SONAR_TOKEN="<SonarQube access token>"

java -jar cli.jar "<key of your project>"
```

### Example with real values

```
SONAR_URL="http://localhost:9000/"
SONAR_TOKEN="343b743ff11569f1bff31a04cdb18bcbb3572e17"

java -jar cli.jar "de.qaware.tools.sonarqube-build-breaker:sonarqube-build-breaker"
```