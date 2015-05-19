package net.beaconpe.bouncyball.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Player Database to store player data. (What server last on, etc).
 */
public class PlayerDataBase {
    public final static byte DB_VERSION = 1;
    private ArrayList<DatabaseEntry> entries = new ArrayList<>();
    private File databaseFile;

    public PlayerDataBase(File location) throws IOException {
        databaseFile = location;
        if(databaseFile.exists()){
            reload();
        } else {
            createNew();
        }
    }

    private void createNew() throws IOException {
        databaseFile.createNewFile();
        DataOutputStream out = new DataOutputStream(new FileOutputStream(databaseFile));
        out.write(DB_VERSION);
        out.writeInt(0); //Entries
        out.close();
    }

    public void reload() {

    }

    public static class DatabaseEntry {
        public String lastServer;
    }
}
