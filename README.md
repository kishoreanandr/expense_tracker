# 💰 SmartExpense - Intelligent Personal Finance Tracker

A comprehensive full-stack web application that helps users track expenses, manage budgets, and receive AI-powered financial insights to optimize their spending habits.

## 🚀 Features

- **📊 Expense Management**: Track expenses with categories, payment methods, and merchant details
- **💰 Budget Control**: Set weekly/monthly/yearly budgets with category-specific limits
- **📈 Analytics Dashboard**: Real-time spending insights and trend analysis
- **🤖 AI Insights**: Intelligent spending suggestions and budget optimization recommendations
- **📧 Smart Notifications**: Automated email alerts for budget overruns and expense summaries
- **📄 Report Export**: Generate PDF reports of expenses and budgets
- **🔐 Secure Authentication**: JWT-based authentication with encrypted password storage
- **📱 Responsive Design**: Cross-device compatibility with Bootstrap UI

## 🛠️ Tech Stack

### Frontend
- **React.js** - Modern UI framework
- **Bootstrap** - Responsive design components
- **Vite** - Fast build tool
- **React Router** - Client-side routing

### Backend
- **Spring Boot 3.5.4** - Java-based REST API
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **MySQL** - Relational database
- **JWT** - Stateless authentication
- **JavaMail** - Email notifications
- **iText PDF** - PDF generation

## 📋 Prerequisites

- Java 21 or higher
- Node.js 18 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/smart-expense-tracker.git
cd smart-expense-tracker
```

### 2. Database Setup
```sql
CREATE DATABASE expensetracker;
```

### 3. Backend Setup
```bash
cd expensetracker
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

Run the application:
```bash
mvn spring-boot:run
```

### 4. Frontend Setup
```bash
cd ../frontend
npm install
npm run dev
```

### 5. Access the Application
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080

## 📁 Project Structure

```
smart-expense-tracker/
├── expensetracker/                 # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/expensetracker/
│   │       ├── Controller/         # REST Controllers
│   │       ├── Entity/            # JPA Entities
│   │       ├── Service/           # Business Logic
│   │       ├── Repository/        # Data Access Layer
│   │       ├── Config/            # Security & CORS Config
│   │       └── util/              # JWT Utilities
│   └── src/main/resources/
│       └── application.properties  # Configuration
├── frontend/                      # React Frontend
│   ├── src/
│   │   ├── components/            # React Components
│   │   │   ├── Auth/             # Authentication
│   │   │   ├── Dashboard/        # Main Dashboard
│   │   │   ├── Expenses/         # Expense Management
│   │   │   ├── Budget/           # Budget Management
│   │   │   ├── Analytics/        # Data Visualization
│   │   │   ├── AI/               # AI Suggestions
│   │   │   ├── Settings/         # User Preferences
│   │   │   └── Export/           # PDF Export
│   │   └── api/                  # API Integration
│   └── package.json
└── README.md
```

## 🔧 Configuration

### Email Setup
1. Enable 2-factor authentication on your Gmail account
2. Generate an App Password
3. Update `application.properties` with your email credentials

### JWT Configuration
The JWT secret is configured in `application.properties`. For production, use a strong, unique secret.

## 📊 Key Features Explained

### Expense Tracking
- Add expenses with amount, date, category, merchant, and payment method
- Edit and delete existing expenses
- View expense history with filtering options

### Budget Management
- Set overall and category-specific budgets
- Choose from weekly, monthly, or yearly periods
- Receive alerts when approaching or exceeding budget limits

### AI-Powered Insights
- Analyze spending patterns
- Provide personalized budget recommendations
- Suggest spending optimizations based on historical data

### Automated Notifications
- Daily/weekly/monthly expense summaries
- Budget alert emails when thresholds are exceeded
- Welcome emails for new users

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the powerful frontend library
- Bootstrap for the responsive UI components
- All contributors and users of this project

## 📞 Support

If you have any questions or need help, please open an issue in the GitHub repository.

---

**Made with ❤️ for better financial management** 