# Banking Application with ZeroMQ

## Overview
This is a **Banking Application** built using **Java** that incorporates **ZeroMQ (ZMQ)** for messaging and communication between different banking services. The project follows **Object-Oriented Programming (OOP) principles**, includes **exception handling**, and implements **multithreading** to efficiently manage banking operations.

## Features
- **User Account Management**: Create, update, and delete bank accounts.
- **Deposit & Withdraw**: Secure transactions with proper validations.
- **Balance Inquiry**: Fetch the account balance efficiently.
- **ZeroMQ Integration**: Efficient message passing for distributed communication.
- **Multithreading**: Handles multiple user requests concurrently.
- **Exception Handling**: Ensures robustness against incorrect operations.

## Tech Stack
- **Java** (Core logic & OOP implementation)
- **ZeroMQ** (For inter-service communication)
- **Multithreading** (For handling concurrent transactions)
- **Exception Handling** (To manage errors efficiently)

## Installation
### Prerequisites
Ensure you have the following installed on your system:
- Java (JDK 11 or later)
- ZeroMQ (JZMQ or jeromq library for Java)

### Steps to Run
1. **Clone the repository**:
   ```sh
   git clone https://github.com/yourusername/banking-zmq.git
   cd banking-zmq
   ```
2. **Install dependencies** (if using Maven or Gradle):
   - For Maven:
     ```xml
     <dependency>
       <groupId>org.zeromq</groupId>
       <artifactId>jeromq</artifactId>
       <version>0.5.2</version>
     </dependency>
     ```
   - For Gradle:
     ```groovy
     dependencies {
         implementation 'org.zeromq:jeromq:0.5.2'
     }
     ```
   ```

## How It Works
- The **server** handles all banking transactions and communicates via **ZeroMQ sockets**.
- The **clients** send requests (e.g., deposit, withdraw, balance check) using **ZMQ messaging**.
- The server processes requests in a **thread-safe** manner and responds to clients asynchronously.


## Future Enhancements
- Implement database integration for persistent account storage.
- Add security features like **authentication & encryption**.
- Extend support for **multiple currencies**.
- Create a **web UI** for better user experience.

## Contributors
- **Nishant Bhandari** - Developer

---
Feel free to contribute and enhance this banking system! 🚀

