package tmp;

import java.util.List;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.Key;

import com.inepex.hyperconnector.common.HyperConstants;
import com.inepex.hyperconnector.common.HyperDateCommon;
import com.inepex.hyperconnector.mapper.HyperMapperBase;
import com.inepex.hyperconnector.mapper.HyperMappingException;
import com.inepex.hyperconnector.serialization.BinarySerializer;
import com.inepex.hyperconnector.serialization.HyperSerializationException;

public class DeviceEventMapper extends HyperMapperBase<DeviceEvent> {

	// static final fields for column family values
	private static final String columnFamily_d = "d";

	protected static final String columnQualifierFixedPart_d_ = "";

	protected static final boolean isSingleCell = true;

	@Override
	public boolean getIsSingleCell() {
		return isSingleCell;
	}

	private String createRowKey(DeviceEvent hyperEntity,
			DeviceEventRowKeyFieldProcessor rowKeyFieldProc)
			throws HyperSerializationException {
		rowKeyFieldProc.setDeviceId(hyperEntity.getDeviceId());
		rowKeyFieldProc
				.setCreationTimestamp(hyperEntity.getCreationTimestamp());

		return rowKeyFieldProc.getValueAsNonNullCharString();
	}

	private void processRowKey(DeviceEvent hyperEntity,
			DeviceEventRowKeyFieldProcessor rowKeyFieldProc, String rowKey)
			throws HyperSerializationException {
		rowKeyFieldProc.setValueAsNonNullCharString(rowKey);

		hyperEntity.setDeviceId(rowKeyFieldProc.getDeviceId());
		hyperEntity
				.setCreationTimestamp(rowKeyFieldProc.getCreationTimestamp());
	}

	private Cell createCell_d_(DeviceEvent hyperEntity,
			BinarySerializer binarySer, String rowKey, long timestamp)
			throws HyperSerializationException {
		binarySer.writeString(hyperEntity.getName());

		String columnQualifier = columnQualifierFixedPart_d_
				+ HyperConstants.columnQualifierFixedPartSeparator
				+ binarySer.getValueAsNonNullCharString();

		// same serializer used for column qualifier and value fields, reset it
		binarySer.reset();

		byte[] value = new byte[0];
		binarySer.writeString(hyperEntity.getMessage());
		binarySer.writeLong(hyperEntity.getConfirmDate());
		binarySer.writeLong(hyperEntity.getConfirmUser());
		value = binarySer.getValue();

		return getCell(rowKey, timestamp, columnFamily_d, columnQualifier,
				value);
	}

	private void processCell_d_(Cell cell, DeviceEvent hyperEntity,
			BinarySerializer binarySer, String columnQualifierFields)
			throws HyperSerializationException {
		binarySer.setValueAsNonNullCharString(columnQualifierFields);
		hyperEntity.setName(binarySer.readString());

		// same serializer used for column qualifier and value fields, reset it
		binarySer.reset();

		binarySer.setValue(cell.getValue());
		hyperEntity.setMessage(binarySer.readString());
		hyperEntity.setConfirmDate(binarySer.readLong());
		hyperEntity.setConfirmUser(binarySer.readLong());
	}

	private CommonCellFields processRawCell(Cell cell, DeviceEvent hyperEntity,
			BinarySerializer binarySer) throws HyperMappingException,
			HyperSerializationException {
		// timestamp and rowKey values must be the same for all cells
		long timestamp = 0;
		String rowKey = "";
		// variables for iterating through cells
		String columnFamily = "";
		String columnQualifier = "";
		String columnQualifierFixedPart = "";
		String columnQualifierFields = "";

		// null check for key
		Key key = cell.getKey();
		if (key == null)
			throw new HyperMappingException(invalidCell_Key);

		// cell consistency check:
		// - read rowKey and timestamp
		// - ensure that row key and column family are not null or empty
		timestamp = key.getTimestamp();
		rowKey = key.getRow();
		if (rowKey == null || rowKey.isEmpty())
			throw new HyperMappingException(invalidCell_RowKeyNullOrEmpty);
		columnFamily = key.getColumn_family();
		if (columnFamily == null || columnFamily.isEmpty())
			throw new HyperMappingException(invalidCell_ColumnFamilyNullOrEmpty);

		// current cell is consistent, process fields

		// read column qualifier
		// - columnQualifierFixedPart: either the entire column qualifier (no
		// HyperEntity values stored in the column qualifier)
		// or the fixed part of the column qualifier that precedes the
		// HyperEntity values stored in the column qualifier
		// (and the column qualifier fixed part separator)
		// - columnQualifierFields: either empty or contains the HyperEntity
		// values in the column qualifier after
		// columnQualifierFixedPart (and the column qualifier fixed part
		// separator)
		// columnFamily and columnQualifierFixedPart specify what cell is being
		// processed (what HyperEntity fields are stored in it)

		columnQualifier = key.getColumn_qualifier();
		if (columnQualifier == null)
			columnQualifier = "";
		String[] columnQualifierParts = columnQualifier.split(
				HyperConstants.columnQualifierFixedPartSeparator, 2);

		columnQualifierFixedPart = columnQualifierParts[0];
		if (columnQualifierParts.length > 1)
			columnQualifierFields = columnQualifierParts[1];
		else
			columnQualifierFields = "";
		if (columnQualifierFields == null)
			columnQualifierFields = "";
		// at this point columnQualifierFields is surely not null

		boolean cellProcessed_d_ = false;
		if (columnFamily.equals(columnFamily_d)) {
			if (columnQualifierFixedPart.equals(columnQualifierFixedPart_d_)) {
				processCell_d_(cell, hyperEntity, binarySer,
						columnQualifierFields);
				cellProcessed_d_ = true;
			} else
				throw new HyperMappingException(invalidCell_InvalidColQ);
		} else
			throw new HyperMappingException(invalidCell_InvalidColF);
		boolean allCellsProcessed = cellProcessed_d_;
		// check if required cell has been processed, throw if not
		if (!allCellsProcessed)
			throw new HyperMappingException(mappingFailed_MissingCell);
		return new CommonCellFields(rowKey, timestamp);
	}

	@Override
	public List<Cell> hyperEntityToCellList(DeviceEvent hyperEntity)
			throws HyperMappingException {
		return null;
	}

	@Override
	public DeviceEvent cellListToHyperEntity(List<Cell> cells)
			throws HyperMappingException {
		return null;
	}

	@Override
	public Cell hyperEntityToCell(DeviceEvent hyperEntity)
			throws HyperMappingException {
		if (hyperEntity == null)
			return null;

		try {
			// //////// CREATE SERIALIZER INSTANCES
			DeviceEventRowKeyFieldProcessor rowKeyFieldProc = new DeviceEventRowKeyFieldProcessor();
			BinarySerializer binarySer = new BinarySerializer();

			// //////// CREATE TIMESTAMP
			// Timestamp.assignmentType ==
			// TimestampAssignmentTypes.AUTO_HYPERCONNECTOR
			// timestamp is assigned by HyperConnector
			long timestamp = HyperDateCommon.utcNowInNanoSecs();
			hyperEntity.setStorageTimestamp(timestamp);
			// //////// CREATE ROWKEY
			String rowKey = createRowKey(hyperEntity, rowKeyFieldProc);

			return createCell_d_(hyperEntity, binarySer, rowKey, timestamp);
		} catch (HyperSerializationException e) {
			throw getHME(e, mappingFailed);
		}
	}

	@Override
	public DeviceEvent cellToHyperEntity(Cell cell)
			throws HyperMappingException {
		if (cell == null)
			return null;
		try {
			// create HyperEntity
			DeviceEvent hyperEntity = new DeviceEvent();

			// //////// CREATE SERIALIZER INSTANCES
			DeviceEventRowKeyFieldProcessor rowKeyFieldProc = new DeviceEventRowKeyFieldProcessor();
			BinarySerializer binarySer = new BinarySerializer();

			// //////// PROCESS RAW CELLS
			CommonCellFields ccf = processRawCell(cell, hyperEntity, binarySer);

			// //////// PROCESS TIMESTAMP
			hyperEntity.setStorageTimestamp(ccf.timestamp);

			// //////// PROCESS ROWKEY
			processRowKey(hyperEntity, rowKeyFieldProc, ccf.rowKey);

			return hyperEntity;
		} catch (HyperSerializationException e) {
			throw getHME(e, mappingFailed);
		}
	}

}