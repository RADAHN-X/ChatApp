# ChatApp Chatroom

A real-time one to one chat web application built using Java 17, MySQL, Spring Boot, Spring Security, WebSocket, and Thymeleaf. This application allows users to chat with other users is a seperate environment, featuring a modern tech stack with a responsive user interface.

## Features

- **Instant Messaging**: Seamless, real-time communication enabled by WebSocket technology for instantaneous message delivery.
- **Secure Login and Registration**: User authentication and access management powered by Spring Security for a robust and secure experience.
- **User Notifications**: Instant alerts for new messages and user activity, such as logins, ensuring you stay updated in real-time.
- **Chat History Persistence**: Effortless storage and retrieval of chat data from a MySQL database, providing access to previous conversations anytime.
- **Responsive Interface**: Optimized for all devices using Bootstrap, delivering a consistent and user-friendly design across platforms.
- **Integrated Social Media Links**: Quick access to social profiles directly from the chatroom header for easy networking.

## Tech Stack

- **Backend**: Java 17, Spring Boot, Spring Security, MySQL Database, Lombok
- **Frontend**: Thymeleaf, Bootstrap, Font Awesome
- **Real-Time Communication**: Spring WebSocket, STOMP protocol
- **Build Tool**: Maven

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Steps to Run Locally

1. **Clone the Repository**:
   ```sh
   git clone https://github.com/RADAHN-X/ChatApp.git
   cd ChatApp
   ```
   
2. Create MySQL database using [SQLScript](src/main/resources/static/SQLScript.txt)

3. Update MySQL password in [application.properties](src/main/resources/application.properties)


