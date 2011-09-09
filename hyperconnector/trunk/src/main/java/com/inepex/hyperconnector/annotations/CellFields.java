package com.inepex.hyperconnector.annotations;

/**
 * The fields of a Hypertable cell where HyperEntity field values can be stored.
 * @author Gabor Dicso
 *
 */
public enum CellFields {
	/**
	 * The column qualifier of the cell.
	 */
	COLUMNQUALIFIER,
	/**
	 * The value field of the cell.
	 */
	VALUE
}
