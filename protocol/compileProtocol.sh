echo "Compiling Protocol for BouncyBall!"
protoc -I=. --java_out=.\build BouncyP2P.proto
#TODO: Move files to SRC
echo "Complete!"