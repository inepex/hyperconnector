package com.inepex.hyperconnector.annotations;

/**
 * Enum for the different serialization types that can be used with HyperConnector.
 * @author Gabor Dicso
 *
 */
public enum SerializationTypes {
	/**
	 * <p>Binary serialization format.</p>
	 */
	BINARY,
	/**
	 * <p>The value is written as is, and is read as is. If multiple fields are stored with
	 * plain serialization, their values will be simply concatenated and it will be
	 * impossible to separate the values upon deserialization. This means that with plain
	 * serialization, only one field's value should be stored in a cell's column qualifier
	 * or value field.</p>
	 * <p>Note: '\0' characters will be removed from strings when using plain serialization.</p>
	 */
	PLAIN,
	/**
	 * <p>Values are stored as strings and they are separated with '\t' characters. It's the
	 * developer's responsibility not to use '\t' characters in the strings to be serialized
	 * this way as they will be removed from fields to be serialized.</p>
	 * <p>Note: '\t' and '\0' characters will be removed from strings when using tab delimited
	 * serialization.</p>
	 */
	TABDELIMITED,
	/**
	 * <p>Indicates a custom serialization type. Fields with customized serialization will be
	 * stored in their customized serialized format. Customized serialization means not only
	 * the customization of field types but the whole serialization and deserialization of all
	 * HyperEntity fields stored in the Hypertable cell's rowkey, column qualifier or value
	 * field. In other words, HyperEntity fields stored with a custom serialization must be
	 * serialized and deserialized by hand-written code for the given HyperEntity. The place
	 * for the hand-written code will be present in the generated code.</p>
	 * <p>The custom serializer must ensure that there are no '\0' characters in the byte array
	 * actually stored in Hypertable, in a transparent and reversible way. Upon selection,
	 * the exact same serialized value will be returned that was inserted.</p>
	 */
	CUSTOM,
	/**
	 * <p>Indicates that the fields of the cell must be handed over to a FieldProcessor object
	 * that creates a series of bytes from the value of the HyperEntity fields stored in the
	 * cell. Differs from CUSTOM in that using the FIELDPROCESSOR serialization type will
	 * result in the generation of a custom FieldProcessor object that receives parameters
	 * matching the fields of the given cell. By default, it serializes fields using binary
	 * format, but this behaviour can be replaced in the code.</p>
	 * <p>The field processor must ensure that there are no '\0' characters in the byte array
	 * actually stored in Hypertable, in a transparent and reversible way. Upon selection,
	 * the exact same serialized value will be returned that was inserted.</p>
	 */
	FIELDPROCESSOR
}
