DROP TABLE LIGUE;
DROP TABLE EMPLOYE;
DROP TABLE NIVEAU_ACCES;
DROP TABLE TRAVAIL;
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
id_niveau_acces int PRIMARY KEY NOT NULL AUTO_INCREMENT,
niveau_acces varchar(32) PRIMARY KEY
);

CREATE TABLE TRAVAIL (
id_employe int,
nom_ligue int,
FOREIGN KEY (id_employe) REFERENCES EMPLOYE(id_employe),
FOREIGN KEY (nom_igue) REFERENCES LIGUE(nom_ligue)
);

CREATE TABLE ROLE (
id_role int PRIMARY KEY NOT NULL AUTO_INCREMENT;
niveau_acces varchar(32),
id_employe int,
FOREIGN KEY (niveau_acces) REFERENCES NIVEAU_ACCES(niveau_acces),
FOREIGN KEY (id_employe) REFERENCES EMPLOYE(id_employe)
);


