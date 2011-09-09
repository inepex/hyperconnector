package com.inepex.hyperconnector.serialization;

import com.inepex.hyperconnector.serialization.util.SerializationUtil;

/**
 * Performs plain text serialization and deserialization. NOTE: '\0' characters are removed from
 * string representation and thus will be lost. It's also impossible to separate values if more
 * than one of them are added to the serializer and this can cause data loss or corruption, or
 * even deserialization error. Use with care, use only for serializing and deserializing exactly
 * one field.
 * @author Gabor Dicso
 *
 */
public class PlainTextSerializer implements HyperSerializer {

	private static final String serFailedMsg = "PlainTextSerializer: serialization failed.";
	private static final String deserFailedMsg = "PlainTextSerializer: deserialization failed.";

	private String strValue = "";

	/**
	 * Should not be used with PlainTextSerializer, not optimal.
	 */
	@Override
	public byte[] getValue() {
		return SerializationUtil.stringToByteArray(strValue.replace("\0", ""));
	}

	/**
	 * Should not be used with PlainTextSerializer, not optimal.
	 */
	@Override
	public void setValue(byte[] value) {
		strValue = SerializationUtil.byteArrayToString(value);
	}

	@Override
	public String getValueAsString() {
		if (strValue == null)
			return null;
		return strValue.replace("\0", "");
	}

	@Override
	public void setValueAsString(String value) {
		this.strValue = value;
	}

	@Override
	public String getValueAsNonNullCharString() {
		if (strValue == null)
			return null;
		return strValue.replace("\0", "");
	}

	@Override
	public void setValueAsNonNullCharString(String value) {
		this.strValue = value; // nothing special to do
	}

	@Override
	public void reset() {
		strValue = "";
	}

	private HyperSerializationException getHSE(Throwable e, String message) {
		HyperSerializationException hse = new HyperSerializationException(message);
		hse.initCause(e);
		return hse;
	}

	@Override
	public boolean readboolean() throws HyperSerializationException {
		try {
			return Boolean.parseBoolean(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Boolean readBoolean() throws HyperSerializationException {
		try {
			return Boolean.parseBoolean(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public byte readbyte() throws HyperSerializationException {
		try {
			return SerializationUtil.charToByte(strValue.charAt(0));
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Byte readByte() throws HyperSerializationException {
		try {
			return SerializationUtil.charToByte(strValue.charAt(0));
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public double readdouble() throws HyperSerializationException {
		try {
			return Double.parseDouble(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Double readDouble() throws HyperSerializationException {
		try {
			return Double.parseDouble(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public float readfloat() throws HyperSerializationException {
		try {
			return Float.parseFloat(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Float readFloat() throws HyperSerializationException {
		try {
			return Float.parseFloat(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public int readint() throws HyperSerializationException {
		try {
			return Integer.parseInt(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Integer readInteger() throws HyperSerializationException {
		try {
			return Integer.parseInt(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public long readlong() throws HyperSerializationException {
		try {
			return Long.parseLong(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Long readLong() throws HyperSerializationException {
		try {
			return Long.parseLong(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public short readshort() throws HyperSerializationException {
		try {
			return Short.parseShort(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Short readShort() throws HyperSerializationException {
		try {
			return Short.parseShort(strValue);
		}
		catch (Throwable e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public String readString() throws HyperSerializationException {
		return strValue;
	}

	@Override
	public void writeboolean(boolean b) throws HyperSerializationException {
		try {
			strValue += b;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeBoolean(Boolean b) throws HyperSerializationException {
		try {
			if (b != null)
				strValue += b.toString();
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writebyte(byte b) throws HyperSerializationException {
		try {
			strValue += SerializationUtil.byteToChar(b);
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeByte(Byte b) throws HyperSerializationException {
		try {
			if (b != null)
				strValue += SerializationUtil.byteToChar(b);
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writedouble(double d) throws HyperSerializationException {
		try {
			strValue += d;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeDouble(Double d) throws HyperSerializationException {
		try {
			if (d != null)
				strValue += d;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writefloat(float f) throws HyperSerializationException {
		try {
			strValue += f;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeFloat(Float f) throws HyperSerializationException {
		try {
			if (f != null)
				strValue += f;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeint(int i) throws HyperSerializationException {
		try {
			strValue += i;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeInteger(Integer i) throws HyperSerializationException {
		try {
			if (i != null)
				strValue += i;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writelong(long l) throws HyperSerializationException {
		try {
			strValue += l;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeLong(Long l) throws HyperSerializationException {
		try {
			if (l != null)
				strValue += l;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeshort(short s) throws HyperSerializationException {
		try {
			strValue += s;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeShort(Short s) throws HyperSerializationException {
		try {
			if (s != null)
				strValue += s;
		}
		catch (Throwable e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeString(String str) throws HyperSerializationException {
		if (str != null)
			strValue += str;
	}
}
