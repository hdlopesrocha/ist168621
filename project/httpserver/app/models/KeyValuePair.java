package models;


/**
 * The Class KeyValuePair.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class KeyValuePair<K, V> {
    
    /** The key. */
    private final K key;
    
    /** The value. */
    private final V value;

    /**
     * Instantiates a new key value pair.
     *
     * @param key the key
     * @param value the value
     */
    public KeyValuePair(K key, V value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public V getValue() {
        return value;
    }


}
