package com.inepex.example;

import java.util.List;

import com.inepex.example.conf.HyperConfigurator;
import com.inepex.example.entity.Ticket;
import com.inepex.example.entity.TicketDao;
import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.mapper.HyperMappingException;

public class InsertApp {
	
	
	/**
	 * 
	 * Create table with Ticket.hql before run this app!
	 * 
	 */
	public static void main(String[] args) throws HyperOperationException, HyperMappingException {
		// CREATE A NEW TICKET
		Ticket newTicket = new Ticket();

		// Set the name of the server (this will be the key in Hypertable)
		newTicket.setServer("Blade66");

		// Set the timestamp of the Ticket
		newTicket.setTimestamp(System.currentTimeMillis());

		// Set the other properties of the Ticket
		newTicket.setTitle("Kernel update");
		newTicket
				.setDescription("New kernel version is available, you should update to it!");
		newTicket.setProblemType("UPDATE");
		newTicket.setPriority("HIGH");
		newTicket.setIsFixed(false);

		// CONFIGURING ACCESS TO THE DATABASE
		TicketDao dao = new TicketDao(HyperConfigurator.getHPA());

		// insert the Ticket
		dao.insert(newTicket);

		// get all of the tickets
		List<Ticket> ticket_result = dao.selectAll();
		System.out.println("Result size: "+ticket_result.size());
		for (Ticket t : ticket_result) {
			System.out.println("Server: " + t.getServer());
			System.out.println("Title: " + t.getTitle());
			System.out.println("Description: " + t.getDescription());
			System.out.println("Problem type: " + t.getProblemType());
			System.out.println("Priority: " + t.getPriority());
			System.out.println("Is Fixed? " + t.getIsFixed());
			System.out.println();
		}
	}

}
