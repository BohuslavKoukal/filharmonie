# filharmonie

Aim of this project is to integrate some of the Czech Philharmonic systems

Database: Application is using PostgreSQL DB. If you want to use another DB, you have to put maven dependency for its driver into pom.xml. You setup your db credentials in DatabaseConfig.java. In database create table entity, corresponding with Entity.java (with automatically generated IDs).

Usage: Entry point is serverAddress/orchestr (set up serverAddress in resources/STringConstants ), there you can simulate REST post and put actions for CPAction resource. POST makes changes in database while PUT just sends messages to other components.
