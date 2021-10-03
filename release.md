# Release process

1. `mvn versions:set -DgenerateBackupPoms=false -DremoveSnapshot`
2. Update version in changelog
3. `git commit -am "Release version $NEW_VERSION"`
4. `mvn clean deploy -P release`
5. Check the staging repo on [OSSRH](https://oss.sonatype.org/#stagingRepositories)
6. If everything is fine, close the repo: `mvn nexus-staging:release -P release` ([Details](https://central.sonatype.org/publish/publish-maven/))
7. `git tag version-$NEW_VERSION`
8. `mvn versions:set -DgenerateBackupPoms=false -DnextSnapshot=true`
9. `git commit -am "Start development on next version"`
10. `git push && git push --tags`
