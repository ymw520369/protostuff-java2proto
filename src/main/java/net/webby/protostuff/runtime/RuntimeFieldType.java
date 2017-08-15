package net.webby.protostuff.runtime;

/**
 * 
 * @author Alex Shvid
 *
 */


public enum RuntimeFieldType {
	RuntimeRepeatedField,
	RuntimeMessageField,
	RuntimeObjectField,
	RuntimeMapField;
	
	public static RuntimeFieldType findByName(String name) {
		for (RuntimeFieldType value : values()) {
			if (name.startsWith(value.name())) {
				return value;
			}
		}
		return null;
	}
	
}
