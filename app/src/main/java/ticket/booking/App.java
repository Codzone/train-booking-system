package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.service.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;

public class App {

    private static final Scanner sc = new Scanner(System.in);
    private static Train selectedTrain = null;

    // Helper method to check if the service is initialized and user is logged in
    private static boolean isServiceAvailable(UserBookingService service) {
        if (service == null) {
            System.out.println("Please login first.");
            return false;
        }
        return true;
    }

    // Capitalizes the first letter of a string
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // Simple input prompt wrapper
    private static String prompt(String message) {
        System.out.print(message + " ");
        return sc.next();
    }

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");

        UserBookingService userBookingService;
        try {
            userBookingService = new UserBookingService(); // Initialize booking service
        } catch (IOException e) {
            System.out.println("Initialization error: " + e.getMessage());
            return;
        }

        // Infinite loop for menu-driven interface
        while (true) {
            // Display menu
            System.out.println();
            System.out.println("=".repeat(40));
            System.out.println("          TRAIN BOOKING SYSTEM         ");
            System.out.println("=".repeat(40));
            System.out.println("Please choose an option:");
            System.out.println(" [1] Sign Up");
            System.out.println(" [2] Login");
            System.out.println(" [3] Fetch My Bookings");
            System.out.println(" [4] Search Available Trains");
            System.out.println(" [5] Book a Seat");
            System.out.println(" [6] Cancel a Booking");
            System.out.println(" [7] Exit the App");
            System.out.println("-".repeat(40));
            System.out.print("Your choice: ");

            int option = sc.nextInt();

            switch (option) {
                case 1 -> {
                    // Sign up process
                    String name = prompt("Enter username to sign up:");
                    String pass = prompt("Enter password to sign up:");
                    User newUser = new User(UUID.randomUUID().toString(), name, pass, UserServiceUtil.hashPassword(pass), new ArrayList<>());
                    if (userBookingService.signUp(newUser)) {
                        System.out.println("Signup successful. Please login now.");
                    } else {
                        System.out.println("Signup failed. Try again.");
                    }
                }

                case 2 -> {
                    // Login process
                    String name = prompt("Enter username to login:");
                    String pass = prompt("Enter password to login:");
                    User loginUser = new User(UUID.randomUUID().toString(), name, pass, "", new ArrayList<>());
                    userBookingService.setCurrentUser(loginUser);
                    if (userBookingService.loginUser()) {
                        System.out.println("Login successful!");
                    } else {
                        System.out.println("Login failed! Incorrect credentials.");
                    }
                }

                case 3 -> {
                    // Fetch user's booked tickets
                    if (isServiceAvailable(userBookingService)) {
                        System.out.println("Fetching your bookings...");
                        userBookingService.fetchBookings();
                    }
                }

                case 4 -> {
                    // Search trains by source and destination
                    if (isServiceAvailable(userBookingService)) {
                        String source = prompt("Enter source station:");
                        String destination = prompt("Enter destination station:");
                        List<Train> trains = userBookingService.getTrains(source, destination);

                        if (trains.isEmpty()) {
                            System.out.println("No trains found.");
                            break;
                        }

                        // Display train options in table format
                        System.out.println("\n=== Available Trains from " + source.toUpperCase() + " to " + destination.toUpperCase() + " ===");
                        System.out.println("+------------+----------------+--------------+");
                        System.out.printf("| %-10s | %-14s | %-12s |\n", "Train No.", "Station", "Departure");
                        System.out.println("+------------+----------------+--------------+");

                        for (int i = 0; i < trains.size(); i++) {
                            Train t = trains.get(i);
                            int trainNo = i + 1;
                            boolean isFirst = true;

                            for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                                String trainNumDisplay = isFirst ? String.valueOf(trainNo) : "";
                                isFirst = false;

                                System.out.printf("| %-10s | %-14s | %-12s |\n",
                                        trainNumDisplay, capitalize(entry.getKey()), entry.getValue());
                            }

                            System.out.println("+------------+----------------+--------------+");
                        }

                        // Let user select a train to book
                        System.out.println();
                        System.out.print("Select a train by number: ");
                        int choice = sc.nextInt();
                        if (choice >= 1 && choice <= trains.size()) {
                            selectedTrain = trains.get(choice - 1);
                            System.out.println("Train selected.");
                        } else {
                            System.out.println("Invalid train selection.");
                        }
                    }
                }

                case 5 -> {
                    // Book a seat on the selected train
                    if (!isServiceAvailable(userBookingService)) break;
                    if (selectedTrain == null) {
                        System.out.println("Please select a train first (Option 4: Search Trains).");
                        break;
                    }

                    System.out.println("\nAvailable Seats (0 = Empty, 1 = Booked):");
                    List<List<Integer>> seats = userBookingService.fetchSeats(selectedTrain);

                    // Display seat matrix
                    for (int i = 0; i < seats.size(); i++) {
                        System.out.print("Row " + i + ": ");
                        for (int j = 0; j < seats.get(i).size(); j++) {
                            System.out.print(seats.get(i).get(j) + " ");
                        }
                        System.out.println();
                    }

                    // Prompt for seat selection
                    System.out.print("\nEnter row number: ");
                    int row = sc.nextInt();
                    System.out.print("Enter column number: ");
                    int col = sc.nextInt();

                    boolean booked = userBookingService.bookTrainSeat(selectedTrain, row, col);
                    System.out.println(booked ? "Seat booked successfully!" : "Seat booking failed. It might already be booked.");
                    System.out.println("=".repeat(40));
                }

                case 6 -> {
                    // Cancel booking
                    if (!isServiceAvailable(userBookingService)) break;
                    System.out.println("Cancelling your booking...");
                    userBookingService.cancelBooking();
                }

                case 7 -> {
                    // Exit the application
                    System.out.println();
                    System.out.println("Exiting Train Booking System...");
                    System.out.println("Thank you for using the system, "
                            + (userBookingService.getLoggedInUserName().isEmpty() ? "Guest!" : userBookingService.getLoggedInUserName())
                            + "\n");
                    System.out.println("Tip: Don't forget to book in advance next time!");
                    System.out.println("=".repeat(50));
                    return;
                }

                default -> System.out.println("Invalid option. Please choose 1–7.");
            }
        }
    }
}
