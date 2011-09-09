package com.inepex.hyperconnector.serialization.util;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import com.inepex.hyperconnector.serialization.util.SerializationUtil;
import com.inepex.hyperconnector.serialization.util.IneBitSet;

public class SerializationUtilTest {
	private void bitSetToByteArrayTestOperation(byte[] bytes) {
		byte[] convertedBytes = SerializationUtil.bitSetToByteArray(SerializationUtil.byteArrayToBitSet(bytes));
		Assert.assertTrue(Arrays.equals(bytes, convertedBytes));
	}
	
	private void nonNullCharByteArrayToByteArrayTestOperation(byte[] bytes) {
		byte[] convertedBytes = SerializationUtil.byteArrayToNonNullCharByteArray(bytes);
		byte[] unconvertedBytes = SerializationUtil.nonNullCharByteArrayToByteArray(convertedBytes);
		Assert.assertTrue(Arrays.equals(bytes, unconvertedBytes));
	}
	
	private byte[] getFullByteArray() {
		byte[] bytes = new byte[256];
		byte b = -128;
		for (int i = 0; i < 256; i++)
			bytes[i] = b++;
		return bytes;
	}
	
	private String getFullString() {
		String str = "";
		for (char c = 0; c < 256; c++)
			str += c;
		return str;
	}
	
	@Test
	public void test_bitSetToByteArray() {
		byte b0 = -128;
		byte b1 = -64;
		byte b2 = -16;
		byte b3 = -1;
		byte b4 = 0;
		byte b5 = 1;
		byte b6 = 63;
		byte b7 = 127;

		byte[] bytes = null;
		bytes = new byte[] {b0};
		bitSetToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1};
		bitSetToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2};
		bitSetToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3};
		bitSetToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3, b4};
		bitSetToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3, b4, b5};
		bitSetToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3, b4, b5, b6};
		bitSetToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3, b4, b5, b6, b7};
		bitSetToByteArrayTestOperation(bytes);
		
		bytes = new byte[256];
		byte b = -128;
		for (int i = 0; i < 256; i++)
			bytes[i] = b++;
		bitSetToByteArrayTestOperation(bytes);
	}

	@Test
	public void test2_0() {
		byte[] bytes1 = new byte[] {-128, -1, 0, 1, 127};
		//byte[] bytes1 = new byte[] {127};
		//byte[] bytes2 = HyperHelper.nonNullCharByteArrayFromByteArray_LSB(HyperHelper.byteArrayFromNonNullCharByteArray_LSB(bytes1));
		byte[] bytes2 = SerializationUtil.nonNullCharByteArrayToByteArray(SerializationUtil.byteArrayToNonNullCharByteArray(bytes1));
		Assert.assertTrue(Arrays.equals(bytes1, bytes2));
	}

	@Test
	public void test_nonNullCharByteArrayToByteArray() {
		byte[] bytes = null;

		byte b0 = -128;
		byte b1 = -64;
		byte b2 = -16;
		byte b3 = -1;
		byte b4 = 0;
		byte b5 = 1;
		byte b6 = 63;
		byte b7 = 127;
		bytes = new byte[] {b0};
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1};
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2};
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3};
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3, b4};
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3, b4, b5};
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3, b4, b5, b6};
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
		bytes = new byte[] {b0, b1, b2, b3, b4, b5, b6, b7};
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
		
		bytes = new byte[256];
		byte b = -128;
		for (int i = 0; i < 256; i++)
			bytes[i] = b++;
		nonNullCharByteArrayToByteArrayTestOperation(bytes);

		bytes = new byte[] {0, 1};
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
		
		bytes = new byte[256];
		b = 0;
		for (int i = 0; i < 128; i++)
			bytes[i] = b++;
		b = -128;
		for (int i = 128; i < 256; i++)
			bytes[i] = b++;
		nonNullCharByteArrayToByteArrayTestOperation(bytes);
	}
	
	@Test
	public void test_charToByte() {
		byte b = -128;
		for (int i = 0; i < 256; i++) {
			byte convertedB = SerializationUtil.charToByte(SerializationUtil.byteToChar(b));
			Assert.assertEquals(b, convertedB);
			b++;
		}
	}
	
	@Test
	public void test_byteToChar() {
		char c = 0;
		for (int i = 0; i < 256; i++) {
			char convertedC = SerializationUtil.byteToChar(SerializationUtil.charToByte(c));
			Assert.assertEquals(c, convertedC);
			c++;
		}
	}
	
	@Test
	public void test_byteArrayToString() {
//		String str = "ABCDEFGH";
		String str = getFullString();
		String convertedStr = SerializationUtil.byteArrayToString(SerializationUtil.stringToByteArray(str));
		Assert.assertTrue(convertedStr.equals(str));
	}
	
	@Test
	public void test_stringToByteArray() {
		byte[] bytes = getFullByteArray();
		byte[] convertedBytes = SerializationUtil.stringToByteArray(SerializationUtil.byteArrayToString(bytes));
		Assert.assertTrue(Arrays.equals(bytes, convertedBytes));
	}
	
	@Test
	public void test_NullCharToByte() {
		/*
		 * TODO: byte value of '\0' char must be filtered!!!
		 * it can be a problem if HyperHelper.charToByte('\0') != 0
		 * when converting byte array to non null byte array,
		 * null char must first be mapped to null byte, then
		 * the same in the opposite direction
		 */
		// get the byte value of '\0'
		byte nullB = SerializationUtil.charToByte('\0');
		byte[] nullBArray = new byte[] { nullB };
		IneBitSet bS = SerializationUtil.byteArrayToBitSet(nullBArray);
		for (int i = 0; i < 8; i++)
			Assert.assertFalse(bS.get(i));
		System.out.println("byte value of null char: " + (int)nullB);
	}
	
	@Test
	public void test_byteArrayToNonNullCharByteArray_stringToByteArray() {
		//return HyperHelper.byteArrayToString(HyperHelper.nonNullCharByteArrayFromByteArray_LSB(getValue()));
		//setValue(HyperHelper.byteArrayFromNonNullCharByteArray_LSB(HyperHelper.stringToByteArray(strValue)));
		String str = getFullString();
		byte[] strBytes = SerializationUtil.stringToByteArray(str);
		byte[] strNonNullBytes = SerializationUtil.byteArrayToNonNullCharByteArray(strBytes);
		byte[] strBytes2 = SerializationUtil.nonNullCharByteArrayToByteArray(strNonNullBytes);
		Assert.assertTrue(Arrays.equals(strBytes, strBytes2));
		String convertedStr = SerializationUtil.byteArrayToString(strBytes2);
		Assert.assertTrue(convertedStr.equals(str));
	}
}
