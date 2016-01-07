package models;

import java.io.File;

public class KeyValueFile {
    private final String key;
    private final String name;
    private final File value;

    public KeyValueFile(String key, String name, File value) {
        super();
        this.key = key;
        this.value = value;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public File getValue() {
        return value;
    }


}
