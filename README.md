# Loan Application

## Technologies used
1. Java 20
2. Maven
3. Spring Boot 3.1.3
4. MySQL
   
## Steps to setup the application
1. Install JDK and MySQL workbench in your system.
2. Inside MySQL worbench, create an schema named 'loan-db'
3. Run the application as Spring Boot Project.
4. Application will run on the url: http://localhost:{server.port}  [server.port value is mentioned in application.properties file]

## Project Description 

Project is divided into 2 main parts :

### Customer Service
This part contains the logic to create, update, delete, and fetch customers. It also includes a utility API for testing, which provides base64 encoded customerId:password. This encoded string is used while calling loan-related APIs.

### Loan Service
This part includes the logic to create, display, approve, and repay loans.
Loan applications are created by users but can only be approved by admins. Admins will approve loans for customers with a credit score greater than 700 and those who are full-time employees. Customers can retrieve pending/paid loan repayment statuses. The current assumption is a weekly repayment frequency.

## APIs:

### Customer APIs
1. Create Customer
2. Update Customer
3. Get All Customers
4. Get Customer By Id
5. Delete customer
6. Fetch Authorization detail for aCustomer

### Loan APIs
1. Create Loan: Performed by a customer.
2. Fetch Loans: Performed by a customer.
3. Approve Loan: Performed by an admin.
4. Repay Loan: Performed by a customer.

Loan APIs need authentication. 

### Admin user
user: admin
password: password

### Customer user
user: <customerId>
password: password

Basic <Base64 encoded<username:password>> needs to be passed as Authorization Header for calling loan APIs.
