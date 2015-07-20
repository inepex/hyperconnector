package com.inepex.hyperconnector.serialization;

import junit.framework.Assert;

import org.junit.Test;

public class PlainTextSerializerTest {
	@Test
	public void test0() {
		PlainTextSerializer ser1 = new PlainTextSerializer();
		PlainTextSerializer ser2 = new PlainTextSerializer();
		
		int i = 1;
//		double d = 2.0;
//		float f = (float)3.0;
//		String str1 = "String1";
//		String str2 = null;
		
		try {
			ser1.writeint(i);
//			ser1.writedouble(d);
//			ser1.writefloat(f);
//			ser1.writeString(str1);
//			ser1.writeString(str2);
			
			ser2.setValue(ser1.getValue());
			
			int iCopy = ser2.readint();
//			double dCopy = ser2.readdouble();
//			float fCopy = ser2.readfloat();
//			String str1Copy = ser2.readString();
//			String str2Copy = ser2.readString();

			Assert.assertEquals(i, iCopy);
//			Assert.assertEquals(d, dCopy);
//			Assert.assertEquals(f, fCopy);
//			Assert.assertEquals(str1, str1Copy);
//			Assert.assertEquals(str2, str2Copy);
		} catch (HyperSerializationException e) {
			Assert.fail();
		}
	}
}
