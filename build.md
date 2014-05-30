Building and distributing TestFun-JEE
=====================================

Snapshot
--------
In order to publish a snapshot build, invoke the ```deploy``` goal using the "release" profile.

Release
-------
The full instructions for releasing a new version can be found in https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide.

1. Create GPG keys for signing the artifacts being uploaded:
    * A GPG client is installed on your command line path. For more information, please refer to http://www.gnupg.org/.
    * You have created your GPG keys and distributed your public key to http://pool.sks-keyservers.net/. For more information, please refer to How To Generate PGP Signatures With Maven.

2. Update version in POM - e.g. from 0.8-SNAPSHOT to 0.9.
3. Build TestFun-JEE using the "release" profile. This will produce JAVADOC and SRC JAR files: ```mvn clean install -P release```
4. Push to main. Update version in POM again - e.g. from 0.9 to 0.9-SNAPSHOT.
5. Issue the following commands (using the correct version) in order to stage the artifacts:

```
path=%path%;C:\Program Files (x86)\GNU\GnuPG;C:\Program Files (x86)\Git\bin
mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=target\jee-0.9.pom -Dfile=target\jee-0.9.jar
mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=target\jee-0.9.pom -Dfile=target\jee-0.9-sources.jar -Dclassifier=sources
mvn gpg:sign-and-deploy-file -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ -DrepositoryId=sonatype-nexus-staging -DpomFile=target\jee-0.9.pom -Dfile=target\jee-0.9-javadoc.jar -Dclassifier=javadoc
```

6. Release the artifacts as described in the documentation.
    1. Login to the Nexus UI at https://oss.sonatype.org.
    2. Go to Staging Repositories page.
    3. Select a staging repository.  The staging repository name should look like your groupId without punctuation followed by a number (orgmygroupid-1000).  If you just finished performing your release, it may take a minute or two for the staging repository to appear on the Staging Repositories page.  Also, if there is a long listing of staging repositories with names like central_bundles-*, make sure that you scroll through the listing to find one that bears a name resembling your groupId.
    4. Click the Close button.

