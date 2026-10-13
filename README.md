# SecureBank &ndash; Java-Based Banking Management System

**SecureBank** is an enterprise-grade retail banking web application built for a college micro-project. It adheres strictly to the core Java syllabus (Modules I &ndash; V) without any modern framework bloat (no Spring, no Hibernate, no JPA, no JavaScript frameworks, no Bootstrap, and no JSTL). The backend is **100% Java (Servlets 4.0 + JSP + JDBC + MySQL)** and the frontend is **pure, professional HTML and CSS**.

---

## 1. Prerequisites

Before running the project, ensure you have the following installed:
1. **Java Development Kit (JDK 17 or higher)**
   - Test with: `java -version` and `javac -version`
2. **MySQL Database Server (v8.0 or higher)**
   - Ensure MySQL service is running.
3. **Apache Tomcat 9**
   - Must be Tomcat 9 (supports `javax.servlet` API 4.0, **not** Tomcat 10+ which uses `jakarta`).
4. **Apache Maven (or the bundled Maven Wrapper `mvnw.cmd` / `mvnw`)**
   - The repository includes Maven wrapper files (`mvnw`, `mvnw.cmd`, `.mvn/`).

---

## 2. MySQL Database Setup

1. Open MySQL Command Line Client or MySQL Workbench:
   ```bash
   mysql -u root -p
   ```
2. Run the provided database schema script:
   ```sql
   source /path/to/SecureBank/db/schema.sql;
   ```
   *(Or copy and paste the contents of `db/schema.sql` directly into MySQL).*
3. This creates the database `securebank_db` and the 4 clean, unseeded tables:
   - `users`: stores Customer and Admin credentials with salted SHA-256 hashes.
   - `accounts`: stores account balances, polymorphic types (`SAVINGS`, `CURRENT`), and status (`ACTIVE`, `INACTIVE`).
   - `transactions`: stores ledger entries (`DEPOSIT`, `WITHDRAW`, `TRANSFER_OUT`, `TRANSFER_IN`).
   - `password_reset_tokens`: stores 15-minute one-time recovery token hashes.
4. **Rule**: Tables start 100% empty. There is **no fake or dummy data**. All users and transactions are created dynamically.

---

## 3. Database Credentials Configuration

Open the file:
```
src/main/resources/db.properties
```
Update your MySQL username and password:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/securebank_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

*(Alternatively, you can set system environment variables `SECUREBANK_DB_USER` and `SECUREBANK_DB_PASS` without editing the file).*

---

## 4. How to Build the WAR and Deploy to Tomcat 9

### Step 4.1: Build the Project
Open terminal / PowerShell in the project root directory and run:

**Using Maven:**
```bash
mvn clean package
```
**Or using the bundled Maven Wrapper (Windows PowerShell):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.4.7-hotspot"  # or your JDK path
.\mvnw.cmd clean package
```

This compiles all classes and generates the production artifact:
```
target/SecureBank.war
```

### Step 4.2: Deploy to Apache Tomcat 9
1. Copy `target/SecureBank.war` to your Tomcat's `webapps/` folder:
   ```powershell
   Copy-Item target\SecureBank.war C:\apache-tomcat-9.0.xx\webapps\
   ```
2. Start Tomcat:
   ```powershell
   C:\apache-tomcat-9.0.xx\bin\startup.bat
   ```
   Tomcat will automatically extract `SecureBank.war` into `SecureBank/`.

---

## 5. Web Application URLs

- **Public Home Page:** [http://localhost:8080/SecureBank/](http://localhost:8080/SecureBank/)
- **Customer Sign In:** [http://localhost:8080/SecureBank/user/login](http://localhost:8080/SecureBank/user/login)
- **Customer Registration:** [http://localhost:8080/SecureBank/user/register](http://localhost:8080/SecureBank/user/register)
- **First-Run Administrator Setup:** [http://localhost:8080/SecureBank/admin/setup](http://localhost:8080/SecureBank/admin/setup)
- **Administrator Sign In:** [http://localhost:8080/SecureBank/admin/login](http://localhost:8080/SecureBank/admin/login)

---

## 6. First-Run Administrator Setup Guide

Because the database contains zero dummy records, SecureBank provides a protected one-time setup mechanism:
1. Open [http://localhost:8080/SecureBank/admin/setup](http://localhost:8080/SecureBank/admin/setup)
2. Enter the administrator's details:
   - Full Name: `System Administrator`
   - Email: `admin@securebank.com`
   - Phone: `9876543210`
   - Department: `Central Banking Operations`
   - Password: `adminPassword123`
3. Click **"Initialize System & Create Admin"**.
4. Once created, `/admin/setup` permanently blocks access and redirects away. You can then log in at `/admin/login`.

---

## 7. Complete Routes Table

| Route | Role | Methods | Description |
|---|---|---|---|
| `/` or `/home` | Public | GET | Home landing page showing bank overview and access links |
| `/user/register` | Public | GET, POST | Customer registration, opens Savings/Current account with 12-digit number |
| `/user/login` | Public | GET, POST | Customer authentication with password verification & active status check |
| `/user/dashboard` | USER | GET | Account summary, balance enquiry, last 5 transactions |
| `/user/deposit` | USER | GET, POST | Synchronized cash deposit; displays electronic Receipt |
| `/user/withdraw` | USER | GET, POST | Synchronized withdrawal with minimum balance validation |
| `/user/transfer` | USER | GET, POST | Atomic peer-to-peer funds transfer with JDBC transaction rollback |
| `/user/transactions`| USER | GET | Transaction history with Stream/Lambda filtering and aggregates |
| `/user/statement` | USER | GET | Character stream download of full statement as `.txt` |
| `/user/profile` | USER | GET, POST | Update name/phone and change password |
| `/user/forgot-password`| Public | GET, POST | Generates 15-minute reset token, displays demo link |
| `/user/reset-password` | Public | GET, POST | Validates reset token and sets new password |
| `/user/logout` | USER | GET | Invalidates session and logs out customer |
| `/admin/setup` | Public (First run) | GET, POST | One-time setup active only when 0 admins exist |
| `/admin/login` | Public | GET, POST | Administrator authentication |
| `/admin/dashboard` | ADMIN | GET | Real-time platform KPI statistics and recent accounts |
| `/admin/customers` | ADMIN | GET | Customer directory with Stream/Lambda multi-field search |
| `/admin/customers/add`| ADMIN | GET, POST | Admin tool to enroll customer and assign initial deposit |
| `/admin/customer-details`| ADMIN | GET | Account inspector with static nested class summary |
| `/admin/toggle-status`| ADMIN | POST | Deactivate or activate customer accounts |
| `/admin/logout` | ADMIN | GET | Invalidates administrator session |

---

## 8. Academic Syllabus Mapping (Modules I &ndash; V)

This table maps every topic directly to source files for faculty presentation and viva examination:

| Syllabus Module | Concepts Covered | Exact Implementation File(s) | Key Details for Viva Explanation |
|---|---|---|---|
| **Module I: Classes, Objects, Inheritance** | `abstract`, `class`, `this`, `super()`, `final`, `static`, Access Specifiers | [Person.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/model/Person.java)<br>[Customer.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/model/Customer.java)<br>[Admin.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/model/Admin.java) | - `Person` is an `abstract` class with `private` fields and `protected role`.<br>- Subclasses `Customer` and `Admin` call `super(...)` in constructors.<br>- `this` resolves parameter shadowing.<br>- `final boolean isAdmin()` and `final String getMaskedEmail()` cannot be overridden.<br>- `public static final String ROLE_USER = "USER"` interface/class constants. |
| **Module II: Polymorphism, Interfaces, Nested Classes** | Runtime Polymorphism, Method Overloading, Inner Class, Static Nested Class, Interface Inheritance | [Account.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/model/Account.java)<br>[SavingsAccount.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/model/SavingsAccount.java)<br>[CurrentAccount.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/model/CurrentAccount.java)<br>[BaseOperations.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/service/BaseOperations.java)<br>[BankOperations.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/service/BankOperations.java) | - **Runtime Polymorphism**: `Account.getMinimumBalance()` is overridden in `SavingsAccount` (Rs. 1,000) and `CurrentAccount` (Rs. 5,000). Dynamic dispatch decides rule at runtime via `canWithdraw()`.<br>- **Method Overloading**: `Account.deposit(double)` and `Account.deposit(double, String)`.<br>- **Inner Class**: `Account.Receipt` accesses enclosing instance via `Account.this.getAccountNumber()`.<br>- **Static Nested Class**: `Account.AccountSummary` instantiated independently (`new Account.AccountSummary(...)`).<br>- **Interface Inheritance**: `BankOperations extends BaseOperations`.<br>- **Nested Interface**: `BankOperations.AuditCallback`. |
| **Module III: Exceptions & Multithreading** | Custom Checked Exceptions, `try`/`catch`, Multi-Catch, Nested Try, `throw`, `throws`, `finally`, `synchronized`, `Runnable` | [com/securebank/exception/*](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/exception)<br>[BankService.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/service/BankService.java)<br>[UserDAO.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/dao/UserDAO.java)<br>[TokenCleanupTask.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/service/TokenCleanupTask.java) | - **Custom Checked Exceptions**: `InsufficientBalanceException`, `InvalidAmountException`, `AccountNotFoundException`, `InactiveAccountException`, `DuplicateAccountException`, `InvalidCredentialsException`.<br>- **Nested Try**: `UserDAO.findByEmail` uses nested try for row mapping.<br>- **Multicatch**: `catch (DuplicateAccountException | InvalidAmountException e)` in servlets.<br>- **Resource Cleanup**: `finally` blocks explicitly close DB connections and streams.<br>- **Concurrency Protection**: Service methods `deposit`, `withdraw`, `transfer` are `synchronized` to eliminate race conditions.<br>- **Multithreading**: `TokenCleanupTask implements Runnable` runs in background daemon thread to purge expired tokens. |
| **Module IV: Collections, Lambdas, File I/O** | `ArrayList`, `HashMap`, `Stack`, Lambda Expressions, Stream API (`filter`, `mapToDouble`, `sum`), Character Streams (`FileWriter`, `PrintWriter`, `BufferedWriter`) | [AdminService.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/service/AdminService.java)<br>[ValidationUtil.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/util/ValidationUtil.java)<br>[SessionUtil.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/util/SessionUtil.java)<br>[FileAuditLogger.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/util/FileAuditLogger.java)<br>[UserStatementServlet.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/servlet/UserStatementServlet.java) | - **Collections**: `ArrayList<Customer>` for listings; `HashMap<String, String>` for validation error map; `Stack<String>` for user navigation tracking in session.<br>- **Lambdas & Streams**: `AdminService.searchCustomers` uses `.stream().filter(...).collect(Collectors.toList())`; `calculateTotalDeposits` uses `.mapToDouble(...).sum()`.<br>- **File I/O**: `FileAuditLogger` appends audit lines to `bank_logs/bank_audit.log` via `FileWriter` & `BufferedWriter`.<br>- **Statement Download**: `UserStatementServlet` writes formatted ASCII bank statement using character stream (`PrintWriter`) as a download. |
| **Module V: JDBC, Servlets, JSP** | `DriverManager`, `PreparedStatement`, `ResultSet`, ACID Transactions (Commit/Rollback), Servlet Lifecycle, `HttpSession`, JSP Expressions/Scriptlets | [DBConnection.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/util/DBConnection.java)<br>[BankService.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/service/BankService.java)<br>[HomeServlet.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/servlet/HomeServlet.java)<br>[SessionUtil.java](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/java/com/securebank/util/SessionUtil.java)<br>[src/main/webapp/jsp/*](file:///c:/Users/Tharanath%20S%20Jadhav/Downloads/Java%20Microproject/src/main/webapp/jsp) | - **JDBC**: Centralized `DriverManager` connections; `PreparedStatement` on all SQL queries; `ResultSet` mapping; resource closure.<br>- **ACID Transactions**: `BankService.transfer()` executes `conn.setAutoCommit(false)`, debits sender, credits receiver, inserts 2 ledger rows, calls `conn.commit()`, or `conn.rollback()` on failure.<br>- **Servlet Lifecycle**: `init()` starts background thread, `doGet()`/`doPost()` handle requests, `destroy()` terminates daemon.<br>- **Security**: `SessionUtil.requireRole(request, response, "ADMIN")` checks authorization on server.<br>- **JSP**: Data presentation with `<%= %>` and `<% %>` scriptlets; shared header and footer via `<%@ include file="..." %>`. |

---

## 9. Security Features

- **Salted SHA-256 Passwords**: Passwords and reset tokens are hashed with a unique salt string via `java.security.MessageDigest`. Plaintext passwords are never persisted.
- **Server-Side Role Guard**: Protected endpoints call `SessionUtil.requireRole(request, response, "ROLE")` at the beginning of `doGet`/`doPost`.
- **Account State Verification**: Deactivated accounts cannot sign in to conduct transactions and cannot receive transfers.
- **Atomic Concurrency Protection**: Service methods are declared `synchronized`, and transfers execute within manual JDBC transactions (`setAutoCommit(false)` with rollback).

---

## 10. Audit Logging and File Output

All critical operations automatically write to:
```
bank_logs/bank_audit.log
```
Example log entries:
```
[2026-10-13 20:17:31] [ACTION: TEST_RUN       ] [TARGET: SYSTEM         ] Automated system test validation entry.
[2026-10-13 20:25:10] [ACTION: REGISTRATION   ] [TARGET: user@demo.com  ] New SAVINGS account created: 100918273645 with opening balance Rs. 2000.00
[2026-10-13 20:27:04] [ACTION: TRANSFER       ] [TARGET: SYSTEM         ] Transfer of Rs. 500.00 from 100918273645 to 100827364519 succeeded [Txn: TXN17603680241029]
```

Statements are downloaded on-demand in plain text format (`statement_<accountNumber>.txt`).

