package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ticket.booking.App.capitalize;

public class UserBookingService {

    // Path to JSON file that stores user data
    private static final String USER_FILE_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<User> userList = new ArrayList<>();
    private User currentUser;
    private Optional<User> loggedInUser = Optional.empty();

    // Constructor with current user (used during login)
    public UserBookingService(User user) throws IOException {
        this.currentUser = user;
        initUserFileIfMissing();  // Create file if not exists
        loadUsersFromFile();      // Load user data into memory
    }

    public UserBookingService() throws IOException {
        initUserFileIfMissing();
        loadUsersFromFile();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // Update the user entry in the in-memory list after any change
    private void updateUserInList(User updatedUser) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getName().equals(updatedUser.getName())) {
                userList.set(i, updatedUser);
                break;
            }
        }
    }

    // Initialize user JSON file if missing
    private void initUserFileIfMissing() throws IOException {
        File file = new File(USER_FILE_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            objectMapper.writeValue(file, new ArrayList<User>());
        }
    }

    // Load users from the JSON file into memory
    private void loadUsersFromFile() throws IOException {
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<>() {});
    }

    // Save the in-memory user list back to file
    private void saveUsersToFile() throws IOException {
        objectMapper.writeValue(new File(USER_FILE_PATH), userList);
    }

    // Handles user registration
    public boolean signUp(User user) {
        for (User u : userList) {
            if (u.getName().equals(user.getName())) {
                System.out.println("Username already taken.");
                return false;
            }
        }

        try {
            userList.add(user);
            saveUsersToFile();
            return true;
        } catch (IOException e) {
            System.out.println("Signup failed: " + e.getMessage());
            return false;
        }
    }

    // Handles user login by verifying credentials
    public boolean loginUser() {
        loggedInUser = userList.stream()
                .filter(u -> u.getName().equals(currentUser.getName()) &&
                        UserServiceUtil.checkPassword(currentUser.getPassword(), u.getHashedPassword()))
                .findFirst();
        return loggedInUser.isPresent();
    }

    // Display all bookings of the currently logged-in user
    public void fetchBookings() {
        if (loggedInUser.isEmpty()) {
            System.out.println("Please login to see bookings.");
            return;
        }

        List<Ticket> bookings = loggedInUser.get().getTicketsBooked();

        if (bookings.isEmpty()) {
            System.out.println("You have no bookings.");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Your Bookings:");
        System.out.println("=".repeat(60));

        for (Ticket ticket : bookings) {
            System.out.println("+----------------------+-----------------------------------------------------+");
            System.out.printf("| %-20s | %-50s |\n", "Ticket ID", ticket.getTicketId());
            System.out.printf("| %-20s | %-50s |\n", "User ID", ticket.getUserId());
            System.out.printf("| %-20s | %-50s |\n", "Source Station", capitalize(ticket.getSource()));
            System.out.printf("| %-20s | %-50s |\n", "Destination Station", capitalize(ticket.getDestination()));
            System.out.printf("| %-20s | %-50s |\n", "Travel Date", ticket.getDateOfTravel());
            System.out.println("+----------------------+-----------------------------------------------------+");
        }
    }

    // Returns list of trains between source and destination
    public List<Train> getTrains(String source, String destination) {
        try {
            return new TrainService().searchTrains(source, destination);
        } catch (IOException e) {
            System.out.println("Failed to fetch trains: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Return the seat layout of a train
    public List<List<Integer>> fetchSeats(Train train) {
        return train.getSeats();
    }

    // Book a seat in the selected train
    public boolean bookTrainSeat(Train train, int row, int seat) {
        try {
            List<List<Integer>> seats = train.getSeats();
            if (!isSeatAvailable(seats, row, seat)) {
                System.out.println("Seat is already booked or invalid.");
                return false;
            }

            // Mark the seat as booked
            seats.get(row).set(seat, 1);
            train.setSeats(seats);

            new TrainService().addTrain(train); // Save updated seat map

            // Generate ticket details
            String ticketId = UUID.randomUUID().toString();
            String userId = loggedInUser.get().getUserId();
            String source = getFirstStation(train);
            String destination = getLastStation(train);
            String date = java.time.LocalDate.now().toString();
            String time = train.getStationTimes().get(destination);

            String ticketInfo = String.format("Train: %s | From: %s To: %s | Seat: Row %d, Column %d | Time: %s",
                    train.getTrainId(), source, destination, row, seat, time);

            // Create and store the ticket
            Ticket ticket = new Ticket(ticketId, userId, source, destination, date, train, ticketInfo, row, seat);
            loggedInUser.get().getTicketsBooked().add(ticket);

            updateUserInList(loggedInUser.get());
            saveUsersToFile();

            return true;
        } catch (IOException e) {
            System.out.println("Booking failed: " + e.getMessage());
            return false;
        }
    }

    // Cancel a selected ticket from the user’s bookings
    public void cancelBooking() {
        if (loggedInUser.isEmpty()) {
            System.out.println("No user is logged in.");
            return;
        }

        User user = loggedInUser.get();
        List<Ticket> bookings = user.getTicketsBooked();

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("You have no bookings to cancel.");
            return;
        }

        // Show all bookings
        System.out.println("\n----------------------------------------");
        System.out.println("Your Bookings:");
        for (int i = 0; i < bookings.size(); i++) {
            Ticket t = bookings.get(i);
            System.out.println("----------------------------------------");
            System.out.println("[" + (i + 1) + "] Ticket");
            System.out.println("  Ticket ID   : " + t.getTicketId());
            System.out.println("  User ID     : " + t.getUserId());
            System.out.println("  From        : " + capitalize(t.getSource()));
            System.out.println("  To          : " + capitalize(t.getDestination()));
            System.out.println("  Travel Date : " + t.getDateOfTravel());
        }
        System.out.println("----------------------------------------");

        // Prompt for cancellation choice
        System.out.print("\nEnter the number of the booking you want to cancel: ");
        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();

        if (choice < 1 || choice > bookings.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Ticket ticketToCancel = bookings.get(choice - 1);

        // Free the seat in train
        Train train = ticketToCancel.getTrain();
        List<List<Integer>> seats = train.getSeats();
        int row = ticketToCancel.getSeatRow();
        int col = ticketToCancel.getSeatCol();
        seats.get(row).set(col, 0);

        train.setSeats(seats);
        try {
            new TrainService().addTrain(train);
        } catch (IOException e) {
            System.out.println("Error updating train seat info: " + e.getMessage());
        }

        // Remove ticket from user list
        bookings.remove(choice - 1);
        updateUserInList(user);

        try {
            saveUsersToFile();
            System.out.println("Booking cancelled successfully.");
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    // Check if a seat is valid and available
    private boolean isSeatAvailable(List<List<Integer>> seats, int row, int col) {
        return row >= 0 && row < seats.size() &&
                col >= 0 && col < seats.get(row).size() &&
                seats.get(row).get(col) == 0;
    }

    // Get first station from the route
    private String getFirstStation(Train train) {
        return train.getStationTimes().keySet().stream().findFirst().orElse("Unknown");
    }

    // Get last station from the route
    private String getLastStation(Train train) {
        List<String> stations = new ArrayList<>(train.getStationTimes().keySet());
        return stations.get(stations.size() - 1);
    }

    // Return logged-in user's name for greetings
    public String getLoggedInUserName() {
        return loggedInUser.map(User::getName).orElse("");
    }
}
