# Login System with Status Upload Feature

A complete Android application implementing a login system with SQLite database and status upload functionality.

## Features

### 🔐 Authentication System
- **User Registration**: Create new accounts with username and password
- **User Login**: Secure authentication with database validation
- **Google Sign-In**: One-click authentication using Google accounts with comprehensive error handling
- **Duplicate Username Check**: Prevents registration with existing usernames
- **Enhanced Password Validation**: Requires uppercase, lowercase, numbers, and special characters
- **Password Strength Indicator**: Real-time password strength feedback
- **Input Validation**: Comprehensive form validation for both login and registration

### 📱 Beautiful UI/UX
- **Modern Material Design**: Clean and intuitive interface
- **Responsive Layout**: Works on different screen sizes
- **Card-based Design**: Elegant card layouts for better visual hierarchy
- **Smooth Transitions**: Seamless navigation between screens

### 📝 Status Management
- **Add Status**: Share what's on your mind (max 280 characters)
- **Edit Status**: Update existing status content
- **Delete Status**: Remove status with confirmation dialog
- **Status Display**: View current status with timestamp
- **Quick Access**: Floating action button for quick status updates

### 🗄️ Database Management
- **SQLite Database**: Local data storage for users and statuses
- **User Table**: Stores user credentials securely
- **Status Table**: Manages user statuses with timestamps
- **Foreign Key Relationships**: Proper database relationships

## Screenshots

### Login Screen
- Clean login form with username and password fields
- **Google Sign-In**: Fully functional Google authentication
- "Sign up here" link for new users
- Form validation with real-time feedback

### Register Screen
- User registration with username, password, and confirm password
- **Google Sign-In**: Register using Google account
- Terms and conditions checkbox
- Duplicate username validation
- Password strength validation

### Home Screen
- Welcome message with user's name (properly spaced from status bar)
- Beautiful logout confirmation dialog
- Status management section with pre-filled text
- Beautiful card-based layout
- Floating action button for quick status updates

## Technical Implementation

### Database Schema

#### Users Table
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
);
```

#### Statuses Table
```sql
CREATE TABLE statuses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    status_text TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Key Components

1. **DatabaseHelper.kt**: SQLite database operations
2. **User.kt**: User data model
3. **Status.kt**: Status data model
4. **LoginActivity.kt**: Login functionality with database validation and Google Sign-In
5. **RegisterActivity.kt**: Registration with duplicate checking and Google Sign-In
6. **HomeActivity.kt**: Main screen with status management
7. **GoogleSignInHelper.kt**: Google authentication helper
8. **dialog_status.xml**: Status input dialog layout

### Features Implementation

#### Registration Flow
1. User enters username, password, and confirm password OR clicks Google Sign-In
2. Real-time validation checks OR Google authentication
3. Database check for existing username/email
4. User creation in SQLite database
5. Redirect to login screen

#### Login Flow
1. User enters credentials OR clicks Google Sign-In
2. Database validation OR Google authentication
3. Successful login redirects to home screen
4. Failed login shows error message

#### Status Management
1. **Add Status**: Dialog-based input with character limit and pre-filled text
2. **Edit Status**: Pre-filled dialog with current status text
3. **Delete Status**: Confirmation dialog before deletion
4. **Display Status**: Shows current status with timestamp
5. **Quick Access**: Floating action button with existing status pre-fill

## Project Structure

```
app/src/main/
├── java/com/homeworks/midexam/
│   ├── auth/
│   │   ├── HomeActivity.kt
│   │   ├── login/
│   │   │   └── LoginActivity.kt
│   │   ├── register/
│   │   │   └── RegisterActivity.kt
│   │   └── utils/
│   │       └── AppUtils.kt
│   ├── database/
│   │   └── DatabaseHelper.kt
│   └── models/
│       ├── User.kt
│       └── Status.kt
└── res/
    ├── layout/
    │   ├── activity_login.xml
    │   ├── activity_register.xml
    │   ├── activity_home.xml
    │   └── dialog_status.xml
    ├── values/
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── dimens.xml
    └── drawable/
        └── (various icons and images)
```

## Dependencies

- **AndroidX Core KTX**: Kotlin extensions
- **Material Design**: Modern UI components
- **CardView**: Card-based layouts
- **ConstraintLayout**: Flexible layouts
- **SQLite**: Local database storage
- **Google Play Services Auth**: Google Sign-In functionality

## Installation & Usage

1. **Clone the repository**
2. **Open in Android Studio**
3. **Build and run the application**
4. **Register a new account** (or use Google Sign-In)
5. **Login with your credentials** (or use Google Sign-In)
6. **Add, edit, or delete your status**

## Validation Rules

### Registration
- Username cannot be empty
- Password must be at least 8 characters
- Password must contain at least one uppercase letter
- Password must contain at least one lowercase letter
- Password must contain at least one number
- Password must contain at least one special character
- Confirm password must match password
- Username must be unique
- Real-time password strength indicator

### Login
- Username cannot be empty
- Password must be at least 8 characters
- Password must contain uppercase, lowercase, numbers, and special characters
- Credentials must match database records

### Status
- Status text cannot be empty
- Maximum 280 characters
- Real-time character count

## Security Features

- **Password Validation**: Minimum length requirements
- **Username Uniqueness**: Prevents duplicate accounts
- **Google Authentication**: Secure OAuth 2.0 authentication with network validation
- **Input Sanitization**: Proper input handling
- **Confirmation Dialogs**: Prevents accidental actions
- **Network Security**: Internet connectivity validation before authentication

## Future Enhancements

- **Password Hashing**: Secure password storage
- **Session Management**: Remember login state
- **Profile Management**: User profile editing
- **Status History**: View all previous statuses
- **Image Upload**: Add images to statuses
- **Push Notifications**: Real-time updates
- **Offline Support**: Enhanced offline functionality

## Contributing

Feel free to contribute to this project by:
- Reporting bugs
- Suggesting new features
- Submitting pull requests
- Improving documentation

## License

This project is open source and available under the MIT License.
