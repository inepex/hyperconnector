package com.inepex.hyperconnector.mapper;

import org.hypertable.thriftgen.Cell;
import org.hypertable.thriftgen.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HyperMapperBase<T> {
	protected class CommonCellFields {
		public final String rowKey;
		public final long timestamp;
		public CommonCellFields(String rowKey, long timestamp) {
			this.rowKey = rowKey;
			this.timestamp = timestamp;
		}
	}

	public static final String mappingFailed = "Mapping failed.";
	public static final String invalidCell = "Mapping failed: invalid cell.";
	public static final String invalidCell_Key = "Mapping failed: invalid cell key.";
	public static final String invalidCell_InconsistentRowKeyOrTimestamp = "Mapping failed: invalid cell, inconsistent row key or timestamp.";
	public static final String invalidCell_RowKeyNullOrEmpty = "Mapping failed: invalid cell, row key null or empty.";
	public static final String invalidCell_ColumnFamilyNullOrEmpty = "Mapping failed: invalid cell, column family null or empty.";
	public static final String invalidCell_InvalidColF = "Mapping failed: invalid cell, invalid column family.";
	public static final String invalidCell_InvalidColQ = "Mapping failed: invalid cell, invalid column qualifier.";
	public static final String mappingFailed_MissingCell = "Mapping failed: missing cell.";
	public static final String invalidCell_MultipleInstance = "Mapping failed: invalid cell, cell with this column family and column qualifier prefix already processed.";
	public static final String invalidCell_InvalidColQ_CollectionFieldNameNull = "Mapping failed: invalid cell, invalid column qualifier, collection field name null.";
	public static final String invalidCell_InvalidColQ_InvalidCollectionFieldName = "Mapping failed: invalid cell, invalid column qualifier, invalid collection field name.";

	public static HyperMappingException getHME(Throwable e, String message) {
		HyperMappingException hme = new HyperMappingException(message);
		hme.initCause(e);
		return hme;
	}

	/**
	 * Returns a Hypertable cell list containing the fields of the HyperEntity passed as parameter.
	 * @param hyperEntity
	 * @return The Hypertable cell list
	 * @throws HyperMappingException
	 */
	public abstract List<Cell> hyperEntityToCellList(T hyperEntity) throws HyperMappingException;
	/**
	 * Returns a HyperEntity constructed from the Hypertable cells passed as parameter.
	 * @param cells
	 * @return The HyperEntity
	 * @throws HyperMappingException
	 */
	public abstract T cellListToHyperEntity(List<Cell> cells) throws HyperMappingException;
	/**
	 * Suitable for single cell HyperEntity types only, the method returns null for multiple
	 * cell HyperEntity types. Converts the HyperEntity passed as parameter to a Hypertable
	 * cell.
	 * @param hyperEntity
	 * @return The Hypertable cell
	 * @throws HyperMappingException
	 */
	public abstract Cell hyperEntityToCell(T hyperEntity) throws HyperMappingException;
	/**
	 * Suitable for single cell HyperEntity types only, the method returns null for multiple
	 * cell HyperEntity types. Converts the Hypertable cell passed as parameter to a
	 * HyperEntity.
	 * @param cell
	 * @return The HyperEntity
	 * @throws HyperMappingException
	 */
	public abstract T cellToHyperEntity(Cell cell) throws HyperMappingException;
	/**
	 * Tells if the mapper's HyperEntity type is single cell or not.
	 * @return
	 */
	public abstract boolean getIsSingleCell();

	/**
	 * Returns a Hypertable cell list constructed from the HyperEntity list passed
	 * as parameter. HyperEntity instances for which the cell list transformation
	 * fails will be skipped.
	 * @param hyperEntityList The HyperEntity list.
	 * @return The Hypertable cell list constructed from the HyperEntity list.
	 */
	public List<Cell> hyperEntityListToCellList(List<T> hyperEntityList) {
		if (hyperEntityList == null)
			return null;
		
		List<Cell> cells = new ArrayList<Cell>();
		for (T hyperEntity : hyperEntityList) {
			try {
				if (getIsSingleCell())
					cells.add(hyperEntityToCell(hyperEntity));
				else
					cells.addAll(hyperEntityToCellList(hyperEntity));
			} catch (HyperMappingException e) {
				// TODO: should log debug info?
				// do nothing, simply skip the invalid hyperEntity
			}
		}
		
		return cells;
	}

	/**
	 * Returns a HyperEntity list constructed from the Hypertable cells passed as
	 * parameter. Assumes that the cell list is ordered such that cells with the
	 * same row key follow each other. Invalid cells will be skipped.
	 * @param cells The Hypertable cell list.
	 * @return The HyperEntity list constructed from the Hypertable cells.
	 */
	public List<T> cellListToHyperEntityList(List<Cell> cells) {
		if (cells == null)
			return null;

		List<T> hyperEntityList = new ArrayList<T>();
		boolean firstCell = true;
		String currentCellRowKey = "";
		String lastRowKey = "";
		long currentCellTimestamp = 0L;
		
		Map<Long, Map<Long, List<Cell>>> completeCellMap = new HashMap<Long, Map<Long,List<Cell>>>(); // create a map by rowkey sequence number, it will contain a map by timestamp sequence number, it will contain cell lists
		
		Long rowKeySeqNr = 0L;
		
		for (Cell c : cells) {
			// null checks and related basic consistency check
			if (c == null)
				continue;
			Key key = c.getKey();
			if (key == null)
				continue;

			// read current cell timestamp
			currentCellTimestamp = key.getTimestamp();

			// read current cell row key
			currentCellRowKey = key.getRow();
			// unset row key means use row key of previous cell
			if (currentCellRowKey == null || currentCellRowKey.isEmpty()) {
				if (lastRowKey.isEmpty())
					continue; // lastRowKey not initialized yet, in this case unset row key means invalid cell, skip
				currentCellRowKey = lastRowKey;
			}

			// if first (valid) cell, init lastRowKey and lastTimestamp with first (valid) cell's data
			if (firstCell) {
				lastRowKey = currentCellRowKey;
				firstCell = false;
			}
			
			// at this point currentCellRowKey and currentCellTimestamp values are surely set correctly for the cell
			
			// cell processing method differs for single cell and multiple cell HyperEntity types
			
			if (getIsSingleCell()) {
				try {
					hyperEntityList.add(cellToHyperEntity(c));
				} catch (HyperMappingException e) {
					// TODO: should log debug info?
					// do nothing, simply skip the invalid cells
				}
			}
			else {
				// check if row key or timestamp changed
				boolean nextRowReached = (!currentCellRowKey.equals(lastRowKey));

				if (nextRowReached)
					lastRowKey = currentCellRowKey;
				if (nextRowReached)
					rowKeySeqNr++;

				if (!completeCellMap.containsKey(rowKeySeqNr))
					completeCellMap.put(rowKeySeqNr, new HashMap<Long, List<Cell>>());

				Map<Long, List<Cell>> rowKeyCellMap = completeCellMap.get(rowKeySeqNr);

				// use -currentCellTimestamp as key to ensure reverse iteration below
				if (!rowKeyCellMap.containsKey(-currentCellTimestamp))
					rowKeyCellMap.put(-currentCellTimestamp, new ArrayList<Cell>());
				rowKeyCellMap.get(-currentCellTimestamp).add(c);
			}
		}
		if (!getIsSingleCell())
			for (Map<Long, List<Cell>> rowKeyCellMap : completeCellMap.values())
				for (List<Cell> rowKeyTimestampCells : rowKeyCellMap.values())
					try {
						hyperEntityList.add(cellListToHyperEntity(rowKeyTimestampCells));
					} catch (HyperMappingException e) {
						// TODO: should log debug info?
						// do nothing, simply skip the invalid cells
					}

		return hyperEntityList;
	}

	/**
	 * Creates a Hypertable Cell with a timestamp specified.
	 * @param rowKey
	 * @param timestamp
	 * @param columnFamily
	 * @param columnQualifier
	 * @param value
	 * @return
	 */
	protected Cell getCell(String rowKey, long timestamp, String columnFamily, String columnQualifier, byte[] value) {
		// create key
		Key key = new Key();
		key.setColumn_family(columnFamily);
		if (columnQualifier != null && !columnQualifier.isEmpty())
			key.setColumn_qualifier(columnQualifier);
		key.setRow(rowKey);
		key.setTimestamp(timestamp);

		// create cell
		Cell cell = new Cell();
		cell.setKey(key);
		cell.setValue(value);

		return cell;
	}

	/**
	 * Creates a Hypertable Cell without a timestamp specified.
	 * @param rowKey
	 * @param columnFamily
	 * @param columnQualifier
	 * @param value
	 * @return
	 */
	protected Cell getCell(String rowKey, String columnFamily, String columnQualifier, byte[] value) {
		// create key
		Key key = new Key();
		key.setColumn_family(columnFamily);
		if (columnQualifier != null && !columnQualifier.isEmpty())
			key.setColumn_qualifier(columnQualifier);
		key.setRow(rowKey);

		// create cell
		Cell cell = new Cell();
		cell.setKey(key);
		cell.setValue(value);

		return cell;
	}
}
