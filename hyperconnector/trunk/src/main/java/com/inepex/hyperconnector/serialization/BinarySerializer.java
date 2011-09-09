package com.inepex.hyperconnector.serialization;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TMemoryBuffer;

import com.inepex.hyperconnector.serialization.util.SerializationUtil;

/**
 * Binary serializer based on Thrift 0.6.0. serialization methods and classes, slightly modified to suit HyperConnector's needs.
 * @author Gabor Dicso
 *
 */
public class BinarySerializer implements HyperSerializer {

	private static final String serFailedMsg = "BinarySerializer: serialization failed.";
	private static final String deserFailedMsg = "BinarySerializer: deserialization failed.";

	private TMemoryBuffer tmb = null;
	private TBinaryProtocol prot = null;

	public BinarySerializer() {
		init();
	}

	@Override
	public byte[] getValue() {
		return tmb.getArray();
	}

	@Override
	public void setValue(byte[] value) {
		tmb.write(value, 0, value.length);
	}

	@Override
	public String getValueAsString() {
		return SerializationUtil.byteArrayToString(getValue());
	}

	@Override
	public void setValueAsString(String strValue) {
		setValue(SerializationUtil.stringToByteArray(strValue));
	}

	@Override
	public String getValueAsNonNullCharString() {
		return SerializationUtil.byteArrayToString(SerializationUtil.byteArrayToNonNullCharByteArray(getValue()));
	}

	@Override
	public void setValueAsNonNullCharString(String strValue) {
		setValue(SerializationUtil.nonNullCharByteArrayToByteArray(SerializationUtil.stringToByteArray(strValue)));
	}

	private void init() {
		tmb = new TMemoryBuffer(0);
		prot = new TBinaryProtocol(tmb);
	}

	@Override
	public void reset() {
		// following code is not optimal for resetting the state, it involves creating new instances upon each reset
		init();
	}

	private HyperSerializationException getHSE(Throwable e, String message) {
		HyperSerializationException hse = new HyperSerializationException(message);
		hse.initCause(e);
		return hse;
	}

	@Override
	public void writeboolean(boolean b) throws HyperSerializationException {
		try {
			prot.writeBool(b);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writebyte(byte b) throws HyperSerializationException {
		try {
			prot.writeByte(b);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeshort(short s) throws HyperSerializationException {
		try {
			prot.writeI16(s);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeint(int i) throws HyperSerializationException {
		try {
			prot.writeI32(i);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writelong(long l) throws HyperSerializationException {
		try {
			prot.writeI64(l);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writedouble(double d) throws HyperSerializationException {
		try {
			prot.writeDouble(d);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writefloat(float f) throws HyperSerializationException {
		try {
			prot.writeI32(Float.floatToIntBits(f));
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeBoolean(Boolean b) throws HyperSerializationException {
		try {
			boolean isNull = (b == null);
			prot.writeBool(isNull);
			if (!isNull)
				prot.writeBool(b);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeByte(Byte b) throws HyperSerializationException {
		try {
			boolean isNull = (b == null);
			prot.writeBool(isNull);
			if (!isNull)
				prot.writeByte(b);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeShort(Short s) throws HyperSerializationException {
		try {
			boolean isNull = (s == null);
			prot.writeBool(isNull);
			if (!isNull)
				prot.writeI16(s);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeInteger(Integer i) throws HyperSerializationException {
		try {
			boolean isNull = (i == null);
			prot.writeBool(isNull);
			if (!isNull)
				prot.writeI32(i);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeLong(Long l) throws HyperSerializationException {
		try {
			boolean isNull = (l == null);
			prot.writeBool(isNull);
			if (!isNull)
				prot.writeI64(l);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeDouble(Double d) throws HyperSerializationException {
		try {
			boolean isNull = (d == null);
			prot.writeBool(isNull);
			if (!isNull)
				prot.writeDouble(d);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeFloat(Float f) throws HyperSerializationException {
		try {
			boolean isNull = (f == null);
			prot.writeBool(isNull);
			if (!isNull)
				prot.writeI32(Float.floatToIntBits(f));
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public void writeString(String str) throws HyperSerializationException {
		try {
			boolean isNull = (str == null);
			prot.writeBool(isNull);
			if (!isNull)
				prot.writeString(str);
		} catch (TException e) {
			throw getHSE(e, serFailedMsg);
		}
	}

	@Override
	public boolean readboolean() throws HyperSerializationException {
		try {
			return prot.readBool();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public byte readbyte() throws HyperSerializationException {
		try {
			return prot.readByte();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public short readshort() throws HyperSerializationException {
		try {
			return prot.readI16();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public int readint() throws HyperSerializationException {
		try {
			return prot.readI32();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public long readlong() throws HyperSerializationException {
		try {
			return prot.readI64();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public double readdouble() throws HyperSerializationException {
		try {
			return prot.readDouble();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public float readfloat() throws HyperSerializationException {
		try {
			return Float.intBitsToFloat(prot.readI32());
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Boolean readBoolean() throws HyperSerializationException {
		try {
			boolean isNull = prot.readBool();
			if (isNull)
				return null;
			return prot.readBool();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Byte readByte() throws HyperSerializationException {
		try {
			boolean isNull = prot.readBool();
			if (isNull)
				return null;
			return prot.readByte();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Short readShort() throws HyperSerializationException {
		try {
			boolean isNull = prot.readBool();
			if (isNull)
				return null;
			return prot.readI16();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Integer readInteger() throws HyperSerializationException {
		try {
			boolean isNull = prot.readBool();
			if (isNull)
				return null;
			return prot.readI32();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Long readLong() throws HyperSerializationException {
		try {
			boolean isNull = prot.readBool();
			if (isNull)
				return null;
			return prot.readI64();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Double readDouble() throws HyperSerializationException {
		try {
			boolean isNull = prot.readBool();
			if (isNull)
				return null;
			return prot.readDouble();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public Float readFloat() throws HyperSerializationException {
		try {
			boolean isNull = prot.readBool();
			if (isNull)
				return null;
			return Float.intBitsToFloat(prot.readI32());
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}

	@Override
	public String readString() throws HyperSerializationException {
		try {
			boolean isNull = prot.readBool();
			if (isNull)
				return null;
			return prot.readString();
		} catch (TException e) {
			throw getHSE(e, deserFailedMsg);
		}
	}
}
