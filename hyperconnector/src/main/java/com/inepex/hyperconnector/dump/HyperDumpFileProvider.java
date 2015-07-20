package com.inepex.hyperconnector.dump;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.SnappyOutputStream;

import com.inepex.hyperconnector.ApplicationDelegate;

public class HyperDumpFileProvider {
	
	public final static String EXTRADELEXTENSION = ".del";
	public final static String EXTENSION = ".dump.snappy";
	public final static TimeZone UTC = TimeZone.getTimeZone("UTC");
	
	private final static int BLOCKSIZE = 2048; 
	private final static long ONE_MINUTE=60*1000;
	private final static long ONE_HOUR=60*ONE_MINUTE;
	private final static String datePathFormatText = "yyyy-MM"+File.separatorChar+"dd"+File.separatorChar+"HH";
	
	private final static Logger _logger = LoggerFactory
			.getLogger(HyperDumpFileProvider.class);
	
	private final NowProvider nowProvider;
	private final Map<Long, Map<String, OpenedStreams>> hourly = new HashMap<Long, Map<String, OpenedStreams>>();
	private final String baseDumpFolder;
	
	public HyperDumpFileProvider(NowProvider nowProvider,
			ApplicationDelegate delegate,
			String baseDumpFolder) {
		this.nowProvider=nowProvider;
		this.baseDumpFolder=baseDumpFolder;
		
		delegate.addShutDownTask(new Runnable() {
			
			@Override
			public void run() {
				HyperDumpFileProvider.this.closeAllFile();
			}
		});
		
		delegate.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				_logger.info("Hourly hyper dumper closing started!");
				HyperDumpFileProvider.this.closeOldFiles();
			}
		}, delayUntilFirstClosingStart(), ONE_HOUR, TimeUnit.MILLISECONDS); 
	}
	
	long delayUntilFirstClosingStart() {
		long now = nowProvider.now();
		
		//next XX:05 
		return (now/ONE_HOUR + 1)*ONE_HOUR + ONE_MINUTE*5-now;
	}
	
	String getBaseDumpFolder() {
		return baseDumpFolder;
	}
	
	void closeAllFile() {
		closeYoungerThan(Long.MAX_VALUE);
	}
	
	/**
	 * invoke it scheduled in every hour 3-4 minutes after the last passed
	 * 
	 */
	void closeOldFiles() {
		long hourCount = nowProvider.now()/ONE_HOUR;
		closeYoungerThan(hourCount);
	}
	
	private synchronized void closeYoungerThan(long hourCount) {
		Iterator<Map.Entry<Long, Map<String, OpenedStreams>>> entries = hourly.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry<Long, Map<String, OpenedStreams>> entry=entries.next();
			if(entry.getKey()<hourCount) {
				for(Map.Entry<String, OpenedStreams> opened: entry.getValue().entrySet()) {
					_logger.info("Closing file for hour: {} table: {}", entry.getKey(), opened.getKey());
					closeStreams(opened.getValue());
				}
				
				entries.remove();
			}
		}
	}
	
	public synchronized TBinaryProtocol getStreamFor(String namespace, String tableName, boolean isDelete) throws IOException {
		long now = nowProvider.now();
		long hourCount = now/ONE_HOUR;
		
		Map<String, OpenedStreams> openedStreamsByName = hourly.get(hourCount);
		if(openedStreamsByName==null) {
			openedStreamsByName=new HashMap<String, OpenedStreams>();
			hourly.put(hourCount, openedStreamsByName);
		}
		
		String name=new StringBuilder(namespace)
			.append(":::")
			.append(tableName)
			.append(":::")
			.append(isDelete ? "delete" : "insert")
			.toString();
		
		OpenedStreams streams = openedStreamsByName.get(name);
		if(streams==null) {
			streams=createStreams(now, namespace, tableName, isDelete);
			openedStreamsByName.put(name, streams);
		}
		
		if(!streams.file.exists())
			throw new IOException("File " + streams.file.getAbsolutePath() + " does'nt exits anymore.");
		
		return streams.binaryProtocol;
	}

	String getDumpFileName(long now, String namespace, String tableName, boolean isDelete) {
		SimpleDateFormat datePathFormat = new SimpleDateFormat(datePathFormatText);
		datePathFormat.setTimeZone(UTC);
		
		StringBuffer sb = new StringBuffer();
		sb.append(baseDumpFolder)
		  .append(File.separatorChar)
		  .append(namespace)
		  .append(File.separatorChar)
		  .append(tableName)
		  .append(File.separatorChar)
		  .append(datePathFormat.format(now))
		  .append("_UTC");
		
		if(isDelete)
			sb.append(EXTRADELEXTENSION);
	
		sb.append(EXTENSION);
		return sb.toString();
	}
	
	OpenedStreams createStreams(long now, String namespace, String tableName, boolean isDelete) throws IOException {
		File file = outFile(now, namespace, tableName, isDelete);
		FileOutputStream fos = new FileOutputStream(file, false);
		SnappyOutputStream snStream = new SnappyOutputStream(fos, BLOCKSIZE);
		
		try {
			TIOStreamTransport fileTransport = new TIOStreamTransport(snStream);
			TBinaryProtocol prot = new TBinaryProtocol(fileTransport);
			fileTransport.open();
			return new OpenedStreams(file, prot, snStream);
			
		} catch (TTransportException e) {
			_logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	private File outFile(long now, String namespace, String tableName, boolean isDelete) {
		String pathAndFile = getDumpFileName(now, namespace, tableName, isDelete);
		
		createFoldersIfNeed(pathAndFile);
		
		File file = new File(pathAndFile);
		if(file.exists()) {
			int i=0;
			do {
				i++;
				file = new File(pathAndFile+"_"+i);				
			} while(file.exists());
		}
		
		return file;
	}

	private static void createFoldersIfNeed(String pathAndFile) {
		File file = new File(pathAndFile);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
	}
	
	void closeStreams(OpenedStreams streams) {
		synchronized(streams.binaryProtocol) {
			try {
				streams.binaryProtocol.getTransport().flush();
			} catch (TTransportException e) {
				_logger.error(e.getMessage(), e);
			}
	
			streams.binaryProtocol.getTransport().close();
	
			try {
				streams.snStream.close();
			} catch(IOException e) {
				_logger.error(e.getMessage(), e);
			}
		}
	}
	
	int hourlySize(){
		return hourly.size();
	}
	
	int openedCountForNow() {
		long hourCount = nowProvider.now()/ONE_HOUR;
		Map<String, OpenedStreams> opened = hourly.get(hourCount);
		if(opened==null)
			return 0;
		else
			return opened.size();
	}
	
	static class OpenedStreams {
		final File file;
		final TBinaryProtocol binaryProtocol;
		final SnappyOutputStream snStream;
		
		public OpenedStreams(File file, TBinaryProtocol binaryProtocol, SnappyOutputStream snStream) {
			this.binaryProtocol = binaryProtocol;
			this.snStream=snStream;
			this.file=file;
		}
	}
}
