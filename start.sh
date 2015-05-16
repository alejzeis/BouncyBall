BOUNCY=""

echo "Searching for BouncyBall jar..."

if [ -f ./BouncyBall.jar ]; then
	BOUNCY="./BouncyBall-1.0-SNAPSHOT.jar"
	echo "Found BouncyBall jar."
elif [ -f ./target/BouncyBall-1.0-SNAPSHOT.jar ]; then
	BOUNCY="./target/BouncyBall-1.0-SNAPSHOT.jar"
	echo "Found BouncyBall jar."
else
	echo "Could not find BouncyBall-1.0-SNAPSHOT.jar, perhaps it does not exist?"
	exit 1
fi

java -jar -Xmx256m -Xms256m $BOUNCY
