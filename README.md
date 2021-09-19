# java-postgresql
Relational database working example with PostgreSQL and Java

This program assumes that schema named "ecom" exists in the PostgreSQL database. 
DB properties including the schema name can be updated in "db.properties" file.

#### Program behavior
1. The program will create tables like Users, Products, Reviews, Orders, OrderDetails.
2. Insert randomly generated data into the tables.
3. Test the performance of various queries/operations with running multiple threads randinging from 1-10 each time re-initializing the DB. 
Operations will be selected randomly based on predefined probability.

#### Program Execution (Gradle project)
run ./gradlew run to execute a program. 

#### Dependencies
Required dependencies include PostgreSQL Java Driver, and JFairy library
