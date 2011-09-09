package com.inepex.hyperconnector.annotations;

/**
 * <p>Indicates that the annotated class is a HyperConnector HyperEntity.</p>
 * 
 * <p>HyperEntity instances are stored in one or more Hypertable cells. HyperEntity fields
 * can be stored in the row key of a cell, the timestamp of a cell, the column qualifier of
 * a cell, or the value field of a cell. All these field value locations allow for storing
 * 0, 1 or more fields of different types, except for the timestamp field, which allows for
 * storing exactly 1 field of type long.</p>
 * 
 * <p>Row key and timestamp are the same for all the cells (one or more) of a HyperEntity.
 * Other fields may be stored in the column qualifier or value fields of these cells. As
 * their row key and timestamp fields are the same, only the column family and the column
 * qualifier distinguish between them.</p>
 * 
 * <p>If there are no HyperEntity field values stored in the column qualifier of a cell, the
 * whole column qualifier for that cell is constant (it may also be empty). In this case,
 * the column family and the whole column qualifier (empty or not) specify the cell.</p>
 * 
 * <p>If there are HyperEntity field values stored in the column qualifier, the whole value
 * of the column qualifier depends on the actual values stored in it. When restoring the
 * HyperEntity field value from the HyperEntity's Hypertable cells, we must be able to
 * unambiguously find the cell that contains the given value. HyperConnector allows values
 * stored in the column qualifier to be preceded by a constant part as a prefix (it may also
 * be left empty). As we can't use the whole column qualifier value to specify the cell
 * because its whole value depends on the HyperEntity field values stored in it, we must
 * rely on the constant prefix along with the column family to find the cell that contains
 * the values for the given HyperEntity fields. This also means that the constant prefix
 * must be unique for the cells in the given column family.</p>
 * 
 * <p>In either case, the constant part of the column qualifier is called the column
 * qualifier fixed part. This, along with the column family, can be used to distinguish
 * between the cells of the HyperEntity. Together they are referred to as cell spec: a
 * column family and column qualifier fixed part pair. Cell specs must be unique for the
 * HyperEntity. (See also in the description of the ValueField annotation.)</p>
 * 
 * <p>There are two kinds of special cell spec for list and map values of HyperEntity types.
 * See the ValueList and ValueMap annotations for details.</p>
 * 
 * <p>Depending on its Hypertable schema defined by the annotations of the fields in it, a
 * HyperEntity can be
 * 
 * <ul>
 * <li /> single cell spec: the annotations of the fields in the HyperEntity define exactly
 *        one cell spec, not counting list or map fields
 * <li /> multiple cell spec: the annotations of the fields in the HyperEntity define either
 *        0 or more than 1 cell spec and/or it has at least one list or map field
 * <li /> having collection: the HyperEntity has at least one list or map field
 * <li /> not having collection: the HyperEntity has no list or map fields
 * <li /> single cell: the HyperEntity is stored in exactly 1 Hypertable cell; this means
 *        that the HyperEntity is single cell spec and it has no list or map fields
 * <li /> multiple cell: the HyperEntity is stored in 1 or more Hypertable cells; this means
 *        that the HyperEntity is multiple cell spec and/or it has at least one list or map
 *        field
 * </ul>
 * 
 * </p>
 * 
 * <p>Cell specs are useful for checking if all mandatory cells have been processed when
 * constructing a HyperEntity from its Hypertable cell(s). They also enable the cell(s) of
 * the HyperEntity to be validated (checking if the cell's cell spec is valid for the given
 * HyperEntity type or not). Finally, it is easy to ensure for each cell spec that they are
 * processed exactly once for a HyperEntity.</p>
 * 
 * <p>List and map elements are handled in a special way, their values are stored in
 * separate cells for each item with a special cell spec. As lists and maps can be empty,
 * the count of these cells can be 0, 1 or more for each list or map field in the
 * HyperEntity. As a result, they can not be handled as mandatory cells with exactly one
 * instance of them.</p>
 * @author Gabor Dicso
 *
 */
public @interface HyperEntity {
	/**
	 * The Hypertable namespace of the HyperEntity's table.
	 */
	String namespace() default "/";
	/**
	 * The name of the HyperEntity's Hypertable table.
	 */
	String table();
	/**
	 * If set to a value > 0, sets the GROUP_COMMIT_INTERVAL value in the CREATE TABLE command.
	 */
	int groupCommitIntervalMillis() default 0;
	/**
	 * Describes the serialization type(s) of the HyperEntity's Hypertable cell(s).
	 */
	CellSerializationDescriptor[] cellSerializationDescriptors() default { };
	/**
	 * Specifies the serialization type of the rowkey of the Hypertable cell(s). If a custom
	 * RowKeyProvider is used for the HyperEntity, the rowKeySerializationType field may or
	 * may not be used by it.
	 */
	SerializationTypes rowKeySerializationType() default SerializationTypes.BINARY;
	/**
	 * Specifies a global default serialization type for the column qualifier fields of the
	 * HyperEntity's Hypertable cells.
	 */
	SerializationTypes globalColumnQualifierSerializationType() default SerializationTypes.BINARY;
	/**
	 * Specifies a global default serialization type for the value fields of the
	 * HyperEntity's Hypertable cells.
	 */
	SerializationTypes globalValueSerializationType() default SerializationTypes.BINARY;
	/**
	 * Specifies the access groups of the Hypertable table. Optional field.
	 */
	AccessGroup[] accessGroups() default { };
}
