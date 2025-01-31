CREATE TABLE ligue (
id_ligue int PRIMARY KEY NOT NULL AUTO_INCREMENT,
nom_ligue varchar(100)
);

CREATE TABLE employe (
id_employe int PRIMARY KEY ,
nom_employe varchar(100),
prenom_employe varchar(100),
email_employe varchar(100),
mdp_employe varchar (32),
date_arrive DATE,
date_depart DATE
);

CREATE TABLE niveau_acces (
niveau_acces varchar(32) PRIMARY KEY
);

CREATE TABLE travail (
id_employe int,
id_ligue int,
FOREIGN KEY (id_employe) REFERENCES employe(id_employe),
FOREIGN KEY (id_ligue) REFERENCES ligue(id_ligue)
);

CREATE TABLE role (
id_employe int,
niveau_acces varchar(32),
FOREIGN KEY (id_employe) REFERENCES employe(id_employe),
FOREIGN KEY (niveau_acces) REFERENCES niveau_acces(niveau_acces)
);
