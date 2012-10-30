package com.inepex.hyperconnector.dumpintegritytest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hypertable.thriftgen.Cell;

public class DumpRestoreSnapshots {

	private final Map<String, List<Cell>> snapsByName = new HashMap<String, List<Cell>>();
	
	public void storeSnapshot(String name, List<Cell> cells) {
		System.out.println("Snapshot created. It's name: "+name+" and size: "+cells.size());
		if(snapsByName.containsKey(name))
			err("Already has snapshot with name: "+ name);
			
		snapsByName.put(name, cells);
	}
	
	public void difference(String snapshot1, String snapshot2) {
		System.out.println("Check difference between "+snapshot1+" and "+snapshot2);
		List<Cell> s1 = snapsByName.get(snapshot1);
		if(s1==null)
			err("No snapshot for name: "+snapshot1);
		
		List<Cell> s2 = snapsByName.get(snapshot2);
		if(s2==null)
			err("No snapshot for name: "+snapshot2);
		
		
		if(s1.size()!=s2.size())
			err("Size doesn't match. s1: "+s1.size()+" s2: "+s2.size());
		
		for(int i=0; i<s1.size(); i++) {
			Cell c1 = s1.get(i);
			Cell c2 = s2.get(i).deepCopy();
			
			//revisions are never the same, but it's not problem 
			c2.getKey().setRevision(c1.getKey().getRevision());
			
			if(c1.getKey().compareTo(c2.getKey())!=0)
				err("Non-equal key for:\nkey1: "+c1.getKey()+"\nkey2: "+c2.getKey());
			
			if(!Arrays.equals(c1.getValue(), c2.getValue()))
				err("Non-equal values for:\nkey1: "+c1.getKey()+"\nkey2: "+c2.getKey());
		}
	}
	
	private void err(String txt) {
		System.err.println(txt);
		System.exit(-1);
	}
}
