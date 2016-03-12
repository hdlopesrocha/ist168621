package dtos;


/**
 * Created by hdlopesrocha on 07-12-2015.
 */
public class AttributeDto {
    
    /** The identifiable. */
    private final boolean identifiable;
    
    /** The searchable. */
    private final boolean searchable;
    
    /** The aggregator. */
    private final boolean aggregator;
    
    /** The key. */
    private String key;
    
    /** The value. */
    private Object value;



    /**
     * Instantiates a new attribute dto.
     *
     * @param key the key
     * @param value the value
     * @param identifiable the identifiable
     * @param searchable the searchable
     * @param aggregator the aggregator
     */
    public AttributeDto(String key, Object value, boolean identifiable, boolean searchable, boolean aggregator) {
        this.key = key;
        this.value = value;
        this.identifiable = identifiable;
        this.searchable = searchable;
        this.aggregator = aggregator;
    }

    /**
     * Checks if is identifiable.
     *
     * @return true, if is identifiable
     */
    public boolean isIdentifiable() {
        return identifiable;
    }

    /**
     * Checks if is searchable.
     *
     * @return true, if is searchable
     */
    public boolean isSearchable() {
        return searchable;
    }

    /**
     * Checks if is aggregator.
     *
     * @return true, if is aggregator
     */
    public boolean isAggregator() {
        return aggregator;
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
     * Gets the value.
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * The Enum Access.
     */
    public enum Access {/** The read. */
READ, /** The write. */
 WRITE, /** The none. */
 NONE}



}