package com.inepex.hyperconnector.annotations;

/**
 * Specifies access groups in the Hypertable table of the HyperEntity.
 * @author Gabor Dicso
 *
 */
public @interface AccessGroup {
	/**
	 * The name of the access group.
	 */
	String name();
	/**
	 * The list of the column families in the access group.
	 */
	String[] columnFamilies();
}
