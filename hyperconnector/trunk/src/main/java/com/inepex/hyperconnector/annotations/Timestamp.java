package com.inepex.hyperconnector.annotations;

/**
 * <p>A HyperEntity may have 0 or 1 field annotated with Timestamp. The annotated field must be
 * of type long.</p>
 * <p>If no annotated field is present, timestamp values of cells will be handled as default
 * (upon insertion, HyperConnector assigns timestamp of insertion to all cells of the HyperEntity)
 * and timestamp will not be visible in the HyperEntity upon selection.</p>
 * <p>If there is an annotated field, its value will be used upon insertion according to the value
 * of autoAssigned. If multiple cells are inserted for the same HyperEntity instance, all their
 * timestamps will be equal.</p>
 * @author Gabor Dicso
 *
 */
public @interface Timestamp {
	/**
	 * <p>This value is used when inserting the HyperEntity into Hypertable.</p>
	 * <p>If set to AUTO, the timestamp of the cell(s) will be the timestamp of the insertion,
	 * regardless from the actual value of the annotated field.</p>
	 * <p>If set to MANUAL, the field's value will be used as the timestamp for the cell(s) being
	 * stored.</p>
	 * <p>If set to FIXED, the field's value will be set to the timestamp value specified in
	 * fixedTimestamp.</p>
	 * <p>When reading the cell(s) from Hypertable, the annotated field's value will be set to
	 * the timestamp of the cell(s), regardless from the value of assignmentType. If multiple cells
	 * were inserted for the same HyperEntity instance, all their timestamps will be equal.</p>
	 */
	TimestampAssignmentTypes assignmentType() default TimestampAssignmentTypes.AUTO_HYPERCONNECTOR;
	/**
	 * The timestamp value that must be applied for the cell(s) of the HyperEntity. Used only
	 * if assignmentType is set to FIXED.
	 */
	long fixedTimestamp() default 0;
}
