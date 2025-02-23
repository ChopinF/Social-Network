# Social-Network
## Overview

This is a Java-based Social Network application that allows users to manage their profiles, connect with friends, and send messages. The application implements user authentication, friendship management, and messaging features, following the MVC architecture.
## Features

- **User authentication** (Login, Registration)
- **Friendship management** (Add, Remove, View friends)
- **Messaging** (Send and reply to messages)
- **Event handling** using observer pattern for updates on user activities
- **Data validation** for user and friendship actions
- **Database** to persist users, messages, and friendships

## Technologies Used

- **Backend:** Java
- **Frontend:** FXML
- **Database:** PostgreSQL

## Project Structure
- **controller**/: Handles the logic for user login, registration, friendship actions, and message operations.
- **domain**/: Contains core business models like User, Friendship, and Message along with validation logic.
- **repo**/: Implements the repository pattern to handle database interactions for users, messages, and friendships.
- **service**/: Contains business logic for managing social network operations like adding friendships and sending messages.
- **ui**/: Provides the console-based user interface for interaction with the application.
- **utils**/: Utility classes for event handling (Observer pattern), message management, and paging.

## Installation & Setup

### Prerequisites

- Java 8+ (for compiling and running the project)
- PostgreSQL
### Steps

1. **Clone the repository**
```
git clone (https://github.com/ChopinF/Social-Network.git)
cd Social-Network
```

2. **Compile and run the application**
```
javac -d bin -sourcepath src src/Main.java
java -cp bin Main
```

3. **Open the application in the console for testing**
```
java -cp bin Main
```

5. Open in browser

```
http://localhost:5000
```
