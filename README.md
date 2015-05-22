# BouncyBall ![Build Status](http://teamcity.beaconpe.net/app/rest/builds/buildType:BouncyBall_BouncyBuild/statusIcon)

A MCPE proxy, for use with PocketMine or other MCPE servers.

## How to use BouncyBall

You can download a pre-compiled STABLE JAR [here](http://teamcity.beaconpe.net/repository/download/BouncyBall_BouncyBuild/27:id/BouncyBall-1.0-SNAPSHOT.jar) (When prompted, click "login as guest" button).

If you want to compile from source (to get a latest build), read on:

You will need: Oracle JDK 8, and Apache Maven. If you don't know where to get them, use google. If you are on windows, you might need to add the JDK and maven bin folders to your PATH. After you installed them, just download a zip of the repo and then do:

```
mvn package
```

Running maven package will create a JAR for you in the "target" folder.

To run the software do:

```
java -jar BouncyBall-1.0-SNAPSHOT.jar
```

You can edit config.yml to your liking.

## Any Problems?
Contact me at jython234@blockserver.org, or on the BeaconPE forums: http://beaconpe.net/forums.
