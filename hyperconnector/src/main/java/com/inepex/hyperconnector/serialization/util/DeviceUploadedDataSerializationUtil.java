package com.inepex.hyperconnector.serialization.util;

public class DeviceUploadedDataSerializationUtil {

    // precalculated values for the powers of 256
    private static final long powersOf256AsLong[] = new long[]{(long) Math.pow(256, 0),
                                                               (long) Math.pow(256, 1),
                                                               (long) Math.pow(256, 2),
                                                               (long) Math.pow(256, 3),
                                                               (long) Math.pow(256, 4),
                                                               (long) Math.pow(256, 5),
                                                               (long) Math.pow(256, 6),
                                                               (long) Math.pow(256, 7)};

    // shift "0123456789ABCDEF" to "ABCDEFGHIJKLMNOP" in order to allow string
    // sorting to work as numerical sorting (provided leading "zeroes" are added
    // to the numbers)
    private static final char[] shiftedHexChars = {'A',
                                                   'B',
                                                   'C',
                                                   'D',
                                                   'E',
                                                   'F',
                                                   'G',
                                                   'H',
                                                   'I',
                                                   'J',
                                                   'K',
                                                   'L',
                                                   'M',
                                                   'N',
                                                   'O',
                                                   'P'};

    /**
     * Convert a long to a byte array in a way that keeps growing order. Works only for value >= 0!
     */
    public static byte[] orderKeepingBytesFromLong(long value, boolean invertBytes) {
        byte[] bytes = new byte[8];

        if (value >= 0) {
            bytes[0] = (byte) (value / powersOf256AsLong[7]);
            bytes[1] = (byte) (value / powersOf256AsLong[6]);
            bytes[2] = (byte) (value / powersOf256AsLong[5]);
            bytes[3] = (byte) (value / powersOf256AsLong[4]);
            bytes[4] = (byte) (value / powersOf256AsLong[3]);
            bytes[5] = (byte) (value / powersOf256AsLong[2]);
            bytes[6] = (byte) (value / powersOf256AsLong[1]);
            bytes[7] = (byte) (value / powersOf256AsLong[0]);
        }

        if (invertBytes) {
            for (int i = 0; i < 8; i++) {
                bytes[i] = (byte) ~bytes[i];
            }
        }

        return bytes;
    }

    public static long longFromOrderKeepingBytes(byte[] bytes, int offset, boolean invertBytes) {
        long value = 0;

        if (bytes == null) {
            return value;
        }

        if (bytes.length < (offset + 8)) {
            return value;
        }

        byte[] valueBytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            valueBytes[i] = bytes[offset + i];
        }

        if (invertBytes) {
            for (int i = 0; i < 8; i++) {
                valueBytes[i] = (byte) ~valueBytes[i];
            }
        }

        value += (long) (powersOf256AsLong[7] * byteValueInInt(valueBytes[0]));
        value += (long) (powersOf256AsLong[6] * byteValueInInt(valueBytes[1]));
        value += (long) (powersOf256AsLong[5] * byteValueInInt(valueBytes[2]));
        value += (long) (powersOf256AsLong[4] * byteValueInInt(valueBytes[3]));
        value += (long) (powersOf256AsLong[3] * byteValueInInt(valueBytes[4]));
        value += (long) (powersOf256AsLong[2] * byteValueInInt(valueBytes[5]));
        value += (long) (powersOf256AsLong[1] * byteValueInInt(valueBytes[6]));
        value += (long) (powersOf256AsLong[0] * byteValueInInt(valueBytes[7]));

        return value;
    }

    public static String shiftedHexStringFromBytes(byte[] bytes) {
        StringBuilder sB = new StringBuilder();

        if (bytes == null) {
            return sB.toString();
        }

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
        if (str == null) {
            return new byte[0];
        }

        if ((str.length() % 2) != 0) {
            str = shiftedHexChars[0] + str;
        }

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
     * Byte in java cannot be unsigned. This means that value between 128 and 255 will be a
     * neagative value. So with bit masking we make it to an unsigned int.
     */
    private static int byteValueInInt(byte b) {
        int val = b & 0x80;
        val += b & 0x7F;

        return val;
    }
}
