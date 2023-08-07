# PayMyBuddy
Application Web : partager de l'argent entre ami via une application web

PaymyBuddy représente une plateforme web dédié au transfert d'argent entre ses utilisteurs.

## Prerequis
- Java JDK 8
- Maven
- MySQL Server

## Technologies 
- Java et le framework SpringBoot pour la gestion de la logique coté serveur garantissant une application sécurisé.
- Du coté Front, la conception a été réalisé en utilisant Thymeleaf et Bootstrap
- Comme base de donné l'application s'appuie sur MySQL

## Fonctionnalités
  - Inscription et authentification des utilisateurs
  - Ajout de contacts d'amis
  - Effectuer des transfert d'argent entre amis
  - Historique des transactions
  - Sécurité renforcée avec Spring Security
  - Intégration avec une base de données MySQL

## Instalation 

1. Cloner le débot sur votre environnement
2. Configurer une base de données :
   - Créez une base de donnés MySQL nommée paymybuddy
   - Importer le schema de la base de donnée avec le fichier : schema.sql
   - Mettez à jour les informations de connexion à la base de données dans src/main/resources/application.properties

## Utilisation
- Accédez à l'application via l'adresse : http://localhost:8080
- Inscrivez-vous en tant qu'utilisateur et connectez-vous.
- Ajoutez des amis en utilisant leurs adresses e-mail
- Effectuez des transferts d'argent entre amis.
- Consultez l'historique des transactions pour suivre vos activités.


    
   
