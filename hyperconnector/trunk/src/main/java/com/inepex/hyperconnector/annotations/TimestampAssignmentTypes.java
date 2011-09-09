package com.inepex.hyperconnector.annotations;

/**
 * Specifies how the value of the timestamp in a HyperEntity can be assigned upon
 * insertion. Applies to the HyperEntity field annotated with Timestamp.
 * @author Gabor Dicso
 *
 */
public enum TimestampAssignmentTypes {
	/**
	 * Timestamp will indicate when the HyperEntity was inserted. Timestamp previously
	 * set in the HyperEntity will be discarded upon insertion. Timestamp will be
	 * assigned by HyperConnector. The timestamp assigned to the HyperEntity by
	 * HyperConnector will be set in the given HyperEntity instance upon insertion.
	 */
	AUTO_HYPERCONNECTOR,
	/**
	 * <p>Suitable for HyperEntity types with exactly one Hypertable cell per HyperEntity.
	 * For HyperEntity types with more than one Hypertable cell, AUTO_HYPERTABLE will
	 * be treated as AUTO_HYPERCONNECTOR.</p>
	 * <p>Timestamp will be assigned automatically by Hypertable. Note: the timestamp 
	 * assigned to the HyperEntity by Hypertable will NOT be set in the given
	 * HyperEntity instance upon insertion.</p>
	 */
	AUTO_HYPERTABLE,
	/**
	 * The HyperEntity will be inserted with the timestamp that has been assigned to
	 * the annotated HyperEntity field before insertion.
	 */
	MANUAL,
	/**
	 * The HyperEntity will be inserted with the fixed timestamp specified in the
	 * Timestamp annotation.
	 */
	FIXED
}
