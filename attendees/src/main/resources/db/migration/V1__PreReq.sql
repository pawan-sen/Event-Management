CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table EVENT_ATTENDEES_DETAILS (
	id UUID DEFAULT uuid_generate_v4(),
	eventId varchar(40),
	userId varchar(40),
	userName varchar(100),
	rsvpStatus varchar(50),
	userComments varchar(500),
	PRIMARY KEY (eventId, userId)
);
