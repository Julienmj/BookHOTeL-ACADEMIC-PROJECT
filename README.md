# Hotel Booking System

A Java-based Hotel Booking System that allows users to manage hotel reservations, room inventory, and guest information.!

## Features

- Room management (view available rooms, room types, pricing)
- Booking management (make, modify, cancel reservations)
- Guest information management
- Simple command-line interface

## Prerequisites

- Java Development Kit (JDK) 8 or later
- Apache NetBeans IDE (recommended) or any Java IDE of your choice

## Getting Started

1. Clone or download this repository
2. Open the project in NetBeans IDE
3. Build the project (F11)
4. Run the application (F6)

## Project Structure

```
HotelBookingSystem/
├── src/
│   └── HotelBookingApp.java    # Main application class
├── build/                      # Compiled class files
├── dist/                       # Distributable files
└── nbproject/                  # NetBeans project configuration
```

## Usage

1. Launch the application
2. Follow the on-screen menu to:
   - View available rooms
   - Make a reservation
   - View existing bookings
   - Manage guest information
   - Exit the application

## Building the Project

To build the project from the command line:

```bash
ant -f "build.xml" jar
```

## Running the Application

After building, you can run the application using:

```bash
java -jar "dist/HotelBookingSystem.jar"
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

