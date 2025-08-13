import java.io.*;
import java.util.*;

public class HotelBookingSystem {
    // Room class
    static class Room {
        int roomNumber;
        String category;
        boolean isAvailable;

        Room(int roomNumber, String category) {
            this.roomNumber = roomNumber;
            this.category = category;
            this.isAvailable = true;
        }

        public String toString() {
            return "Room " + roomNumber + " (" + category + ") - " + (isAvailable ? "Available" : "Booked");
        }
    }

    // Booking class
    static class Booking {
        String customerName;
        int roomNumber;
        String category;

        Booking(String customerName, int roomNumber, String category) {
            this.customerName = customerName;
            this.roomNumber = roomNumber;
            this.category = category;
        }

        public String toString() {
            return "Booking: " + customerName + " - Room " + roomNumber + " (" + category + ")";
        }
    }

    // Hotel class
    static class Hotel {
        List<Room> rooms = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        final String FILE_NAME = "bookings.txt";

        Hotel() {
            rooms.add(new Room(101, "Standard"));
            rooms.add(new Room(102, "Deluxe"));
            rooms.add(new Room(103, "Suite"));
            loadBookingsFromFile();
        }

        void showAvailableRooms() {
            for (Room room : rooms) {
                if (room.isAvailable) {
                    System.out.println(room);
                }
            }
        }

        boolean bookRoom(String customerName, String category) {
            for (Room room : rooms) {
                if (room.isAvailable && room.category.equalsIgnoreCase(category)) {
                    room.isAvailable = false;
                    Booking booking = new Booking(customerName, room.roomNumber, category);
                    bookings.add(booking);
                    saveBookingToFile(booking);
                    System.out.println("💳 Payment successful. Room booked!");
                    return true;
                }
            }
            System.out.println("❌ No available rooms in category: " + category);
            return false;
        }

        boolean cancelBooking(String customerName) {
            Iterator<Booking> iterator = bookings.iterator();
            while (iterator.hasNext()) {
                Booking booking = iterator.next();
                if (booking.customerName.equalsIgnoreCase(customerName)) {
                    iterator.remove();
                    for (Room room : rooms) {
                        if (room.roomNumber == booking.roomNumber) {
                            room.isAvailable = true;
                            break;
                        }
                    }
                    removeBookingFromFile(customerName);
                    System.out.println("✅ Booking cancelled.");
                    return true;
                }
            }
            System.out.println("❌ No booking found for: " + customerName);
            return false;
        }

        void viewBookings() {
            if (bookings.isEmpty()) {
                System.out.println("📭 No bookings yet.");
            } else {
                for (Booking booking : bookings) {
                    System.out.println(booking);
                }
            }
        }

        void saveBookingToFile(Booking booking) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                writer.write(booking.customerName + "," + booking.roomNumber + "," + booking.category);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("⚠️ Error saving booking.");
            }
        }

        void removeBookingFromFile(String customerName) {
            File inputFile = new File(FILE_NAME);
            File tempFile = new File("temp.txt");

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith(customerName + ",")) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                System.out.println("⚠️ Error removing booking.");
            }

            inputFile.delete();
            tempFile.renameTo(inputFile);
        }

        void loadBookingsFromFile() {
            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String name = parts[0];
                        int roomNum = Integer.parseInt(parts[1]);
                        String category = parts[2];
                        bookings.add(new Booking(name, roomNum, category));
                        for (Room room : rooms) {
                            if (room.roomNumber == roomNum) {
                                room.isAvailable = false;
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("⚠️ Error loading bookings.");
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        Hotel hotel = new Hotel();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n🏨 Hotel Booking System");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View Bookings");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    hotel.showAvailableRooms();
                    break;
                case 2:
                    System.out.print("Enter your name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter room category (Standard/Deluxe/Suite): ");
                    String category = scanner.nextLine();
                    hotel.bookRoom(name, category);
                    break;
                case 3:
                    System.out.print("Enter your name to cancel: ");
                    String cancelName = scanner.nextLine();
                    hotel.cancelBooking(cancelName);
                    break;
                case 4:
                    hotel.viewBookings();
                    break;
                case 5:
                    System.out.println("👋 Goodbye!");
                    return;
                default:
                    System.out.println("❗ Invalid option.");
            }
        }
    }
}