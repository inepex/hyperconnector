package com.inepex.hyperconnector.annotations;

/**
 * Defines how data must be serialized in the HyperEntity's Hypertable cell specified by
 * columnFamily and columnQualifier.
 * @author Gabor Dicso
 *
 */
public @interface CellSerializationDescriptor {
	/**
	 * <p>The column family of the cell.</p>
	 * <p>The columnFamily and columnQualifierFixedPart fields together are referred to as CellSpec.</p>
	 */
	String columnFamily();
	/**
	 * <p>The fixed part of the column qualifier of the cell. Note: the column qualifier of
	 * the actual cell inserted may contain field values after the column qualifier
	 * prefix provided in the columnQualifier field.</p>
	 * <p>The columnFamily and columnQualifierFixedPart fields together are referred to as CellSpec.</p>
	 */
	String columnQualifierFixedPart() default "";
	/**
	 * Specifies the serialization type to be used in the column qualifier field of the
	 * specified cell. Affects only the HyperEntity field values stored in the column
	 * qualifier; column qualifier prefix will be serialized in plain text anyway.
	 */
	SerializationTypes columnQualifierSerializationType() default SerializationTypes.BINARY;
	/**
	 * Specifies the serialization type to be used when storing the values in the cell's value
	 * field.
	 */
	SerializationTypes valueSerializationType() default SerializationTypes.BINARY;
}
