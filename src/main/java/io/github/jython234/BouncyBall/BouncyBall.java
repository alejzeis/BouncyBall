package io.github.jython234.BouncyBall;

import io.github.jython234.BouncyBall.utility.YamlConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The main run class for BouncyBall.
 *
 * @author jython234
 */
public class BouncyBall {
    public static final String VERSION = "1.0-SNAPSHOT";

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger("BouncyBall");
        checkFiles();
        BouncyBallProxy proxy = new BouncyBallProxy(logger, new YamlConfiguration(new File("bouncyball.yml")));
        proxy.start();
    }

    private static void checkFiles() {
        if(!new File("bouncyball.yml").isFile()) {
            try {
                FileUtils.copyInputStreamToFile(ClassLoader.getSystemResourceAsStream("conf/defaultConfig.yml"), new File("bouncyball.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
