# Xeno CRM Platform

## Project Overview

The Xeno CRM Platform is a web-based Customer Relationship Management (CRM) application built using Spring Boot for the backend and plain HTML, CSS, and JavaScript for the frontend. It provides core functionalities for managing customer data, tracking orders, and orchestrating marketing campaigns.

## Key Features

* **Customer Management:**
    * Add new customers with details like name, email, and phone.
    * View existing customers.
* **Order Management:**
    * Add new orders linked to existing customers, including amount and item details.
    * Automatically updates customer spend and visit counts upon order creation.
* **Campaign Management:**
    * Create and launch marketing campaigns.
    * Define audience segmentation rules using a flexible JSON structure (e.g., based on `totalSpend`, `visitCount`, `lastOrderDate`).
    * Preview audience size before launching a campaign.
    * Send personalized messages using a message template.
* **Simple Web UI:** An intuitive, single-page application (SPA) style frontend for easy interaction.

## Technologies Used

### Backend (Spring Boot)

* **Spring Boot:** Framework for building robust, stand-alone, production-grade Spring applications.
* **Spring Data JPA:** For database interaction and object-relational mapping (ORM).
* **MySQL:** Relational database for storing application data (customers, orders, campaigns).
* **Maven:** Build automation tool.
* **Lombok:** Reduces boilerplate code (getters, setters, constructors, etc.).

### Frontend (Web)

* **HTML5:** Structure of the web application.
* **CSS3:** Styling and layout.
* **JavaScript (ES6+):** Client-side logic for interacting with the backend APIs and dynamic UI updates.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* Java Development Kit (JDK) 17 or higher
* Maven 3.6.x or higher
* MySQL Server (version 8.0+)
* A web browser (Chrome, Firefox, etc.)

### Installation and Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/crm.git](https://github.com/YOUR_USERNAME/crm.git)
    cd crm
    ```
    (Replace `YOUR_USERNAME` with your actual GitHub username).

2.  **Database Setup:**
    * Create a MySQL database. For example: `CREATE DATABASE crm_db;`
    * Update your `src/main/resources/application.properties` file with your MySQL database credentials:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/crm_db?useSSL=false&serverTimezone=UTC
        spring.datasource.username=root
        spring.datasource.password=your_mysql_password
        spring.jpa.hibernate.ddl-auto=update # For initial setup, consider 'none' or 'validate' for production
        ```
        *Replace `your_mysql_password` with your actual MySQL root password.*

3.  **Build the Spring Boot application:**
    ```bash
    mvn clean install
    ```

4.  **Run the Spring Boot application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8081`.

### Accessing the Application

Open your web browser and navigate to:

`http://localhost:8081/`

You should see the Xeno CRM Platform dashboard.

## Usage

* **Dashboard:** Provides an overview.
* **Customers & Orders:** Add new customers and orders, view existing data.
* **Campaigns:** Create new marketing campaigns with audience segmentation and message templates.

## API Endpoints (for reference)

The backend provides the following RESTful API endpoints:

* `POST /api/customers` - Create a new customer
* `GET /api/customers` - Get all customers
* `POST /api/orders` - Create a new order
* `GET /api/orders` - Get all orders
* `POST /api/campaigns` - Create and launch a new campaign
* `GET /api/campaigns` - Get all campaigns
* `POST /api/campaigns/preview-audience` - Preview audience size for segmentation rules

## Contributing

(If you plan to allow others to contribute, you would add guidelines here)

## License

(If you chose a license, specify it here. E.g., "This project is licensed under the MIT License - see the LICENSE.md file for details.")
