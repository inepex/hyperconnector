package com.inepex.hyperconnector.annotations;

/**
 * <p>Indicates that the annotated HyperEntity field must be stored in Hypertable.
 * Defines how the field must be stored. This means specifying what the Hypertable cell
 * in which the field must be stored looks like.</p>
 * <p>Note: all fields annotated with ValueField with the same columnFamily and
 * columnQualifierFixedPart values will have their values stored in the same Hypertable
 * cell (even though field values stored in the column qualifier will change the actual
 * column qualifier value).</p>
 * @author Gabor Dicso
 *
 */
public @interface ValueField {
	/**
	 * <p>The column family in which the field must be stored.</p>
	 * <p>The columnFamily and columnQualifierFixedPart fields together are referred to as CellSpec.</p>
	 */
	String columnFamily();
	/**
	 * <p>The fixed part of the column qualifier (used as prefix if there are HyperEntity
	 * field values stored in the column qualifier) for the cell, optional field, default
	 * is empty column qualifier fixed part.</p>
	 * <p>If the field value is stored in the column qualifier, the value of
	 * columnQualifierFixedPart will be used as a prefix to the value(s) stored in the
	 * column qualifier. The prefix is separated from the field values by the
	 * HyperConnector column qualifier prefix separator string. If there is no prefix but
	 * there is at least one value stored in the column qualifier, the column qualifier
	 * will start with the HyperConnector column qualifier prefix separator string.</p>
	 * <p>The columnFamily and columnQualifierFixedPart fields together are referred to as CellSpec.</p>
	 */
	String columnQualifierFixedPart() default "";
	/**
	 * Indicates in which field of the Hypertable cell the value must be stored.
	 */
	CellFields cellField() default CellFields.VALUE;
	/**
	 * Sets the order in which the fields must be serialized. Fields with the same order value
	 * will be sorted by their names.
	 */
	int order() default 0;
}
