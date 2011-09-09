package com.inepex.hyperconnector.annotations;

/**
 * Defines search methods for the given HyperEntity. IMPORTANT: search
 * method annotations are not used yet.
 * @author Gabor Dicso
 *
 */
public @interface SearchMethods {
	/**
	 * Row key based search methods for the HyperEntity.
	 */
	RowKeyBasedSearchMethod[] rowKeyBasedSearchMethods();
}
