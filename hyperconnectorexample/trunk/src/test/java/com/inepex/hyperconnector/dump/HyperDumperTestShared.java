package com.inepex.hyperconnector.dump;

import java.io.File;
import java.util.Random;

import com.inepex.example.entity.Ticket;

public class HyperDumperTestShared {
	
	public static Ticket getTestTicket() {
		Random r = new Random();
		Ticket ticket = new Ticket();
		ticket.setDescription("Descr. of test ticket");
		ticket.setIsFixed(false);
		ticket.setPriority("PR"+r.nextInt(10));
		ticket.setProblemType("task");
		ticket.setServer("inf-"+(r.nextInt(23)+10)+".domain.com");
		ticket.setTimestamp(System.currentTimeMillis());
		ticket.setTitle("Test ticket");
		return ticket;
	}
	
	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns false.
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

}
