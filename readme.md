# Gestionnaire de Ligues - Application Java

Ce projet est une application de gestion des ligues sportives ou associatives, développée en Java avec Swing pour l'interface graphique.

Elle permet aux différents types d'utilisateurs de gérer les ligues, leurs responsables, et les membres.

## Fonctionnalités principales

- **Gestion des Ligues** :
  - Créer une nouvelle ligue
  - Renommer ou supprimer une ligue

- **Gestion des Responsables** :
  - Un utilisateur de niveau 1 peut affecter un responsable (niveau 2) à une ligue
  - Le responsable peut ensuite gérer sa ligue (ajouter / retirer des membres)

- **Gestion des Membres** :
  - Ajouter de nouveaux utilisateurs (niveaux 1 et 2)
  - Supprimer des utilisateurs
  - L'affectation des membres à une ligue se fait par une autre interface dédiée (pas depuis la fiche ligue)

## Rôles et droits

- **Niveau 1** (Super Administrateur) :
  - Tous les droits : création / suppression / modification des ligues et des utilisateurs
  - Peut affecter les responsables de ligue (niveau 2)

- **Niveau 2** (Responsable de ligue) :
  - Gère uniquement sa propre ligue : ajout / suppression / modification des membres de sa ligue

- **Niveau 3** (Utilisateur standard) :
  - Ne dispose d'aucun droit de gestion

## Objectif

Le projet a été réalisé dans un cadre pédagogique (BTS SIO SLAM).  
L'objectif était d'apprendre à :

- Concevoir une interface graphique avec Java Swing
- Gérer des droits utilisateurs
- Intégrer une base de données (MySQL)
- Mettre en place un système de gestion multi-rôle (RBAC)
