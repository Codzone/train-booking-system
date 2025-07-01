
# 🚆 Train Booking System (Console-Based Java App)

A simple Java console application for booking train tickets. This project allows users to sign up, log in, search available trains, book seats, and manage bookings — all via a clean terminal interface.

---

## ✨ Features

- ✅ Sign up & login with basic authentication  
- 🔍 Search trains between stations  
- 📊 View train route and departure time in table format  
- 🪑 Book or cancel seats  
- 💾 Data stored locally using JSON (no database needed)

---

## 🛠 Technologies Used

- Java 21  
- Jackson (for JSON parsing)  
- File I/O (JSON-based data storage)  
- Simple console-based UI (no external libraries required)

---

## 📂 Folder Structure

```
ticket-booking-system/
└── app/
    └── src/
        └── main/
            └── java/
                └── ticket/
                    └── booking/
                        ├── App.java
                        ├── entities/
                        ├── service/
                        ├── util/
                        └── localDb/
                            ├── users.json
                            └── trains.json

```

---

## ▶️ How to Run

1. **Clone this repo**:
   ```bash
   git clone https://github.com/your-username/train-booking-system.git
   ```

2. **Open in your IDE** (like IntelliJ IDEA or Eclipse)

3. **Run** the `App.java` file

4. Follow the menu options in the console

---

## 📌 Sample Features in Action

```
========================================
         TRAIN BOOKING SYSTEM
========================================
Please choose an option:
 [1] Sign Up
 [2] Login
 [3] Fetch My Bookings
 [4] Search Available Trains
 [5] Book a Seat
 [6] Cancel a Booking
 [7] Exit the App
----------------------------------------
```

### Train Search Output (Example)
```
=== Available Trains from JAIPUR to DELHI ===
+------------+----------------+--------------+
| Train No.  | Station        | Departure    |
+------------+----------------+--------------+
| 0001       | Bangalore      | 13:50:00     |
| 0001       | Jaipur         | 13:50:00     |
| 0001       | Delhi          | 13:50:00     |
+------------+----------------+--------------+
| 0002       | Bangalore      | 13:50:00     |
| 0002       | Jaipur         | 13:50:00     |
| 0002       | Delhi          | 13:50:00     |
+------------+----------------+--------------+
```

---

## 🙋 Why I Built This

This is a personal learning project to improve my Java skills in:

- File I/O handling  
- JSON parsing with Jackson  
- Object-Oriented Programming  
- Building a modular and maintainable codebase  
- Creating a user-friendly command-line UI

---

## 📬 Contact

Feel free to connect or leave feedback!  
Email:  [Hasnain Ansari](codzone8832@gmail.com)
