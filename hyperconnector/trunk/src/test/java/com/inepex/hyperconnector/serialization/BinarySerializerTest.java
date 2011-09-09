package com.inepex.hyperconnector.serialization;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

public class BinarySerializerTest {
	@Test
	public void test1() {
		BinarySerializer ser1 = new BinarySerializer();
		BinarySerializer ser2 = new BinarySerializer();
		
		boolean booleanValue = true;
		Boolean booleanValueObj1 = true;
		Boolean booleanValueObj2 = null;
		byte byteValue = 1;
		Byte byteValueObj1 = 2;
		Byte byteValueObj2 = null;
		double doubleValue = 3.0;
		Double doubleValueObj1 = 4.0;
		Double doubleValueObj2 = null;
		float floatValue = (float)5.0;
		Float floatValueObj1 = (float)6.0;
		Float floatValueObj2 = null;
		int intValue = 7;
		Integer intValueObj1 = 8;
		Integer intValueObj2 = null;
		long longValue = 9L;
		Long longValueObj1 = 10L;
		Long longValueObj2 = null;
		short shortValue = 11;
		Short shortValueObj1 = 12;
		Short shortValueObj2 = null;
		String str1 = "String1";
		String str2 = null;
		
		try {
			ser1.writeboolean(booleanValue);
			ser1.writeBoolean(booleanValueObj1);
			ser1.writeBoolean(booleanValueObj2);
			ser1.writebyte(byteValue);
			ser1.writeByte(byteValueObj1);
			ser1.writeByte(byteValueObj2);
			ser1.writeint(intValue);
			ser1.writeInteger(intValueObj1);
			ser1.writeInteger(intValueObj2);
			ser1.writedouble(doubleValue);
			ser1.writeDouble(doubleValueObj1);
			ser1.writeDouble(doubleValueObj2);
			ser1.writefloat(floatValue);
			ser1.writeFloat(floatValueObj1);
			ser1.writeFloat(floatValueObj2);
			ser1.writelong(longValue);
			ser1.writeLong(longValueObj1);
			ser1.writeLong(longValueObj2);
			ser1.writeshort(shortValue);
			ser1.writeShort(shortValueObj1);
			ser1.writeShort(shortValueObj2);
			ser1.writeString(str1);
			ser1.writeString(str2);
			
			ser2.setValue(ser1.getValue());
			
			Assert.assertEquals(booleanValue, ser2.readboolean());
			Assert.assertEquals(booleanValueObj1, ser2.readBoolean());
			Assert.assertEquals(booleanValueObj2, ser2.readBoolean());
			Assert.assertEquals(byteValue, ser2.readbyte());
			Assert.assertEquals(byteValueObj1, ser2.readByte());
			Assert.assertEquals(byteValueObj2, ser2.readByte());
			Assert.assertEquals(intValue, ser2.readint());
			Assert.assertEquals(intValueObj1, ser2.readInteger());
			Assert.assertEquals(intValueObj2, ser2.readInteger());
			Assert.assertEquals(doubleValue, ser2.readdouble());
			Assert.assertEquals(doubleValueObj1, ser2.readDouble());
			Assert.assertEquals(doubleValueObj2, ser2.readDouble());
			Assert.assertEquals(floatValue, ser2.readfloat());
			Assert.assertEquals(floatValueObj1, ser2.readFloat());
			Assert.assertEquals(floatValueObj2, ser2.readFloat());
			Assert.assertEquals(longValue, ser2.readlong());
			Assert.assertEquals(longValueObj1, ser2.readLong());
			Assert.assertEquals(longValueObj2, ser2.readLong());
			Assert.assertEquals(shortValue, ser2.readshort());
			Assert.assertEquals(shortValueObj1, ser2.readShort());
			Assert.assertEquals(shortValueObj2, ser2.readShort());
			Assert.assertEquals(str1, ser2.readString());
			Assert.assertEquals(str2, ser2.readString());
		} catch (HyperSerializationException e) {
			Assert.fail();
		}
	}

	@Test
	public void test2() {
		BinarySerializer ser1 = new BinarySerializer();
		BinarySerializer ser2 = new BinarySerializer();
		
		int i = 1;
		double d = 2.0;
		
		try {
			ser1.writeint(i);
			ser2.setValue(ser1.getValue());
			int iCopy = ser2.readint();
			Assert.assertEquals(i, iCopy);

			ser1.reset();
			ser2.reset();

			ser1.writedouble(d);
			ser2.setValue(ser1.getValue());
			double dCopy = ser2.readdouble();
			Assert.assertEquals(d, dCopy);
		} catch (HyperSerializationException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void test3() {
		BinarySerializer ser1 = new BinarySerializer();
		BinarySerializer ser2 = new BinarySerializer();
		
		int i = 1;
		double d = 2.0;
		float f = (float)3.0;
		String str1 = "String1";
		String str2 = null;
		
		try {
			ser1.writeint(i);
			ser1.writedouble(d);
			ser1.writefloat(f);
			ser1.writeString(str1);
			ser1.writeString(str2);
			
			String ser1UTF8Value = ser1.getValueAsString();
			
			ser2.setValueAsString(ser1UTF8Value);
			
			Assert.assertTrue(Arrays.equals(ser1.getValue(), ser2.getValue()));
			
			int iCopy = ser2.readint();
			double dCopy = ser2.readdouble();
			float fCopy = ser2.readfloat();
			String str1Copy = ser2.readString();
			String str2Copy = ser2.readString();

			Assert.assertEquals(i, iCopy);
			Assert.assertEquals(d, dCopy);
			Assert.assertEquals(f, fCopy);
			Assert.assertEquals(str1, str1Copy);
			Assert.assertEquals(str2, str2Copy);
		} catch (HyperSerializationException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void test4() {
		BinarySerializer ser1 = new BinarySerializer();
		BinarySerializer ser2 = new BinarySerializer();
		
		int i = 1;
		double d = 2.0;
		float f = (float)3.0;
		String str1 = "String1";
		String str2 = null;
		
		try {
			ser1.writeint(i);
			ser1.writedouble(d);
			ser1.writefloat(f);
			ser1.writeString(str1);
			ser1.writeString(str2);
			
			String ser1NonNullUTF8Value = ser1.getValueAsNonNullCharString();
			
			ser2.setValueAsNonNullCharString(ser1NonNullUTF8Value);
			
			byte[] value1 = ser1.getValue();
			byte[] value2 = ser2.getValue();
			
			Assert.assertTrue(Arrays.equals(value1, value2));
			
			int iCopy = ser2.readint();
			double dCopy = ser2.readdouble();
			float fCopy = ser2.readfloat();
			String str1Copy = ser2.readString();
			String str2Copy = ser2.readString();

			Assert.assertEquals(i, iCopy);
			Assert.assertEquals(d, dCopy);
			Assert.assertEquals(f, fCopy);
			Assert.assertEquals(str1, str1Copy);
			Assert.assertEquals(str2, str2Copy);
		} catch (HyperSerializationException e) {
			Assert.fail();
		}
	}
}
