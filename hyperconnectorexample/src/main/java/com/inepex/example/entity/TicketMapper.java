package com.inepex.example.entity;

import java.util.ArrayList;
import java.util.List;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.Key;

import com.inepex.hyperconnector.common.HyperConstants;
import com.inepex.hyperconnector.mapper.HyperMapperBase;
import com.inepex.hyperconnector.mapper.HyperMappingException;
import com.inepex.hyperconnector.serialization.HyperSerializationException;
import com.inepex.hyperconnector.serialization.PlainTextSerializer;

public class TicketMapper extends HyperMapperBase<Ticket> {

	// static final fields for column family values
	private static final String columnFamily_title = "title";
	private static final String columnFamily_description = "description";
	private static final String columnFamily_priority = "priority";
	private static final String columnFamily_problemtype = "problemtype";
	private static final String columnFamily_isFixed = "isFixed";

	protected static final String columnQualifierFixedPart_title_ = "";
	protected static final String columnQualifierFixedPart_description_ = "";
	protected static final String columnQualifierFixedPart_priority_ = "";
	protected static final String columnQualifierFixedPart_problemtype_ = "";
	protected static final String columnQualifierFixedPart_isFixed_ = "";

	protected static final boolean isSingleCell = false;

	@Override
	public boolean getIsSingleCell() {
		return isSingleCell;
	}

	private String createRowKey(Ticket hyperEntity,
			PlainTextSerializer plainTextSer)
			throws HyperSerializationException {
		plainTextSer.writeString(hyperEntity.getServer());

		return plainTextSer.getValueAsNonNullCharString();
	}

	private void processRowKey(Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String rowKey)
			throws HyperSerializationException {
		plainTextSer.setValueAsNonNullCharString(rowKey);

		hyperEntity.setServer(plainTextSer.readString());
	}

	private Cell createCell_title_(Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String rowKey, long timestamp)
			throws HyperSerializationException {

		String columnQualifier = columnQualifierFixedPart_title_;

		byte[] value = new byte[0];
		plainTextSer.writeString(hyperEntity.getTitle());
		value = plainTextSer.getValue();

		return getCell(rowKey, timestamp, columnFamily_title, columnQualifier,
				value);
	}

	private void processCell_title_(Cell cell, Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String columnQualifierFields)
			throws HyperSerializationException {

		plainTextSer.setValue(cell.getValue());
		hyperEntity.setTitle(plainTextSer.readString());
	}

	private Cell createCell_description_(Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String rowKey, long timestamp)
			throws HyperSerializationException {

		String columnQualifier = columnQualifierFixedPart_description_;

		byte[] value = new byte[0];
		plainTextSer.writeString(hyperEntity.getDescription());
		value = plainTextSer.getValue();

		return getCell(rowKey, timestamp, columnFamily_description,
				columnQualifier, value);
	}

	private void processCell_description_(Cell cell, Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String columnQualifierFields)
			throws HyperSerializationException {

		plainTextSer.setValue(cell.getValue());
		hyperEntity.setDescription(plainTextSer.readString());
	}

	private Cell createCell_priority_(Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String rowKey, long timestamp)
			throws HyperSerializationException {

		String columnQualifier = columnQualifierFixedPart_priority_;

		byte[] value = new byte[0];
		plainTextSer.writeString(hyperEntity.getPriority());
		value = plainTextSer.getValue();

		return getCell(rowKey, timestamp, columnFamily_priority,
				columnQualifier, value);
	}

	private void processCell_priority_(Cell cell, Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String columnQualifierFields)
			throws HyperSerializationException {

		plainTextSer.setValue(cell.getValue());
		hyperEntity.setPriority(plainTextSer.readString());
	}

	private Cell createCell_problemtype_(Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String rowKey, long timestamp)
			throws HyperSerializationException {

		String columnQualifier = columnQualifierFixedPart_problemtype_;

		byte[] value = new byte[0];
		plainTextSer.writeString(hyperEntity.getProblemType());
		value = plainTextSer.getValue();

		return getCell(rowKey, timestamp, columnFamily_problemtype,
				columnQualifier, value);
	}

	private void processCell_problemtype_(Cell cell, Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String columnQualifierFields)
			throws HyperSerializationException {

		plainTextSer.setValue(cell.getValue());
		hyperEntity.setProblemType(plainTextSer.readString());
	}

	private Cell createCell_isFixed_(Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String rowKey, long timestamp)
			throws HyperSerializationException {

		String columnQualifier = columnQualifierFixedPart_isFixed_;

		byte[] value = new byte[0];
		plainTextSer.writeboolean(hyperEntity.getIsFixed());
		value = plainTextSer.getValue();

		return getCell(rowKey, timestamp, columnFamily_isFixed,
				columnQualifier, value);
	}

	private void processCell_isFixed_(Cell cell, Ticket hyperEntity,
			PlainTextSerializer plainTextSer, String columnQualifierFields)
			throws HyperSerializationException {

		plainTextSer.setValue(cell.getValue());
		hyperEntity.setIsFixed(plainTextSer.readboolean());
	}

	private CommonCellFields processRawCellList(List<Cell> cells,
			Ticket hyperEntity, PlainTextSerializer plainTextSer)
			throws HyperMappingException, HyperSerializationException {
		long timestamp = 0;
		String rowKey = "";
		// variables for iterating through cells
		String columnFamily = "";
		String columnQualifier = "";
		String columnQualifierFixedPart = "";
		String columnQualifierFields = "";

		// CODEGEN INFO: place for required cell spec flags
		// no expected cells, no flags for them
		boolean cellProcessed_title_ = false;
		boolean cellProcessed_description_ = false;
		boolean cellProcessed_priority_ = false;
		boolean cellProcessed_problemtype_ = false;
		boolean cellProcessed_isFixed_ = false;

		boolean firstCellProcessed = false;
		for (Cell cell : cells) {
			// null checks
			if (cell == null)
				throw new HyperMappingException(invalidCell);
			Key key = cell.getKey();
			if (key == null)
				throw new HyperMappingException(invalidCell_Key);

			// cell consistency check:
			// - read (first cell) or check (later cells) rowKey and timestamp
			// - ensure that column family is not null or empty
			if (firstCellProcessed) {
				// key.timestamp() must be the same for all cells
				// key.getRow() must contain the current rowKey for first cell,
				// must be empty or contain the same rowKey for the rest of the
				// cells
				String currentCellRowKey = key.getRow();
				if (key.getTimestamp() != timestamp
						|| (currentCellRowKey != null
								&& !currentCellRowKey.isEmpty() && !rowKey
									.equals(currentCellRowKey)))
					throw new HyperMappingException(
							invalidCell_InconsistentRowKeyOrTimestamp);
			} else {
				timestamp = key.getTimestamp();
				rowKey = key.getRow();
				if (rowKey == null || rowKey.isEmpty())
					throw new HyperMappingException(
							invalidCell_RowKeyNullOrEmpty);
				firstCellProcessed = true;
			}
			columnFamily = key.getColumn_family();
			if (columnFamily == null || columnFamily.isEmpty())
				throw new HyperMappingException(
						invalidCell_ColumnFamilyNullOrEmpty);

			// current cell is consistent, process fields

			// read column qualifier
			// - columnQualifierPrefix: either the entire column qualifier (no
			// HyperEntity values stored in the column qualifier)
			// or the fixed part of the column qualifier that precedes the
			// HyperEntity values stored in the column qualifier
			// (and the column qualifier prefix separator)
			// - columnQualifierFields: either empty or contains the HyperEntity
			// values in the column qualifier after
			// columnQualifierPrefix (and the column qualifier prefix separator)
			// columnFamily and columnQualifierPrefix specify what cell is being
			// processed (what HyperEntity fields are stored in it)

			columnQualifier = key.getColumn_qualifier();
			if (columnQualifier == null)
				columnQualifier = "";
			String[] columnQualifierParts = columnQualifier.split(
					HyperConstants.columnQualifierFixedPartSeparator, 2);

			columnQualifierFixedPart = columnQualifierParts[0];
			if (columnQualifierFixedPart == null)
				columnQualifierFixedPart = "";

			if (columnQualifierParts.length > 1) {
				columnQualifierFields = columnQualifierParts[1];
				if (columnQualifierFields == null)
					columnQualifierFields = "";
			} else
				columnQualifierFields = "";
			if (columnFamily.equals(columnFamily_title)) {
				if (columnQualifierFixedPart
						.equals(columnQualifierFixedPart_isFixed_)) {
					processCell_title_(cell, hyperEntity, plainTextSer,
							columnQualifierFields);
					cellProcessed_title_ = true;
				} else
					throw new HyperMappingException(invalidCell_InvalidColQ);
			} else if (columnFamily.equals(columnFamily_description)) {
				if (columnQualifierFixedPart
						.equals(columnQualifierFixedPart_title_)) {
					processCell_description_(cell, hyperEntity, plainTextSer,
							columnQualifierFields);
					cellProcessed_description_ = true;
				} else
					throw new HyperMappingException(invalidCell_InvalidColQ);
			} else if (columnFamily.equals(columnFamily_priority)) {
				if (columnQualifierFixedPart
						.equals(columnQualifierFixedPart_description_)) {
					processCell_priority_(cell, hyperEntity, plainTextSer,
							columnQualifierFields);
					cellProcessed_priority_ = true;
				} else
					throw new HyperMappingException(invalidCell_InvalidColQ);
			} else if (columnFamily.equals(columnFamily_problemtype)) {
				if (columnQualifierFixedPart
						.equals(columnQualifierFixedPart_priority_)) {
					processCell_problemtype_(cell, hyperEntity, plainTextSer,
							columnQualifierFields);
					cellProcessed_problemtype_ = true;
				} else
					throw new HyperMappingException(invalidCell_InvalidColQ);
			} else if (columnFamily.equals(columnFamily_isFixed)) {
				if (columnQualifierFixedPart
						.equals(columnQualifierFixedPart_problemtype_)) {
					processCell_isFixed_(cell, hyperEntity, plainTextSer,
							columnQualifierFields);
					cellProcessed_isFixed_ = true;
				} else
					throw new HyperMappingException(invalidCell_InvalidColQ);
			} else
				throw new HyperMappingException(invalidCell_InvalidColF);
		}
		boolean allCellsProcessed = cellProcessed_title_
				&& cellProcessed_description_ && cellProcessed_priority_
				&& cellProcessed_problemtype_ && cellProcessed_isFixed_;
		// check if required cell has been processed, throw if not
		if (!allCellsProcessed)
			throw new HyperMappingException(mappingFailed_MissingCell);
		return new CommonCellFields(rowKey, timestamp);
	}

	@Override
	public List<Cell> hyperEntityToCellList(Ticket hyperEntity)
			throws HyperMappingException {
		if (hyperEntity == null)
			return null;

		try {
			// //////// CREATE SERIALIZER INSTANCES
			PlainTextSerializer plainTextSer = new PlainTextSerializer();

			// //////// CREATE TIMESTAMP
			// Timestamp.assignmentType == TimestampAssignmentTypes.MANUAL
			// not setting timestamp in hyperEntity
			long timestamp = hyperEntity.getTimestamp();
			// //////// CREATE ROWKEY
			String rowKey = createRowKey(hyperEntity, plainTextSer);
			plainTextSer.reset();

			// //////// CREATE CELLS

			List<Cell> cells = new ArrayList<Cell>();

			cells.add(createCell_title_(hyperEntity, plainTextSer, rowKey,
					timestamp));

			plainTextSer.reset();
			cells.add(createCell_description_(hyperEntity, plainTextSer,
					rowKey, timestamp));

			plainTextSer.reset();
			cells.add(createCell_priority_(hyperEntity, plainTextSer, rowKey,
					timestamp));

			plainTextSer.reset();
			cells.add(createCell_problemtype_(hyperEntity, plainTextSer,
					rowKey, timestamp));

			plainTextSer.reset();
			cells.add(createCell_isFixed_(hyperEntity, plainTextSer, rowKey,
					timestamp));

			return cells;
		} catch (HyperSerializationException e) {
			throw getHME(e, mappingFailed);
		}
	}

	@Override
	public Ticket cellListToHyperEntity(List<Cell> cells)
			throws HyperMappingException {
		if (cells == null)
			return null;
		try {
			// create HyperEntity
			Ticket hyperEntity = new Ticket();

			// //////// CREATE SERIALIZER INSTANCES
			PlainTextSerializer plainTextSer = new PlainTextSerializer();

			// //////// PROCESS RAW CELLS
			CommonCellFields ccf = processRawCellList(cells, hyperEntity,
					plainTextSer);

			// //////// PROCESS TIMESTAMP
			hyperEntity.setTimestamp(ccf.timestamp);

			// //////// PROCESS ROWKEY
			plainTextSer.reset();
			processRowKey(hyperEntity, plainTextSer, ccf.rowKey);

			return hyperEntity;
		} catch (HyperSerializationException e) {
			throw getHME(e, mappingFailed);
		}
	}

	@Override
	public Cell hyperEntityToCell(Ticket hyperEntity)
			throws HyperMappingException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Ticket cellToHyperEntity(Cell cell) throws HyperMappingException {
		throw new UnsupportedOperationException();
	}

}