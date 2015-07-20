package com.inepex.hyperconnector.serialization;

/**
 * Interface for serializing and deserializing HyperEntity values.
 * @author Gabor Dicso
 *
 */
public interface HyperSerializer {
	/**
	 * Returns the serialized value of the values written into the serializer as a byte array.
	 */
	byte[] getValue();
	/**
	 * Sets the serialized value as a byte array. Values can be read from the serializer
	 * afterwards in the order they were written into it.
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
	 * Returns the return value of getValue() as a specially encoded string that does not contain
	 * '\0' characters.
	 */
	String getValueAsNonNullCharString();
	/**
	 * Wraps setValue(byte[] value), allows setting the value as a specially encoded string that
	 * does not contain '\0' characters.
	 */
	void setValueAsNonNullCharString(String value);

	/**
	 * Resets the internal state of the serializer.
	 */
	void reset();

	boolean readboolean() throws HyperSerializationException;
	Boolean readBoolean() throws HyperSerializationException;
	byte readbyte() throws HyperSerializationException;
	Byte readByte() throws HyperSerializationException;
	double readdouble() throws HyperSerializationException;
	Double readDouble() throws HyperSerializationException;
	float readfloat() throws HyperSerializationException;
	Float readFloat() throws HyperSerializationException;
	int readint() throws HyperSerializationException;
	Integer readInteger() throws HyperSerializationException;
	long readlong() throws HyperSerializationException;
	Long readLong() throws HyperSerializationException;
	short readshort() throws HyperSerializationException;
	Short readShort() throws HyperSerializationException;
	String readString() throws HyperSerializationException;

	void writeboolean(boolean b) throws HyperSerializationException;
	void writeBoolean(Boolean b) throws HyperSerializationException;
	void writebyte(byte b) throws HyperSerializationException;
	void writeByte(Byte b) throws HyperSerializationException;
	void writedouble(double d) throws HyperSerializationException;
	void writeDouble(Double d) throws HyperSerializationException;
	void writefloat(float f) throws HyperSerializationException;
	void writeFloat(Float f) throws HyperSerializationException;
	void writeint(int i) throws HyperSerializationException;
	void writeInteger(Integer i) throws HyperSerializationException;
	void writelong(long l) throws HyperSerializationException;
	void writeLong(Long l) throws HyperSerializationException;
	void writeshort(short s) throws HyperSerializationException;
	void writeShort(Short s) throws HyperSerializationException;
	void writeString(String str) throws HyperSerializationException;
}
