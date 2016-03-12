package dtos;

import java.util.List;
import java.util.Set;

/**
 * Created by hdlopesrocha on 07-12-2015.
 */
public class PermissionDto {
	
	/** The key. */
	private String key;
	private Set<String> readSet;	// empty means all can read
	private Set<String> writeSet;	// empty means no one can write
	
	/**
	 * Instantiates a new attribute dto.
	 *
	 * @param key
	 *            the key
	 */
	public PermissionDto(String key, Set<String> readSet,Set<String> writeSet) {
		this.key = key;
		this.readSet = readSet;
		this.writeSet = writeSet;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	public Set<String> getReadSet() {
		return readSet;
	}

	public Set<String> getWriteSet() {
		return writeSet;
	}


}