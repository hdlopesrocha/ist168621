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
    private final boolean aggregator;

    public enum Access {READ,WRITE,NONE};
    public enum Visibility {PUBLIC, PRIVATE};

    private Visibility visibility;
    private String key;
    private Object value;
    private Access access;

    public AttributeDto(String key, Object value, Access access,Visibility visibility,boolean identifiable,boolean searchable,boolean aggregator) {
        this.key = key;
        this.value = value;
        this.visibility = visibility;
        this.access = access;
        this.identifiable = identifiable;
        this.searchable = searchable;
        this.aggregator = aggregator;
    }

    public boolean isAggregator() {
        return aggregator;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Access getAccess() {
        return access;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }


}