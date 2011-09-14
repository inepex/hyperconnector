package com.inepex.example;

import java.util.List;

import org.junit.Test;

import com.inepex.example.conf.HyperConfigurator;
import com.inepex.example.dao.TicketDao;
import com.inepex.hyperconnector.dao.HyperOperationException;
import com.inepex.hyperconnector.mapper.HyperMappingException;

public class InsertAndSelectTest {
	@Test
	public void insertTickets() throws HyperOperationException, HyperMappingException{
		//CREATE A NEW TICKET
		Ticket server_update = new Ticket();
		
		//Set the name of the server (this will be the key in Hypertable)
		server_update.setServer("Blade66");

		//Set the timestamp of the Ticket
		server_update.setTimestamp( System.currentTimeMillis() );
		
		//Set the other properties of the Ticket
		server_update.setTitle("Kernel update");
		server_update.setDescription("New kernel version is available, you should update to it!");
		server_update.setProblemType("UPDATE");
		server_update.setPriority("HIGH");
		server_update.setIsFixed(false);
		
		//CONFIGURING ACCESS TO THE DATABASE
		TicketDao dao = new TicketDao( HyperConfigurator.getHPA() );
		
		//insert the Ticket
		dao.insert(server_update);
		
		//get all of the tickets
		List<Ticket> ticket_result = dao.selectAll();
		
		for(Ticket t : ticket_result){
			System.out.println("Server: " + t.getServer());
			System.out.println("Title: " + t.getTitle());
			System.out.println("Description: " + t.getDescription());
			System.out.println("Problem type: " + t.getProblemType());
			System.out.println("Priority: " + t.getPriority());
			System.out.println("Is Fixed?" + t.getIsFixed());
		}
	}
}
