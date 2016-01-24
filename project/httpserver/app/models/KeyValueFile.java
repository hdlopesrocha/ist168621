package models;

import java.io.File;


/**
 * The Class KeyValueFile.
 */
public class KeyValueFile {
    
    /** The key. */
    private final String key;
    
    /** The name. */
    private final String name;
    
    /** The value. */
    private final File value;

    /**
     * Instantiates a new key value file.
     *
     * @param key the key
     * @param name the name
     * @param value the value
     */
    public KeyValueFile(String key, String name, File value) {
        super();
        this.key = key;
        this.value = value;
        this.name = name;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public File getValue() {
        return value;
    }


}
