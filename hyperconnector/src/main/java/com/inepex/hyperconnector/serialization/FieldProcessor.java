package com.inepex.hyperconnector.serialization;

/**
 * <p>Interface for a custom FieldProcessor for a given HyperEntity type. The usage of a
 * FieldProcessor is similar to that of a HyperSerializer, but FieldProcessor
 * instances serve for storing values in Hypertable cells (row key, column qualifier,
 * cell value) in a format that is specific to the given HyperEntity type. This means
 * that the FieldProcessor's serialization and deserialization logic must be
 * implemented specifically for the given HyperEntity type.</p>
 * <p>When serializing, HyperEntity field values are typically handed over to their
 * setters in the custom FieldProcessor, and the serialized value containing the values
 * of the HyperEntity fields is then read through the desired FieldProcessor value
 * getter. When deserializing, the previously serialized FieldProcessor value is handed
 * over to the appropriate FieldProcessor value setter, and the deserialized HyperEntity
 * field values are read through their getters in the custom FieldProcessor.</p> 
 * @author Gabor Dicso
 *
 */
public interface FieldProcessor {
	/**
	 * Returns the value of the FieldProcessor in a byte array.
	 */
	byte[] getValue();
	/**
	 * Sets the value of the FieldProcessor in a byte array.
	 */
	void setValue(byte[] value);
	/**
	 * Returns the return value of getValue() as a string.
	 */
	String getValueAsString();
	/**
	 * Wraps setValue(byte[] value), allows setting the value as a string.
	 */
	void setValueAsString(String value);
	/**
	 * Returns the return value of getValue() as a specially encoded string that does not contain '\0' characters.
	 */
	String getValueAsNonNullCharString();
	/**
	 * Wraps setValue(byte[] value), allows setting the value as a specially encoded string that does not contain '\0' characters.
	 */
	void setValueAsNonNullCharString(String value);
	/**
	 * Resets the internal state of the FieldProcessor.
	 */
	void reset();
}
