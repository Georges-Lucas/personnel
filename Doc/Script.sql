DROP TABLE LIGUE;
DROP TABLE EMPLOYE;
DROP TABLE NIVEAU_ACCES;
DROPT TABLE TRAVAIL;
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
date_depart DATE
);

CREATE TABLE NIVEAU_ACCES (
niveau_acces varchar(32) PRIMARY KEY
);

CREATE TABLE TRAVAIL (
id_employe int,
id_ligue int,
FOREIGN KEY (id_employe) REFERENCES EMPLOYE(id_employe),
FOREIGN KEY (id_ligue) REFERENCES LIGUE(id_ligue)
);

CREATE TABLE ROLE (
id_employe int,
niveau_acces varchar(32),
FOREIGN KEY (id_employe) REFERENCES EMPLOYE(id_employe),
FOREIGN KEY (niveau_acces) REFERENCES NIVEAU_ACCES(niveau_acces)
);


