package com.inepex.hyperconnector.annotations;

/**
 * <p>Indicates that the annotated field is a value list. Currently only lists of
 * primitive types are supported. However, serialized values of complex data objects
 * may be stored in the list as String objects.</p>
 * 
 * <p>Below follows a brief description of how HyperConnector handles storing lists and
 * maps. Values of lists and maps are stored in a special column family with a special
 * column qualifier. Sequence numbers (for lists) or keys (for maps) are stored in the
 * column qualifier, values are stored in the cell's value field. Binary serialization
 * is used both in the column qualifier and in the cell's value field.</p>
 * 
 * <p>Column family is "HyperConnector_Collections" for both lists and maps.</p>
 * 
 * <p>Column qualifier:</p>
 * 
 * <p>
 * <ul>
 * <li />Lists: "L&lt;column qualifier fixed part separator
 * string&gt;&lt;field name&gt;&lt;value sequence number&gt;"
 * <li />Maps: "M&lt;column qualifier fixed part separator string&gt;&lt;field
 * name&gt;&lt;key&gt;"
 * </ul>
 * </p>
 * 
 * <p>Explanation of the fields above:</p>
 * 
 * <p>
 * <ul>
 * <li />&lt;column qualifier fixed part separator string&gt;: the HyperConnector column
 * qualifier fixed part separator string.
 * <li />&lt;field name&gt;: the HyperEntity field's name.
 * <li />&lt;value sequence number&gt;: the sequence number of the given value in the
 * list.
 * <li />&lt;key&gt;: the key for the given value in the map.
 * </ul>
 * </p>
 * @author Gabor Dicso
 *
 */
public @interface ValueList {
	
}
