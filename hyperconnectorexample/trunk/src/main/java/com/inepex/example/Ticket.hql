USE "/";

CREATE NAMESPACE TestUserSupportNameSpace;

USE UserSupport;

CREATE TABLE Ticket(description,
	isFixed,
	priority,
	problemtype,
	title);