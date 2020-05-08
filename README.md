# Zinger-Framework
An Open Source Framework for building Hyperlocal Delivery Mobile and Web applications.
Zinger provides developers easy to handle backend for ordering & delivering foods, groceries, fruits, vegetables, medicines, etc.




## Architecture
<img src="https://raw.githubusercontent.com/ddlogesh/zinger-framework-docs/master/website/static/img/zinger/zinger-arch.png" height=75% width=75%/>

#### Zinger Core
&emsp;&emsp;A backend server that exposes the REST API, written in spring boot using MySQL database.

#### Zinger Client
&emsp;&emsp;A customer side application to place and track your orders from multiple store partners near your location. It allows you to browse catalogs of all stores and provides you a seamless shopping experience from order acceptance to last mile delivery.

#### Zinger Partner
&emsp;&emsp;A one-stop solution for store owners to manage and track orders from their clients and it provides an effective order and inventory management solutions.

#### Zinger Admin
&emsp;&emsp;A web console to monitor the zinger client and partner applications. It is a powerful tool to track application performance, manage outlet information and promotes new arrivals to increase brand engagement.



## Database Schema 
Check out the [database schema](https://drawsql.app/zinger-technologies/diagrams/zinger-framework)



## Installation Setup


### Prerequisites

#### Software Packages
* Java **8** or newer
* MySQL **8.0.19**
* Spring Boot **2.2.4**

#### Preferred IDE
* Eclipse JavaEE IDE
* IntelliJ IDEA
* MySQL Work Bench

#### Basic Knowledge  
* MySQL Procedure and Trigger

### Fork Project

* [Fork](https://github.com/ddlogesh/zinger-framework/fork) the Main Repository
* Clone the forked repository locally `git clone forked_repo_url`

### Setup DB

* Open MySQL Workbench
* Run the [DB_INIT.sql](https://github.com/ddlogesh/zinger-framework/blob/master/sql/DB_INIT.sql) script to create the tables, indexes and triggers.
* Run the [DB_PROC.sql](https://github.com/ddlogesh/zinger-framework/blob/master/sql/DB_PROC.sql) script to create the procedures.
* Run the [DB_INSERT.sql](https://github.com/ddlogesh/zinger-framework/blob/master/sql/DB_INSERT.sql) script to populate the database.
* Set your MySQL username and password in [application.properties](https://github.com/ddlogesh/zinger-framework/blob/master/src/main/resources/application.properties)

### Setup Firebase Admin SDK
* Create a new [Firebase](https://console.firebase.google.com/) project 
* Navigate to `Project setting -> Service accounts -> Generate new private key`
* Set your credentials file path in [application.properties](https://github.com/ddlogesh/zinger-framework/blob/master/src/main/resources/application.properties)



### Build
#### Eclipse IDE
* Choose `File -> Import -> Maven -> Existing Maven projects`
* Right-click [MainApplication.java](https://github.com/ddlogesh/zinger-framework/blob/master/src/main/java/com/food/ordering/zinger/MainApplication.java) and choose `Run As -> Java Application`  

#### IntelliJ IDEA
* Choose `File -> Open` and choose the project location
* Right-click [MainApplication.java](https://github.com/ddlogesh/zinger-framework/blob/master/src/main/java/com/food/ordering/zinger/MainApplication.java) and choose `Run MainApplication.main()` 

### Test
* Visit [localhost:8080](localhost:8080) in the browser to check if the setup is successful.
* Test the endpoints in Postman by importing the [API collection](https://github.com/ddlogesh/zinger-framework/blob/master/Zinger%20framework%20(Release).json)
* For detailed API documentation, please refer [API Docs](https://documenter.getpostman.com/view/6369926/Szmb6KVo?version=latest)

### Deploy
If you would like to integrate with any mobile or web application, use [Ngrok](https://ngrok.com/download) to get a public URL of 
your localhost. Also, you can host in any of the online cloud service providers like [Heruko](https://www.heroku.com/), [Azure](https://azure.microsoft.com/), [AWS](https://aws.amazon.com/), etc.







