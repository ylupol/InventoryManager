CREATE TABLE item(
  id INTEGER GENERATED BY DEFAULT AS IDENTITY,
  name VARCHAR (256) NOT NULL,
  location_id INTEGER NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE item_events(
  item_id INTEGER,
  events_id INTEGER
);

CREATE TABLE event(
  id INTEGER GENERATED BY DEFAULT AS IDENTITY,
  time TIMESTAMP NOT NULL,
  event_type VARCHAR (256) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE item_location(
  item_id INTEGER,
  location_id INTEGER
);