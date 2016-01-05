package dtos;

/**
 * Created by hdlopesrocha on 07-12-2015.
 */
public class AttributeDto {
    public boolean isIdentifiable() {
        return identifiable;
    }

    public boolean isSearchable() {
        return searchable;
    }

    private final boolean identifiable;
    private final boolean searchable;


    private String key;
    private Object value;

    public AttributeDto(String key, Object value,boolean identifiable,boolean searchable) {
        this.key = key;
        this.value = value;

        this.identifiable = identifiable;
        this.searchable = searchable;
    }


	public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }


}
