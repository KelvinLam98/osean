# --- !Ups

CREATE TABLE `PersonalData` (
  `id` varchar(128) not null,
  `Name` varchar(64) not null,
  `Age` varchar(64) not null,
  `Address` varchar(128) not null,
  primary key (`id`)
);

# --- !Downs

DROP TABLE `PersonalData`;