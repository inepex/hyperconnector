package com.inepex.hyperconnector.annotations;

/**
 * Specifies the row limit handling types of HyperConnector search methods. IMPORTANT: search
 * method annotations are not used yet.
 * @author Gabor Dicso
 *
 */
public enum RowLimitTypes {
	/**
	 * Indicates that row limiting is not used at all.
	 */
	NONE,
	/**
	 * Indicates that a fixed row limit must be used. The fixed value will be set in the
	 * RowKeySearchMethod annotation.
	 */
	FIXED,
	/**
	 * Indicates that the row limit of the search result will be provided as a search method
	 * parameter, with a value of 0 or below meaning no row limit.
	 */
	PARAMETER
}
