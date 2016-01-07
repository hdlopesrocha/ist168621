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

    public enum Access {READ,WRITE,NONE}
	public enum Visibility {PUBLIC, PRIVATE}
	
	private final Visibility visibility;
    private final String key;
    private final Object value;
    private final Access access;

    public AttributeDto(String key, Object value, Access access, Visibility visibility, boolean identifiable, boolean searchable) {
        this.key = key;
        this.value = value;
        this.visibility = visibility;
        this.access = access;
        this.identifiable = identifiable;
        this.searchable = searchable;
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
