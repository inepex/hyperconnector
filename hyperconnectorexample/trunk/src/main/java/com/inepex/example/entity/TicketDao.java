package com.inepex.example.entity;

import com.inepex.hyperconnector.dao.HyperDao;
import com.inepex.hyperconnector.dao.bottom.BottomLevelDaoImpl;
import com.inepex.hyperconnector.thrift.HyperPoolArgs;

public class TicketDao extends HyperDao<Ticket> {
	
	public static final String namespace = "TestUserSupportNameSpace";
	public static final String table = "Ticket";

	protected static final String column_description = "description";
	protected static final String column_isFixed = "isFixed";
	protected static final String column_priority = "priority";
	protected static final String column_problemtype = "problemtype";
	protected static final String column_title = "title";

	protected static final String firstColumn = "description";
	protected static final String lastColumn = "title";

	public TicketDao(HyperPoolArgs hyperClientPoolArgs) {
		super(new BottomLevelDaoImpl(hyperClientPoolArgs, namespace, table),
				new TicketMapper());
	}

}