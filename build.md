Building and distributing TestFun-JEE
=====================================

Snapshot
--------
In order to publish a snapshot build, invoke the ```deploy``` goal using the "release" profile.

Release
-------
1. Create GPG keys for signing the artifacts being uploaded:
 * A GPG client is installed on your command line path. For more information, please refer to http://www.gnupg.org/.
 * You have created your GPG keys and distributed your public key to hkp://pool.sks-keyservers.net/. For more information, please refer to How To Generate PGP Signatures With Maven.
2. Build TestFun-JEE using the "release" profile. This will produce JAVADOC and SRC JAR files.
3. Issue the following commands (using the correct version)
