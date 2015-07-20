package com.inepex.hyperconnector.annotations;

/**
 * Defines the possible search parameter types for HyperConnector search methods. IMPORTANT: search
 * method annotations are not used yet.
 * @author Gabor Dicso
 *
 */
public enum SearchParamTypes {
	/**
	 * Indicates that the search parameter is a discrete value. We search for entries with this value.
	 */
	DISCRETE,
	/**
	 * Indicates that the search parameter is a value interval. We search for entries with a value
	 * that is within the given interval. Interval ends may be inclusive or exclusive; the generated
	 * code will have parameters that specify these values, with a default of inclusive for both.
	 */
	INTERVAL,
//	/**
//	 * Indicates that the search parameter contains the beginning of the field being searched.
//	 * Useful when searching on Hypertable row key or column qualifier.
//	 */
//	BEGINSWITH,
//	/**
//	 * Indicates that the search parameter contains a regular expression that must be matched
//	 * against the field being searched. Can be used for searching row key or cell value fields.
//	 */
//	REGEXP
}
