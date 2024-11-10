import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection; // Connection-> Interface
import java.sql.Statement;  // Statement -> Interface
import java.sql.ResultSet;  // ResultSet -> Interface
import java.util.Scanner;

public class HotelReservationSystem {
    // Database Variable
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "prateek@8810";

    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        try{
            // Loading database driver to connect database it is mandatory
            Class.forName("com.mysql.cj.jdbc.Driver");  // (com.sql.cj) -> package, (jdbc.Driver) -> driver, forName() method is used to load drivers
        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        // Connection established
        try{
            Connection connection = DriverManager.getConnection(url, username, password); // getConnection() takes 3 parameter which is used to established connection with database and getConnection() is inside of DriverManager class.
            while(true){
                System.out.println();
                System.out.println("Hotel Management System");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Deleter Reservation");
                System.out.println("0. EXIT");
                System.out.print("Choose a option: ");
                int choice = scanner.nextInt();
                switch(choice){
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 6:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter guest name: ");
            String guestName = scanner.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = scanner.nextLine();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number)"+"VALUES('"+guestName+"',"+roomNumber+",'"+contactNumber+"')";
            try(Statement statement = connection.createStatement()){ // Statement -> It is used to run SQL query in java
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0){
                    System.out.println("Reservation Successful!");
                }
                else{
                    System.out.println("Reservation failed!");
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection) throws SQLException{
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservation";

        try(Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)){ // ResultSet -> It is an interface where Database se jo bhi data aata hai woo hai iske instance(resultset) main store ho jaata hai
            System.out.println("Current Reservation: ");
            System.out.println("+-------------------+------------------+--------------------+------------------------+---------------------+");
            System.out.println("| Reservation ID    | Guest            | Room Number        | Contact Number         | Reservation Date  |");
            System.out.println("+-------------------+------------------+--------------------+------------------------+---------------------+");

            while(resultSet.next()){  // next() method main jab tak database wali value hogi tab tak woo true ki tarah kaam karega
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                int contactNumber = resultSet.getInt("Contact_number");
                String reservationDate = resultSet.getString("reservation_date").toString();

                // Format to display the reservation data in table
                System.out.printf("|  %-14d  |  %-15s  |  %-13d  |  %-20s  |  %-19s  |\n", reservationId,guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("+-------------------+------------------+--------------------+------------------------+---------------------+");
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.println("Enter guest name: ");
            String guestName = scanner.nextLine();

            String sql = "SELECT room_number FROM reservations "+
                         "WHERE reservation_Id = "+reservationId+
                         " AND guest_name = "+guestName+"'";
            try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
                if (resultSet.next()){
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room number for Reservation ID "+reservationId+" and Guest "+guestName+" is: "+roomNumber);
                }
                else{
                System.out.println("Reservation not found for the given data ID and guest name.");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            System.out.println("Enter the guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "Update reservation SET guest_name = '"+newGuestName+"', "+
                          "room_number = " +newRoomNumber+", "+
                          "contact_number = '"+newContactNumber +"' "+
                          "WHERE reservation_id = "+reservationId;
            try(Statement statement = connection.createStatement()){
            int affectedRows = statement.executeUpdate(sql);

            if (affectedRows>0){
                System.out.println("Reservation updated successfully!");
            }
            else{
                System.out.println("Reservation update failed");
            }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = "+reservationId;
            try(Statement statement = connection.createStatement()){
            int affectedRows = statement.executeUpdate(sql);

            if (affectedRows>0){
                System.out.println("Reservation deleted successfully!");
            }
            else{
                System.out.println("Reservation deletion failed");
            }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId){
        try{
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id ="+reservationId;

            try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
                return resultSet.next();    // If there is a result, the reservation exists
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;  // Handled database error as needed
        }
    }

    private static void exit() throws InterruptedException{
        System.out.println("Existing system");
        int i = 5;
        while(i!=0){
            System.out.println(".");
            Thread.sleep(400);
            i--;
        }
        System.out.println("\n Thank you for using Hotel Reservation System!!!");
    }
}
