if exist target\BouncyBall-1.0-SNAPSHOT.jar (
  set JAR=target\BouncyBall-1.0-SNAPSHOT.jar
) else (
  set JAR=BouncyBall-1.0-SNAPSHOT.jar
)

java -jar -Xmx256m -Xms256m %JAR%
