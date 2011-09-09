package com.inepex.hyperconnector.serialization.util;

import java.util.BitSet;

/**
 * Wrapper class around java.util.BitSet to be able to tell the count of bits actually used in the BitSet.
 * @author Gabor Dicso
 *
 */
public class IneBitSet extends BitSet {
	private static final long serialVersionUID = -994242481913694857L;
	private int realSize;
	public IneBitSet(int realSize) {
		super(realSize);
		this.realSize = realSize;
	}
	public int getRealSize() {
		return realSize;
	}
}
