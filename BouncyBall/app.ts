import BouncyBallProxy from "./BouncyBallProxy"

var proxy = new BouncyBallProxy("play.lbsg.net", 19132, "0.0.0.0", 19132);
proxy.getLogger().info("Starting BouncyBallProxy...");
proxy.getLogger().warn("Starting BouncyBallProxy...");
proxy.getLogger().error("Starting BouncyBallProxy...");
proxy.run();