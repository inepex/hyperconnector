package com.inepex.hyperconnector.dumpreader;

import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDaoImpl;
import com.inepex.hyperconnector.dump.HyperDumper;
import com.inepex.hyperconnector.dumpreader.HyperDumpReaderBufferedCellStream.BufferedCellStreamException;
import com.inepex.hyperconnector.serialization.util.DeviceUploadedDataSerializationUtil;
import com.inepex.hyperconnector.serialization.util.SerializationUtil;
import com.inepex.hyperconnector.thrift.HyperClientPool;
import com.inepex.hyperconnector.thrift.HyperHqlServicePool;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

import org.apache.thrift.transport.TTransportException;
import org.hypertable.thriftgen.Cell;

import tmp.DeviceEvent;
import tmp.DeviceEventMapper;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Application for restore the files of {@link HyperDumper}. Run the application with "help"
 * parameter for more details.
 */
public class HyperDumpRestoreApp {

    //temporaly stored
    private static String year = null;
    private static String month = null;

    //mandatory params
    private static String hyperAddress = null;
    private static Integer hyperPort = null;
    private static String baseFolder = null;
    private static String table = null;
    private static String nameSpace = null;
    private static String deviceId = null;

    //optional params
    private static HyperDumpReaderFilter filter = new HyperDumpReaderFilter();

    //hyper resources
    private static HyperPoolArgs hyperPoolArgs;
    private static BottomLevelDaoImpl daoImpl;

    public static void main(String[] args) {
        System.out.println(HyperDumpRestoreApp.class.getSimpleName()
                           + ": big file optimized version July/2014");
        if (args == null || args.length == 0) {
            printHelp();
            System.exit(-1);
        }

        if ("help".equals(args[0])) {
            printHelp();
            System.exit(0);
        }

        System.out.println("Parsing params....");
        parseParams(args);
        checkMandatories();

        System.out.println("Connection to hypertable...");
        createHyperResources();

        long cellInsertedSum = 0;
        List<File> corruptFiles = new LinkedList<>();

        try {
            for (File dumpFile : HyperDumpFiles.collectMatchingFiles(baseFolder, filter)) {
                long cellCountInfile = 0;
                HyperDumpReaderBufferedCellStream stream = new HyperDumpReaderBufferedCellStream(
                    dumpFile);
                Exception readException = null;

                try {
                    stream.open();

                    List<Cell> cells;
                    while (true) {
                        cells = stream.readCells();
                        if (cells.isEmpty()) {
                            break;
                        }

                        if (deviceId != null) {
                            System.out.println("Restoring only for: " + deviceId);
                            filterForDeviceId(cells);
                        }
                        if (table != null && table.equals("DeviceEvent")){
                        	dumpCells(cells);
                        }
                        cellCountInfile += cells.size();

                        System.out.println("Inserting cells...");
                        daoImpl.insertAndFlush(cells);
                        try {
                        	System.out.println("Waiting for 3 seconds");
                        	Thread.sleep(3000);
                        }catch (Exception e){
                        	
                        }
                    }
                } catch (BufferedCellStreamException e) {
                    if (!e.getAlreadyDecodedCells().isEmpty()) {
                        cellCountInfile += e.getAlreadyDecodedCells().size();
                        daoImpl.insertAndFlush(e.getAlreadyDecodedCells());
                    }

                    readException = e.getCause();
                    stream.close();
                } catch (HyperOperationException e) {
                    throw e;
                } catch (Exception e) {
                    readException = e;
                    stream.close();
                }

                System.out.print(dumpFile.getPath() + ": " + cellCountInfile + " cell in it...");
                cellInsertedSum += cellCountInfile;

                if (readException == null) {
                    System.out.println("  Ok!");
                } else {
                    System.out.println(" Corrupt file:");
                    readException.printStackTrace();
                    if (!(readException instanceof TTransportException)) {
                        System.out.println(
                            "There are maybe more unprocessed files, but program stops, cause this exception is not a 'well-known-currupt-file-indicator'.\n "
                            + "Check this situation manually!");
                        break;
                    } else {
                        corruptFiles.add(dumpFile);
                    }
                }
            }

            System.out.println("\nFinished ("
                               + cellInsertedSum
                               + " cells)------------------------------------");

            if (!corruptFiles.isEmpty()) {
                System.out.println();
                System.out.println("The list of corrupt files:");
                for (File corrupt : corruptFiles) {
                    System.out.println("\t" + corrupt.getPath());
                }
            }
        } catch (HyperOperationException ex) {
            ex.printStackTrace();
        } finally {
            closeHyperConnections();
        }
        
    }

    private static void filterForDeviceId(List<Cell> cells) {
        Iterator<Cell> cellIterator = cells.iterator();
        while (cellIterator.hasNext()) {
            String row = cellIterator.next().getKey().getRow();
            String cellDeviceId = getDeviceId(SerializationUtil.stringToByteArray(row));
            if (!deviceId.equals(cellDeviceId)) {
                cellIterator.remove();
            }
        }
    }

    private static String getDeviceId(byte[] value) {
        if (value == null) {
            return null; // TODO: throw?
        }
        byte[] valueBytes = DeviceUploadedDataSerializationUtil.bytesFromShiftedHexString(
            SerializationUtil.byteArrayToString(value));
        if (valueBytes.length != 16) {
            return null; // TODO: throw?
        }
        byte[] deviceIdBytes = new byte[8];
        byte[] creationTimestampBytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            deviceIdBytes[i] = valueBytes[i];
        }
        for (int i = 0; i < 8; i++) {
            creationTimestampBytes[i] = valueBytes[i + 8];
        }

        return String.valueOf(DeviceUploadedDataSerializationUtil.longFromOrderKeepingBytes(
            deviceIdBytes,
            0,
            false));
    }
    
    private static void dumpCells(List<Cell> cells){
    	try {
    	int i = 0;
    	for (Cell cell : cells){
    		DeviceEvent event = new DeviceEventMapper().cellToHyperEntity(cell);
    		System.out.println(event);
    		i++;
    		if (i>100) break;
    	}
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }

    private static void closeHyperConnections() {
        hyperPoolArgs.getHyperHqlServicePool().destroyAllResource();
        hyperPoolArgs.getHyperClientPool().destroyAllResource();
    }

    private static void createHyperResources() {
        HyperClientPool hyperClientPool = new HyperClientPool(hyperAddress, hyperPort);
        HyperHqlServicePool hyperHqlServicePool = new HyperHqlServicePool(hyperAddress, hyperPort);

        hyperPoolArgs = new HyperPoolArgs(hyperClientPool, hyperHqlServicePool, 10, 3, 3, 0, 0);

        daoImpl = new BottomLevelDaoImpl(hyperPoolArgs, nameSpace, table);
    }

    private static void printHelp() {
        System.out.println("-----------------------------\n"
                           +
                           "see also it's wiki page on https://github.com/inepex/hyperconnector/blob/master/wiki/HyperDumpRestoreApp.wiki"
                           +
                           "\n"
                           +
                           "Mandatory params:\n"
                           +
                           "\t hyperAddress - url or ip of target hypertable\n"
                           +
                           "\t hyperPort - port of hypertable\n"
                           +
                           "\t baseFolder - base folder of hyper dump hierarchy\n"
                           +
                           "\t nameSpace - name of namespace\n"
                           +
                           "\t table - name of table\n"
                           +
                           "\n"
                           +
                           "Optional params (default value is every):\n"
                           +
                           "\t year - number of [1999,infinity) if you set year you must set month too\n"
                           +
                           "\t month - number of [1,12] if you set month you must set year too\n"
                           +
                           "\t day - number of [1,31] OR number range from of [1,31] inclusive to of [2,32] exclusive like: 1-15 or 1-32\n"
                           +
                           "\t hour - number of [0, 23] OR number range from of [0, 23] inclusive to of [1, 24] exclusive like: 2-14 or 0-24\n"
                           +
                           "\t content - one of: DELETE_ONLY, INSERT_ONLY, BOTH\n"
                           +
                           "\t deviceId - ID of the device you want to run restore on\n"
                           +
                           "\n"
                           +
                           "minimal params to invoke:\n"
                           +
                           "\tHyperDumpRestoreApp hyperAddress=localhost hyperPort=38080 baseFolder=/backup/hyperDump/ nameSpace=InepexLbs table=Report\n\n"
                           +
                           "sample invoke:\n"
                           +
                           "\tHyperDumpRestoreApp hyperAddress=localhost hyperPort=38080 baseFolder=/backup/hyperDump/ nameSpace=InepexLbs table=Report year=2012 month=12 day=12-25 content=BOTH\n");
    }

    private static void checkMandatories() {
        if (hyperAddress == null) {
            mustBeSet("hyperAddress");
        }
        if (hyperPort == null) {
            mustBeSet("hyperPort");
        }
        if (baseFolder == null) {
            mustBeSet("baseFolder");
        }
        if (table == null) {
            mustBeSet("table");
        }
        if (nameSpace == null) {
            mustBeSet("nameSpace");
        }
    }

    private static void parseParams(String[] args) {
        for (String param : args) {
            if (param.indexOf('=') == -1) {
                invalidParam(param);
            }

            String[] splittedParam = param.split("=");
            String name = splittedParam[0];
            String value = splittedParam[1];

            if ("hyperAddress".equals(name)) {
                hyperAddress = value;
            } else if ("hyperPort".equals(name)) {
                hyperPort = Integer.parseInt(value);
            } else if ("baseFolder".equals(name)) {
                baseFolder = value;
            } else if ("table".equals(name)) {
                table = value;
                filter.table(value);
            } else if ("nameSpace".equals(name)) {
                nameSpace = value;
                filter.nameSpace(value);
            } else if ("year".equals(name)) {
                year = value;
            } else if ("month".equals(name)) {
                month = value;
            } else if ("day".equals(name)) {
                if (value.indexOf('-') != -1) {
                    String[] splittedVal = value.split("-");
                    filter.day(Integer.parseInt(splittedVal[0]), Integer.parseInt(splittedVal[1]));
                } else {
                    filter.day(Integer.parseInt(value));
                }
            } else if ("hour".equals(name)) {
                if (value.indexOf('-') != -1) {
                    String[] splittedVal = value.split("-");
                    filter.hour(Integer.parseInt(splittedVal[0]), Integer.parseInt(splittedVal[1]));
                } else {
                    filter.hour(Integer.parseInt(value));
                }
            } else if ("content".equals(name)) {
                filter.content(HyperDumpReaderFilter.Content.valueOf(value));
            } else if ("deviceId".equals(name)) {
                deviceId = value;
            } else {
                paramNameMistyped(name);
            }
        }

        if (year != null && month != null) {
            filter.yearAndMonth(Integer.parseInt(year), Integer.parseInt(month));
        } else {
            if (year != null && month != null) {
                invalidYearMonth();
            }
        }
    }

    private static void mustBeSet(String name) {
        System.err.println("Param " + name + " must be set!");
        System.err.println();
        printHelp();
        System.exit(-1);
    }

    private static void invalidYearMonth() {
        System.err.println("You must set both year and month params too. Or neither.");
        System.err.println();
        printHelp();
        System.exit(-1);
    }

    private static void paramNameMistyped(String name) {
        System.err.println("Invalid param name: " + name);
        System.err.println();
        printHelp();
        System.exit(-1);
    }

    private static void invalidParam(String param) {
        System.err.println("Invalid param: " + param + " The right param format is: name=value");
        System.err.println();
        printHelp();
        System.exit(-1);
    }
}
