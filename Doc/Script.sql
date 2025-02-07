DROP TABLE LIGUE;
DROP TABLE EMPLOYE;
DROP TABLE NIVEAU_ACCES;
DROP TABLE ROLE;


CREATE TABLE LIGUE (
id_ligue int PRIMARY KEY NOT NULL AUTO_INCREMENT,
nom_ligue varchar(100)
);

CREATE TABLE EMPLOYE (
id_employe int PRIMARY KEY ,
nom_employe varchar(100),
prenom_employe varchar(100),
email_employe varchar(100),
mdp_employe varchar (32),
date_arrive DATE,
date_depart DATE,
id_role int,
id_ligue int,
FOREIGN KEY (id_ligue) REFERENCES LIGUE(id_ligue),
FOREIGN KEY (id_role) REFERENCES ROLE(id_role)
);

CREATE TABLE NIVEAU_ACCES (
id_niveau_acces int PRIMARY KEY NOT NULL AUTO_INCREMENT,
niveau_acces varchar(32) PRIMARY KEY
);


CREATE TABLE ROLE (
id_role int PRIMARY KEY NOT NULL AUTO_INCREMENT,
id_niveau_acces int,
FOREIGN KEY (id_niveau_acces) REFERENCES NIVEAU_ACCES(id_niveau_acces)
);


