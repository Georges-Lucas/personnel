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
id_employe int PRIMARY KEY NOT NULL AUTO_INCREMENT,
nom_employe varchar(100),
prenom_employe varchar(100),
email_employe varchar(100),
mdp_employe varchar (32),
date_arrive DATE,
date_depart DATE,
id_role int,
id_ligue,
FOREIGN KEY (id_role) REFERENCES ROLE(id_role),
FOREIGN KEY (id_ligue) REFERENCES LIGUE(id_ligue)

);

CREATE TABLE NIVEAU_ACCES (
id_niveau_acces int PRIMARY KEY NOT NULL AUTO_INCREMENT,
niveau_acces varchar(32) PRIMARY KEY
);

CREATE TABLE TRAVAIL (
id_travail int PRIMARY KEY NOT NULL AUTO_INCREMENT,
id_employe int,
nom_ligue int,
FOREIGN KEY (id_employe) REFERENCES EMPLOYE(id_employe),
FOREIGN KEY (nom_ligue) REFERENCES LIGUE(nom_ligue)
);

CREATE TABLE ROLE (
id_role int PRIMARY KEY NOT NULL AUTO_INCREMENT,
id_niveau_acces,
niveau_acces varchar(32)
);




DROP TABLE IF EXISTS TRAVAIL;
DROP TABLE IF EXISTS EMPLOYE;
DROP TABLE IF EXISTS ROLE;
DROP TABLE IF EXISTS NIVEAU_ACCES;
DROP TABLE IF EXISTS LIGUE;

CREATE TABLE LIGUE (
    id_ligue INT PRIMARY KEY AUTO_INCREMENT,
    nom_ligue VARCHAR(100)
);

CREATE TABLE NIVEAU_ACCES (
    id_niveau_acces INT PRIMARY KEY AUTO_INCREMENT,
    niveau_acces VARCHAR(32)
);

CREATE TABLE ROLE (
    id_role INT PRIMARY KEY AUTO_INCREMENT,
    id_niveau_acces INT,
    niveau_acces VARCHAR(32),
    FOREIGN KEY (id_niveau_acces) REFERENCES NIVEAU_ACCES(id_niveau_acces)
);

CREATE TABLE EMPLOYE (
    id_employe INT PRIMARY KEY AUTO_INCREMENT,
    nom_employe VARCHAR(100),
    prenom_employe VARCHAR(100),
    email_employe VARCHAR(100),
    mdp_employe VARCHAR(32),
    date_arrive DATE,
    date_depart DATE,
    id_role INT,
    id_ligue INT,
    FOREIGN KEY (id_role) REFERENCES ROLE(id_role),
    FOREIGN KEY (id_ligue) REFERENCES LIGUE(id_ligue)
);

CREATE TABLE TRAVAIL (
    id_travail INT PRIMARY KEY AUTO_INCREMENT,
    id_employe INT,
    id_ligue INT,
    FOREIGN KEY (id_employe) REFERENCES EMPLOYE(id_employe),
    FOREIGN KEY (id_ligue) REFERENCES LIGUE(id_ligue)
);