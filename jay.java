package Assignment_1;

import javax.swing.JOptionPane;
import java.util.ArrayList;

public class jay {

    // ===================== Arrays (Part 3, requirement 1) =====================
    private static ArrayList<String> sentMessages = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessagesArray = new ArrayList<>();
    private static ArrayList<String> messageHashes = new ArrayList<>();
    private static ArrayList<String> messageIDs = new ArrayList<>();

    public static void main(String[] args) {

        // REGISTRATION
        String firstName = JOptionPane.showInputDialog( null, "Enter First Name:","REGISTRATION",JOptionPane.INFORMATION_MESSAGE);

        String lastName = JOptionPane.showInputDialog(null, "Enter Last Name:","REGISTRATION" ,JOptionPane.INFORMATION_MESSAGE);

        String username = JOptionPane.showInputDialog(null,"Enter Username:","REGISTRATION",JOptionPane.INFORMATION_MESSAGE);

        String password = JOptionPane.showInputDialog( null,"Enter Password:","REGISTRATION",JOptionPane.INFORMATION_MESSAGE);

        String phone = JOptionPane.showInputDialog(null,"Enter Phone Number (+27):","REGISTRATION",JOptionPane.INFORMATION_MESSAGE);

        // Login object
        Login login = new Login();

        // Validation
        boolean validUsername =login.checkUserName(username);
        boolean validPassword = login.checkPasswordComplexity(password);
        boolean validPhone =login.checkCellPhoneNumber(phone);

        // Username
        if (validUsername) {

            JOptionPane.showMessageDialog( null,"Username successfully captured.");

        } else {

            JOptionPane.showMessageDialog( null, "Username incorrectly formatted.");
        }

        // Password
        if (validPassword) {

            JOptionPane.showMessageDialog(null,"Password successfully captured.");

        } else {

            JOptionPane.showMessageDialog(null,"Password incorrectly formatted.");
        }

        // Phone
        if (validPhone) {

            JOptionPane.showMessageDialog(null,"Phone number successfully captured.");

        } else {

            JOptionPane.showMessageDialog(null,"Phone number incorrectly formatted.");
        }

        // Registration success
        if (validUsername && validPassword && validPhone) {

            JOptionPane.showMessageDialog(null,"Registration successful." );

            // LOGIN
            String enteredUsername =JOptionPane.showInputDialog(null,"Enter Username:","LOGIN",JOptionPane.INFORMATION_MESSAGE);

            String enteredPassword =JOptionPane.showInputDialog(null,"Enter Password:","LOGIN",JOptionPane.INFORMATION_MESSAGE);

            boolean loginStatus = login.loginUser(username,password,enteredUsername,enteredPassword);

            JOptionPane.showMessageDialog(null,login.returnLoginStatus(loginStatus, firstName,lastName));

            // QUICKCHAT
            if (loginStatus) {

                JOptionPane.showMessageDialog( null,"Welcome to QuickChat");

                boolean running = true;

                while (running) {

                    int option = Integer.parseInt( JOptionPane.showInputDialog(
                                    """
                                    Choose an option:
                                    
                                    1) Send Messages
                                    2) Show recently sent messages
                                    3) Quit
                                    4) Stored Messages
                                    """
                            )
                    );

                    switch (option) {

                        case 1:

                            sendMessagesOption();
                            break;

                        case 2:

                            showRecentlySentMessages();
                            break;

                        case 3:

                            JOptionPane.showMessageDialog(
                                    null,
                                    "Goodbye."
                            );

                            running = false;
                            break;

                        case 4:

                            storedMessagesMenu();
                            break;

                        default:

                            JOptionPane.showMessageDialog(
                                    null,
                                    "Invalid option selected."
                            );
                    }
                }
            }

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "Registration failed."
            );
        }
    }

    // ===================== Option 1: Send Messages =====================

    private static void sendMessagesOption() {

        int total = Integer.parseInt(
                JOptionPane.showInputDialog(
                        "How many messages would you like to send?"
                )
        );

        for (int i = 1; i <= total; i++) {

            String recipient =
                    JOptionPane.showInputDialog(
                            "Enter recipient number:"
                    );

            String messageText =
                    JOptionPane.showInputDialog(
                            "Enter message:"
                    );

            // Create Message object
            Message msg =
                    new Message(
                            i,
                            recipient,
                            messageText
                    );

            // Length check
            String lengthResult = msg.checkMessageLength();

            JOptionPane.showMessageDialog(null, lengthResult);

            if (!lengthResult.equals("Message ready to send.")) {

                i--;
                continue;
            }

            // ID validation
            if (msg.checkMessageID()) {

                JOptionPane.showMessageDialog(
                        null,
                        "Message ID successfully created."
                );
            }

            // Recipient validation
            String recipientResult = msg.checkRecipientCell();

            JOptionPane.showMessageDialog(
                    null,
                    recipientResult
            );

            if (!recipientResult.equals("Cell phone number successfully captured.")) {

                i--;
                continue;
            }

            // Message hash
            JOptionPane.showMessageDialog(
                    null,
                    "Message Hash: "
                            + msg.getMessageHash()
            );

            // Send/store/disregard
            String choice = msg.sentMessage();

            switch (choice) {

                case "SENT":

                    sentMessages.add(msg.printMessages());
                    messageHashes.add(msg.getMessageHash());
                    messageIDs.add(msg.getMessageID());

                    JOptionPane.showMessageDialog(
                            null,
                            "Message successfully sent."
                    );

                    // Print full details (requirement 7)
                    JOptionPane.showMessageDialog(
                            null,
                            msg.printMessages()
                    );

                    break;

                case "DISREGARD":

                    disregardedMessages.add(msg.printMessages());

                    JOptionPane.showMessageDialog(
                            null,
                            "Press 0 to delete message."
                    );

                    break;

                case "STORE":

                    msg.storeMessage();
                    storedMessagesArray.add(msg);
                    messageHashes.add(msg.getMessageHash());
                    messageIDs.add(msg.getMessageID());

                    JOptionPane.showMessageDialog(
                            null,
                            "Message successfully stored."
                    );

                    break;

                default:

                    JOptionPane.showMessageDialog(
                            null,
                            "No option selected."
                    );
            }
        }

        // Total messages (requirement 6)
        JOptionPane.showMessageDialog(
                null,
                "Total messages sent: "
                        + Message.returnTotalMessages()
        );
    }

    // ===================== Option 2: Show Recently Sent Messages =====================

    private static void showRecentlySentMessages() {

        if (sentMessages.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "No messages have been sent yet."
            );
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (String m : sentMessages) {
            sb.append(m).append("\n\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    // ===================== Option 4: Stored Messages =====================

    private static void storedMessagesMenu() {

        // Refresh stored messages array from JSON file each time we enter
        storedMessagesArray = Message.readStoredMessages();

        int subOption = Integer.parseInt(
                JOptionPane.showInputDialog(
                        """
                        Stored Messages - choose an option:
                        
                        1) Display sender and recipient of all stored messages
                        2) Display the longest stored message
                        3) Search for a message ID
                        4) Search for all messages for a particular recipient
                        5) Delete a message using its hash
                        6) Display full report of all stored messages
                        """
                )
        );

        switch (subOption) {

            case 1:
                displaySenderRecipient();
                break;

            case 2:
                displayLongestStoredMessage();
                break;

            case 3:
                searchByMessageID();
                break;

            case 4:
                searchByRecipient();
                break;

            case 5:
                deleteByHash();
                break;

            case 6:
                displayFullReport();
                break;

            default:
                JOptionPane.showMessageDialog(
                        null,
                        "Invalid option selected."
                );
        }
    }

    // a) Display the sender and recipient of all stored messages
    private static void displaySenderRecipient() {

        if (storedMessagesArray.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "No stored messages found."
            );
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (Message m : storedMessagesArray) {

            sb.append("Sender: ")
              .append("Developer")
              .append(" | Recipient: ")
              .append(m.getRecipient())
              .append("\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    // b) Display the longest stored message
    private static void displayLongestStoredMessage() {

        if (storedMessagesArray.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "No stored messages found."
            );
            return;
        }

        Message longest = storedMessagesArray.get(0);

        for (Message m : storedMessagesArray) {

            if (m.getMessage().length() > longest.getMessage().length()) {
                longest = m;
            }
        }

        JOptionPane.showMessageDialog(
                null,
                "Longest stored message:\n" + longest.getMessage()
        );
    }

    // c) Search for a message ID and display the corresponding recipient and message
    private static void searchByMessageID() {

        String searchID = JOptionPane.showInputDialog(
                "Enter the message ID to search for:"
        );

        for (Message m : storedMessagesArray) {

            if (m.getMessageID().equals(searchID)) {

                JOptionPane.showMessageDialog(
                        null,
                        "Recipient: " + m.getRecipient()
                                + "\nMessage: " + m.getMessage()
                );
                return;
            }
        }

        JOptionPane.showMessageDialog(
                null,
                "No stored message found with ID: " + searchID
        );
    }

    // d) Search for all messages stored for a particular recipient
    private static void searchByRecipient() {

        String searchRecipient = JOptionPane.showInputDialog(
                "Enter the recipient number to search for:"
        );

        StringBuilder sb = new StringBuilder();
        boolean found = false;

        for (Message m : storedMessagesArray) {

            if (m.getRecipient().equals(searchRecipient)) {

                sb.append(m.getMessage()).append("\n");
                found = true;
            }
        }

        if (found) {

            JOptionPane.showMessageDialog(
                    null,
                    "Messages for " + searchRecipient + ":\n" + sb.toString()
            );

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "No stored messages found for recipient: " + searchRecipient
            );
        }
    }

    // e) Delete a message using the message hash
    private static void deleteByHash() {

        String hash = JOptionPane.showInputDialog(
                "Enter the message hash of the message to delete:"
        );

        boolean removed = Message.deleteStoredMessageByHash(hash);

        if (removed) {

            JOptionPane.showMessageDialog(
                    null,
                    "Message successfully deleted."
            );

            // Refresh local array after deletion
            storedMessagesArray = Message.readStoredMessages();

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "No stored message found with hash: " + hash
            );
        }
    }

    // f) Display a report that lists the full details of all the stored messages
    private static void displayFullReport() {

        if (storedMessagesArray.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "No stored messages found."
            );
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (Message m : storedMessagesArray) {

            sb.append("Message ID: ").append(m.getMessageID()).append("\n")
              .append("Message Hash: ").append(m.getMessageHash()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getMessage()).append("\n")
              .append("------------------------------\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }
}
