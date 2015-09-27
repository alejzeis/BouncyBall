package io.github.jython234.BouncyBall.utility;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A Utility class for easily managing YAML configuration files.
 * <br>
 * Based on https://github.com/RedstoneLamp/RedstoneLamp/blob/rewrite/src/main/java/net/redstonelamp/config/YamlConfig.java
 *
 * @author RedstoneLamp Team and jython234
 */
public class YamlConfiguration {
    private Map<String, Object> map;

    @SuppressWarnings("unchecked")
    public YamlConfiguration(File location) {
        Yaml yml = new Yaml();
        try {
            map = (Map<String, Object>) yml.load(new FileInputStream(location));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapInMap(String key, Map<String, Object> map) {
        Object value = map.get(key);
        if(!(value instanceof Map)){
            throw new RuntimeException("Value is not in map!");
        }
        return (Map<String, Object>) value;
    }

    /**
     * Gets an Object from the configuration with the specified <code>path</code>.
     * The Path is separated by (.). For example, if I want to get the value of myValue inside of
     * myConfig, the path would be "myConfig.myValue"
     *
     * @param path The Path of the value
     * @return The value as an Object if found, null if not.
     */
    public Object get(String path){
        if(path.indexOf('.') == -1){ //Check if its a root element
            return map.get(path);
        }
        String[] splitPath = path.split(Pattern.quote("."));
        Map<String, Object> map = this.map;
        for(int i = 0; i < splitPath.length - 1; i++){
            String element = splitPath[i];
            try{
                map = getMapInMap(element, map);
            }catch(Exception e){
                return null;
            }
        }
        return map.get(splitPath[splitPath.length - 1]);
    }

    @SuppressWarnings("unchecked")
    public void putString(String path, String s){ //TODO: TEST THIS
        if(path.indexOf('.') == -1){ //Check if its a root element
            map.put(path, s);
            return;
        }
        String[] splitPath = path.split(Pattern.quote("."));
        Map<String, Object> map = this.map;
        Map[] maps = new Map[splitPath.length];
        for(int i = 0; i < splitPath.length - 1; i++){
            String element = splitPath[i];
            try{
                map = getMapInMap(element, map);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        map.put(splitPath[splitPath.length - 1], s);
        for(int i = splitPath.length - 1; i > 0; i--){
            putMapInMap(splitPath[i], map, maps[i - 1]);
        }
    }

    public String getString(String path){
        return (String) get(path);
    }

    public boolean getBoolean(String path){
        return (boolean) get(path);
    }

    public int getInt(String path){
        return (int) get(path);
    }

    /**
     * Returns a Map of the YAML file
     *
     * @return
     */
    public Map<String, Object> getMap(){
        return map;
    }

    /**
     * Returns a Map of a Map in the YAML file
     *
     * @param mapName
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getInMap(String mapName){
        Object value = map.get(mapName);
        if(value instanceof Map){
            return (Map<String, Object>) value;
        }
        return map;
    }

    public void putMapInMap(String key, Map<String, Object> mapToPut, Map<String, Object> map){
        map.put(key, mapToPut);
    }
}
