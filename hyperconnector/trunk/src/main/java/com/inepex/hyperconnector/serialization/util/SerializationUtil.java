package com.inepex.hyperconnector.serialization.util;

import java.util.HashMap;
import java.util.Map;


public class SerializationUtil {
	public static Map<Integer, Integer> bitSetToMap(IneBitSet bS) {
		if (bS == null)
			return null;
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		int bSLen = bS.getRealSize();
		for (int i = 0; i < bSLen; i++)
			map.put(i, bS.get(i) ? 1 : 0);
		
		return map;
	}

	public static byte[] byteArrayToNonNullCharByteArray(byte[] bytes) {
        // LSB bit set to 1; 
        // plus 1 bit for every 7 bits of 'bytes', filled with 0's to have a bit count of 8 * k
        // n bytes in 'bytes' => n * 8 bits => Ceiling((n * 8) / 7) leading 1's (followed by original bits in blocks of 7), we need that much bytes to hold the non-null bytes; fill with 0's at the end (up until the length reaches the closest number dividable with 8)
        int byteCount = bytes.length;
        int nonNullCharByteCount = (int)Math.ceil((byteCount * 8.0) / 7.0);
        int nonNullCharBitCount = nonNullCharByteCount * 8;

        // filled with 0's by default
        IneBitSet bits = byteArrayToBitSet(bytes);
        IneBitSet nonNullCharBits = new IneBitSet(nonNullCharBitCount);

        int bitCounter_Bytes = 0;
        int bitCounter_NonNullCharBytes = 0;
        for (int i = 0; i < nonNullCharByteCount; i++) {
            // set MSB to 1 for all bytes in the new byte array
            nonNullCharBits.set(i * 8, true);
            // copy bits of the 'bits' BitSet in blocks of 7
            for (int j = 0; j < 7; j++) {
                bitCounter_Bytes = i * 7 + j; // we are at this position in 'bits' (the bit list of 'bytes')
                // copy bit only if still within range, otherwise leave at 0
                if (bitCounter_Bytes < bits.getRealSize()) {
                    bitCounter_NonNullCharBytes = i * 8 + j + 1; // at byte 'i', leave 1 place for leading 1, skip j positions for the actual bit to copy
                    nonNullCharBits.set(bitCounter_NonNullCharBytes, bits.get(bitCounter_Bytes));
                }
            }
        }
        
        // copy the resulting BitSet to a byte array
        byte[] nonNullCharBytes = bitSetToByteArray(nonNullCharBits);

        return nonNullCharBytes;
    }

	public static byte[] nonNullCharByteArrayToByteArray(byte[] nonNullCharBytes){
        int nonNullCharByteCount = nonNullCharBytes.length;
        //int byteCount = (int)Math.ceil((nonNullCharByteCount * 7.0) / 8.0);
        int byteCount = (nonNullCharByteCount * 7) / 8;
        int bitCount = byteCount * 8;

        // filled with 0's by default
        IneBitSet nonNullCharBits = byteArrayToBitSet(nonNullCharBytes);
        IneBitSet bits = new IneBitSet(bitCount);

        // copy bits according to the following: leading 1's are skipped, first bit to copy: nonNull[1] -> bits[0], then up until nonNull[7] -> bits[6], jump (skip nonNull[8]): nonNull[9] -> bits[7] and so on
        //  original bit -> where to copy it (i)
        //  0 -> 0
        //  1 ->  1
        // ...
        //  7 -> (skip)

        //  8 -> 7
        //  9 -> 0
        // 10 -> 1
        // 11 -> 2
        // 12 -> 3
        // 13 -> 4
        // 14 -> 5
        // 15 -> (skip)
        
        // 23 -> (skip)
        // ... and so on
        int copyPosition = 0;
        for (int i = 0; i < bitCount; i++) {
            copyPosition = i + i / 7 + 1; // skip one position in 'nonNullCharBits' for each 7-bit block already filled in 'bits', and one initially
            if (copyPosition < nonNullCharBits.getRealSize())
                bits.set(i, nonNullCharBits.get(copyPosition));
        }

        // copy the resulting BitSet to a byte array
        byte[] bytes = bitSetToByteArray(bits);

        return bytes;
    }

	public static IneBitSet byteArrayToBitSet(byte[] bytes) {
	    int byteCount = bytes.length;
	    int bitCount = byteCount * 8;
	    IneBitSet bits = new IneBitSet(bitCount);
	    for (int i = 0; i < bitCount; i++)
	        if ((bytes[byteCount - 1 - i / 8] & (1 << (i % 8))) != 0)
	            bits.set(i, true);
	        else
	        	bits.set(i, false);

		return bits;
	}

	public static byte[] bitSetToByteArray(IneBitSet bits) {
		int bitCount = bits.getRealSize();
		int byteCount = (int)Math.ceil(bitCount / 8.0);
	    byte[] bytes = new byte[byteCount];
	    for (int i = 0; i < bitCount; i++)
	        if (bits.get(i))
	            bytes[byteCount - 1 - i / 8] |= (1 << (i % 8));
	    return bytes;
	}
	
	public static String byteArrayToString(byte[] bytes) {
		if (bytes == null)
			return null;
		String str = "";
		for (byte b : bytes)
			str += byteToChar(b);
		return str;
	}

	public static byte[] stringToByteArray(String str) {
		if (str == null)
			return null;
		int strLen = str.length();
		byte[] bytes = new byte[strLen];
		for (int i = 0; i < strLen; i++)
			bytes[i] = charToByte(str.charAt(i));
		return bytes;
	}

	public static char byteToChar(byte b) {
		return (char)(b & 0xFF);
//		byte b = -128;
//		//To Char
//		char c = (char)(b & 0xFF);
//		//To Byte
//		byte b2 = (byte)(c & 0xFF);	}
	}

	public static byte charToByte(char c) {
		return (byte)(c & 0xFF);
	}
}
