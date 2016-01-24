package dtos;


/**
 * The Class KeyValue.
 *
 * @param <T> the generic type
 */
public class KeyValue<T> {
    
    /** The key. */
    private String key;
    
    /** The value. */
    private T value;

    /**
     * Instantiates a new key value.
     *
     * @param key the key
     * @param value the value
     */
    public KeyValue(String key, T value) {
        super();
        this.key = key;
        this.value = value;
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
     * Sets the key.
     *
     * @param key the new key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(T value) {
        this.value = value;
    }


}
