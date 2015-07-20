package com.inepex.hyperconnector.annotations;

/**
 * Defines a row key based search method (select and/or delete).
 * @author Gabor Dicso
 *
 */
public @interface RowKeyBasedSearchMethod {
	/**
	 * The base of the method name. Must be unique for the HyperEntity. Select methods will be
	 * prefixed with "select". Delete methods will be prefixed with "delete".
	 */
	String nameBase();
	/**
	 * Indicates if deleting entries should be enabled with the given method.
	 */
	boolean delete() default true;
	/**
	 * Indicates if selecting entries should be enabled with the given method.
	 */
	boolean select() default true;
	/**
	 * <p>Specifies the <i>last</i> field to be used as search parameter in the search method. Last
	 * here means that taking the row key fields in the order set by the RowKeyField annotations,
	 * we only use the field specified by lastSearchFieldName and the fields preceding it. The
	 * last search field may have a discrete value or a range. The search method's parameters
	 * must contain a fixed value for all search fields preceding it.</p>
	 * <p>If lastSearchFieldName is set to an empty string, the search will match all rows.</p>
	 */
	String lastSearchFieldName() default "";
	/**
	 * Specifies the type of the last search field. Used only if lastSearchFieldName is set to the
	 * name of a row key field (i.e. is not set to an empty string).
	 */
	SearchParamTypes lastSearchFieldParamType() default SearchParamTypes.DISCRETE;
	/**
	 * Sets if the timestamp of the searched HyperEntity must be specified. If set to true, search
	 * results will be filtered by the timestamp of the Hypertable cell(s).
	 */
	boolean specifyTimestamp() default false;
	/**
	 * The row limit handling type of the search method.
	 */
	RowLimitTypes rowLimitType() default RowLimitTypes.NONE;
	/**
	 * Only used if rowLimitType is set to FIXED. Specifies the fixed row limit value to be used
	 * in this case.
	 */
	int fixedRowLimit() default 0;
	/**
	 * The result type of the search method. Select method: specifies the returned value. Delete
	 * method: deletes the value that is returned by the select method.
	 */
	SearchResultTypes resultType();
	/**
	 * Specifies if a regular expression may also be passed as parameter and should be used in the
	 * search.
	 */
	boolean allowRegExp() default false;
	/**
	 * Specifies if a row key prefix may also be passed as parameter and should be used in the
	 * search as a "starts with" search parameter for the row key. Makes primarily sense with no
	 * additional search parameters specified (maybe except for a regexp).
	 */
	boolean allowStartsWith() default false;
}
