package com.inepex.hyperconnector.annotations;

/**
 * Specifies the result types of HyperConnector search methods. IMPORTANT: search
 * method annotations are not used yet.
 * @author Gabor Dicso
 *
 */
public enum SearchResultTypes {
	/**
	 * <p>A search method with this result type returns a list object containing 0, 1
	 * or more HyperEntity instances that match the search parameters.</p>
	 * 
	 * <p>A few notes about row key ordering follow.</p>
	 * 
	 * <p>The row key order must be defined correctly for row key search methods
	 * to work. If the row key is composed by one or more fields following each other,
	 * the sort order of the field(s) composing the row key must be specified correctly
	 * one by one using the sortOrder field of the RowKeyField annotation (that's why
	 * the sortOrder of the possible values of the row key field can be specified per
	 * row key field).</p>
	 * <p>The serialization method used in the row key must provide serialized values
	 * that follow the specified order. (This applies to the individual serialized
	 * values as well as the resulting complete row keys.) Custom serializers must also
	 * follow this rule. If a custom field processor is used, row key ordering is
	 * entirely out of HyperConnector's control.</p>
	 */
	LIST,
	/**
	 * <p>A search method with this result type returns null (no matching result) or
	 * a single result object. In this case the first HyperEntity instance matching
	 * the search parameters will be returned.</p>
	 * 
	 * <p>The first instance among the ones matching the search parameters is in the
	 * row with the smallest row key and has the biggest timestamp among the instances
	 * in the row.</p>
	 * 
	 * <p>A few notes about row key ordering follow.</p>
	 * 
	 * <p>The row key order must be defined correctly for row key search methods
	 * to work. If the row key is composed by one or more fields following each other,
	 * the sort order of the field(s) composing the row key must be specified correctly
	 * one by one using the sortOrder field of the RowKeyField annotation (that's why
	 * the sortOrder of the possible values of the row key field can be specified per
	 * row key field).</p>
	 * <p>The serialization method used in the row key must provide serialized values
	 * that follow the specified order. (This applies to the individual serialized
	 * values as well as the resulting complete row keys.) Custom serializers must also
	 * follow this rule. If a custom field processor is used, row key ordering is
	 * entirely out of HyperConnector's control.</p>
	 */
	SINGLE
}
