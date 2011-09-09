package com.inepex.hyperconnector.annotations;

/**
 * Indicates that the field must be used in the row key field of the Hypertable cell(s) of the
 * HyperEntity. At least one field of a HyperEntity must be annotated with RowKeyField. Note:
 * Hypertable row keys must not contain '\0' characters.
 * @author Gabor Dicso
 *
 */
public @interface RowKeyField {
	/**
	 * Sets the order in which the fields must be serialized. Fields with the same order value
	 * will be sorted by their names.
	 */
	int order() default 0;
	/**
	 * Tells the sorting order of the values of the given row key field (ascending or descending).
	 */
	SortOrders sortOrder() default SortOrders.ASCENDING;
}
