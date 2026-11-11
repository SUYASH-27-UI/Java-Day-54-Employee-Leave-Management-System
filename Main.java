
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class LeaveRequest {
    int employeeId;
    String employeeName;
    String leaveType;
    int days;
    String status;

    LeaveRequest(int employeeId, String employeeName,
                 String leaveType, int days, String status) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.leaveType = leaveType;
        this.days = days;
        this.status = status;
    }

    void display() {
        System.out.println("\nEmployee ID: " + employeeId);
        System.out.println("Name: " + employeeName);
        System.out.println("Leave Type: " + leaveType);
        System.out.println("Days: " + days);
        System.out.println("Status: " + status);
    }

    String toFileString() {
        return employeeId + "|" + employeeName + "|"
                + leaveType + "|" + days + "|" + status;
    }
}

public class Main {

    static Scanner sc = new Scanner(System.in);
    static ArrayList<LeaveRequest> requests = new ArrayList<>();

    static final String FILE_NAME = "leaves.txt";

    static void loadRequests() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.split("\\|", -1);

                if (data.length == 5) {
                    try {
                        requests.add(new LeaveRequest(
                            Integer.parseInt(data[0]),
                            data[1],
                            data[2],
                            Integer.parseInt(data[3]),
                            data[4]
                        ));
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid record.");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading records.");
        }
    }

    static void saveRequests() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            for (LeaveRequest request : requests) {
                writer.write(request.toFileString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving records.");
        }
    }

    static void applyLeave() {
        System.out.print("\nEnter Employee ID: ");
        if (!sc.hasNextInt()) {
            System.out.println("Invalid employee ID.");
            sc.nextLine();
            return;
        }

        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Employee Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter Leave Type: ");
        String type = sc.nextLine().trim();

        System.out.print("Enter Number of Days: ");

        if (!sc.hasNextInt()) {
            System.out.println("Invalid number of days.");
            sc.nextLine();
            return;
        }

        int days = sc.nextInt();
        sc.nextLine();

        if (id <= 0 || name.isEmpty() || type.isEmpty()
                || name.contains("|") || type.contains("|")
                || days <= 0) {
            System.out.println("Invalid leave details.");
            return;
        }

        requests.add(new LeaveRequest(
            id, name, type, days, "Pending"
        ));

        saveRequests();

        System.out.println("Leave request submitted successfully.");
    }

    static void viewRequests() {
        if (requests.isEmpty()) {
            System.out.println("\nNo leave requests available.");
            return;
        }

        System.out.println("\n===== ALL LEAVE REQUESTS =====");

        for (LeaveRequest request : requests) {
            request.display();
        }
    }

    static void searchRequest() {
        System.out.print("\nEnter Employee ID: ");

        if (!sc.hasNextInt()) {
            System.out.println("Invalid employee ID.");
            sc.nextLine();
            return;
        }

        int id = sc.nextInt();
        sc.nextLine();

        boolean found = false;

        for (LeaveRequest request : requests) {
            if (request.employeeId == id) {
                request.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No request found for this employee.");
        }
    }

    static void updateStatus() {
        System.out.print("\nEnter Employee ID: ");

        if (!sc.hasNextInt()) {
            System.out.println("Invalid employee ID.");
            sc.nextLine();
            return;
        }

        int id = sc.nextInt();
        sc.nextLine();

        boolean found = false;

        for (LeaveRequest request : requests) {
            if (request.employeeId == id
                    && request.status.equals("Pending")) {

                System.out.println("Employee: " + request.employeeName);
                System.out.println("1. Approve");
                System.out.println("2. Reject");

                System.out.print("Enter choice: ");

                if (!sc.hasNextInt()) {
                    System.out.println("Invalid choice.");
                    sc.nextLine();
                    return;
                }

                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    request.status = "Approved";
                } else if (choice == 2) {
                    request.status = "Rejected";
                } else {
                    System.out.println("Invalid choice.");
                    return;
                }

                saveRequests();

                System.out.println(
                    "Leave request " + request.status + "."
                );

                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("No pending request found for this ID.");
        }
    }

    public static void main(String[] args) {
        loadRequests();

        while (true) {
            System.out.println("\n===== EMPLOYEE LEAVE MANAGEMENT =====");
            System.out.println("1. Apply for Leave");
            System.out.println("2. View All Requests");
            System.out.println("3. Search Employee Request");
            System.out.println("4. Approve / Reject Leave");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Enter a valid number.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    applyLeave();
                    break;

                case 2:
                    viewRequests();
                    break;

                case 3:
                    searchRequest();
                    break;

                case 4:
                    updateStatus();
                    break;

                case 5:
                    System.out.println("Leave management system closed.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
