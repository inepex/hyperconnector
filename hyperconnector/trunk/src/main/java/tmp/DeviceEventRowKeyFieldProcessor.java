package tmp;

import com.inepex.hyperconnector.serialization.FieldProcessor;
import com.inepex.hyperconnector.serialization.util.SerializationUtil;

public class DeviceEventRowKeyFieldProcessor implements FieldProcessor {
	// precalculated values for 256's pows
	private static final long bytepow[] = new long[] {
		(long) Math.pow(256, 0),
		(long) Math.pow(256, 1),
		(long) Math.pow(256, 2),
		(long) Math.pow(256, 3),
		(long) Math.pow(256, 4),
		(long) Math.pow(256, 5),
		(long) Math.pow(256, 6),
		(long) Math.pow(256, 7)
		};
	
	
	
	// shift "0123456789ABCDEF" to "ABCDEFGHIJKLMNOP" in order to allow string
	// sorting to work as numerical sorting (provided leading "zeroes" are added
	// to the numbers)
	private static final char[] shiftedHexChars = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P'
		};

	/**
	 * Convert a long to a byte array in a way that keeps growing order. Works
	 * only for value >= 0!
	 */
	public static byte[] orderKeepingBytesFromLong(long value, boolean invertBytes) {
		byte[] bytes = new byte[8];

		if (value >= 0) {
			bytes[0] = (byte) (value / bytepow[7]);
			bytes[1] = (byte) (value / bytepow[6]);
			bytes[2] = (byte) (value / bytepow[5]);
			bytes[3] = (byte) (value / bytepow[4]);
			bytes[4] = (byte) (value / bytepow[3]);
			bytes[5] = (byte) (value / bytepow[2]);
			bytes[6] = (byte) (value / bytepow[1]);
			bytes[7] = (byte) (value / bytepow[0]);
		}

		if (invertBytes) {
			for(int i = 0; i < 8; i++)
				bytes[i] = (byte) ~bytes[i];
		}

		return bytes;
	}
	
	

	public static long longFromOrderKeepingBytes(byte[] bytes, int offset, boolean invertBytes) {
		long value = 0;

		if (bytes == null)
			return value;

		if (bytes.length < (offset + 8))
			return value;

		byte[] valueBytes = new byte[8];

		for (int i = 0; i < 8; i++)
			valueBytes[i] = bytes[offset + i];

		if (invertBytes) {
			for(int i = 0; i < 8; i++)
				valueBytes[i] = (byte) ~valueBytes[i];
		}

		value += (long) (bytepow[7] * byteValueInInt(valueBytes[0]));
		value += (long) (bytepow[6] * byteValueInInt(valueBytes[1]));
		value += (long) (bytepow[5] * byteValueInInt(valueBytes[2]));
		value += (long) (bytepow[4] * byteValueInInt(valueBytes[3]));
		value += (long) (bytepow[3] * byteValueInInt(valueBytes[4]));
		value += (long) (bytepow[2] * byteValueInInt(valueBytes[5]));
		value += (long) (bytepow[1] * byteValueInInt(valueBytes[6]));
		value += (long) (bytepow[0] * byteValueInInt(valueBytes[7]));

		return value;
	}

	public static String shiftedHexStringFromBytes(byte[] bytes) {
		StringBuilder sB = new StringBuilder();

		if (bytes == null)
			return sB.toString();

		for (byte b : bytes) {
			int ub = byteValueInInt(b);

			int firstHex = ub / 16;
			int secondHex = ub % 16;

			sB.append(shiftedHexChars[firstHex]);
			sB.append(shiftedHexChars[secondHex]);
		}

		return sB.toString();
	}

	
	
	public static byte[] bytesFromShiftedHexString(String str) {
		if (str == null)
			return new byte[0];

		if ((str.length() % 2) != 0)
			str = shiftedHexChars[0] + str;

		byte[] bytes = new byte[str.length() / 2];

		try {
			char[] ca_str = str.toCharArray();

			for (int i = 0; i < str.length(); i += 2) {

				int firstHex = getCharPosInShiftedHexChars(ca_str[i]);
				int secondHex = getCharPosInShiftedHexChars(ca_str[i + 1]);

				bytes[i / 2] = (byte) (firstHex * 16 + secondHex);
			}
		} catch (Exception ex) {
			// error: return 0 bytes
			bytes = new byte[str.length() / 2];
		}

		return bytes;
	}

	
	
	private static int getCharPosInShiftedHexChars(char c) {
		for (int i = 0; i < shiftedHexChars.length; i++) {
			if (shiftedHexChars[i] == c) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Byte in java cannot be unsigned. This means that value between 128 and
	 * 255 will be a neagative value. So with bit masking we make it to an
	 * unsigned int.
	 */
	public static int byteValueInInt(byte b) {
		int val = b & 0x80;
		val += b & 0x7F;

		return val;
	}
	
	private long deviceId = 0L;
	private long creationTimestamp = 0L;

	public DeviceEventRowKeyFieldProcessor() {
	}

	@Override
	public byte[] getValue() {
		byte[] value = new byte[16];

		byte[] deviceIdBytes = orderKeepingBytesFromLong(deviceId, false);
		byte[] creationTimestampBytes = orderKeepingBytesFromLong(creationTimestamp, true);
		if (deviceIdBytes.length != 8 || creationTimestampBytes.length != 8)
			return value; // TODO: throw?
		for (int i = 0; i < 8; i++)
			value[i] = deviceIdBytes[i];
		for (int i = 0; i < 8; i++)
			value[i + 8] = creationTimestampBytes[i];

		return SerializationUtil.stringToByteArray(shiftedHexStringFromBytes(value));
	}

	@Override
	public void setValue(byte[] value) {
		if (value == null)
			return; // TODO: throw?
		byte[] valueBytes = bytesFromShiftedHexString(SerializationUtil.byteArrayToString(value));
		if (valueBytes.length != 16)
			return; // TODO: throw?
		byte[] deviceIdBytes = new byte[8];
		byte[] creationTimestampBytes = new byte[8];
		for (int i = 0; i < 8; i++)
			deviceIdBytes[i] = valueBytes[i];
		for (int i = 0; i < 8; i++)
			creationTimestampBytes[i] = valueBytes[i + 8];
		
		deviceId = longFromOrderKeepingBytes(deviceIdBytes, 0, false);
		creationTimestamp = longFromOrderKeepingBytes(creationTimestampBytes, 0, true);
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
		// value won't contain null chars anyway
		return getValueAsString();
	}

	@Override
	public void setValueAsNonNullCharString(String strValue) {
		// value won't contain null chars anyway
		setValueAsString(strValue);
	}

	public long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	@Override
	public void reset() {
		// do anything?
	}
}