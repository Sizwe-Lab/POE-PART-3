/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Assignment_1;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Message {

    private static int totalMessages = 0;

    private String messageID;
    private int messageNumber;
    private String recipient;
    private String message;
    private String messageHash;

    private static final String JSON_FILE = "storedMessages.json";

    // Constructor
    public Message(
            int messageNumber,
            String recipient,
            String message) {

        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;

        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
    }

    // Generate random ID
    private String generateMessageID() {

        Random random = new Random();

        long number = 1000000000L
                + (long) (random.nextDouble()
                * 9000000000L);

        return String.valueOf(number);
    }

    // ===================== Getters =====================

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageHash() {
        return messageHash;
    }

    // ===================== Validation / Hash =====================

    // Check message ID
    public boolean checkMessageID() {

        return messageID.length() <= 10;
    }

    // Check message length (<= 250 characters)
    public String checkMessageLength() {

        if (message.length() > 250) {

            int excess = message.length() - 250;

            return "Message exceeds 250 characters by "
                    + excess
                    + "; please reduce the size.";

        } else {

            return "Message ready to send.";
        }
    }

    // Check recipient
    public String checkRecipientCell() {

        if (recipient.length() <= 12
                && recipient.startsWith("+27")) {

            return "Cell phone number successfully captured.";

        } else {

            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // Create hash
    public String createMessageHash() {

        String[] words = message.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        return (messageID.substring(0, 2)
                + ":" + messageNumber
                + ":" + firstWord + lastWord)
                .toUpperCase();
    }

    // ===================== Sending / Storing =====================

    // Send message - returns the user's choice as a flag string
    public String sentMessage() {

        String[] options = {
                "Send Message",
                "Disregard Message",
                "Store Message"
        };

        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose an option",
                "Message Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {

            case 0:

                totalMessages++;
                return "SENT";

            case 1:

                return "DISREGARD";

            case 2:

                return "STORE";

            default:

                return "NONE";
        }
    }

    // Print message
    public String printMessages() {

        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    // Return total messages
    public static int returnTotalMessages() {

        return totalMessages;
    }

    // ===================== JSON Storage (manual parsing) =====================

    /**
     * Appends this message to storedMessages.json as a JSON array.
     * Each entry is written on its own line in the form:
     * {"MessageID":"...","MessageHash":"...","Recipient":"...","Message":"..."}
     */
    public void storeMessage() {

        ArrayList<Message> existing = readStoredMessages();
        existing.add(this);

        writeStoredMessages(existing);
    }

    /**
     * Reads storedMessages.json and returns a list of Message objects
     * reconstructed from the stored data.
     * If the file doesn't exist or is empty, returns an empty list.
     */
    public static ArrayList<Message> readStoredMessages() {

        ArrayList<Message> messages = new ArrayList<>();

        File file = new File(JSON_FILE);

        if (!file.exists()) {
            return messages;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                // Skip array brackets, blank lines, etc.
                if (line.isEmpty()
                        || line.equals("[")
                        || line.equals("]")) {
                    continue;
                }

                // Remove trailing comma if present
                if (line.endsWith(",")) {
                    line = line.substring(0, line.length() - 1);
                }

                // Remove surrounding braces
                if (line.startsWith("{")) {
                    line = line.substring(1);
                }
                if (line.endsWith("}")) {
                    line = line.substring(0, line.length() - 1);
                }

                if (line.isEmpty()) {
                    continue;
                }

                Message msg = parseJsonLine(line);

                if (msg != null) {
                    messages.add(msg);
                }
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error reading stored messages."
            );
        }

        return messages;
    }

    /**
     * Parses a single flat JSON object line into a Message object.
     * Expected fields: MessageID, MessageHash, Recipient, Message, MessageNumber
     */
    private static Message parseJsonLine(String line) {

        String messageID = null;
        String messageHash = null;
        String recipient = null;
        String messageText = null;
        int messageNumber = 0;

        // Split on commas that separate key:value pairs.
        // Values are quoted strings, so commas inside the message text
        // would break a naive split — to keep this dependency-free and
        // robust, we extract each field by locating its key explicitly.

        messageID = extractField(line, "MessageID");
        messageHash = extractField(line, "MessageHash");
        recipient = extractField(line, "Recipient");
        messageText = extractField(line, "Message");

        String numberStr = extractField(line, "MessageNumber");
        if (numberStr != null) {
            try {
                messageNumber = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                messageNumber = 0;
            }
        }

        if (messageID == null
                || recipient == null
                || messageText == null) {
            return null;
        }

        // Build a Message object, then override the auto-generated
        // ID/hash with the stored values.
        Message msg = new Message(messageNumber, recipient, messageText);
        msg.messageID = messageID;
        msg.messageHash = (messageHash != null) ? messageHash : msg.createMessageHash();

        return msg;
    }

    /**
     * Extracts the value of a given key from a flat JSON object string.
     * Handles escaped quotes within the value.
     */
    private static String extractField(String json, String key) {

        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);

        if (keyIndex == -1) {
            return null;
        }

        int colonIndex = json.indexOf(":", keyIndex + searchKey.length());

        if (colonIndex == -1) {
            return null;
        }

        // Find the start of the value (skip whitespace, then quote)
        int valueStart = colonIndex + 1;

        while (valueStart < json.length()
                && json.charAt(valueStart) == ' ') {
            valueStart++;
        }

        if (valueStart >= json.length()) {
            return null;
        }

        // Numeric (unquoted) value
        if (json.charAt(valueStart) != '"') {

            int valueEnd = valueStart;

            while (valueEnd < json.length()
                    && json.charAt(valueEnd) != ','
                    && json.charAt(valueEnd) != '}') {
                valueEnd++;
            }

            return json.substring(valueStart, valueEnd).trim();
        }

        // Quoted string value - skip opening quote
        valueStart++;

        StringBuilder value = new StringBuilder();
        boolean escaped = false;

        for (int i = valueStart; i < json.length(); i++) {

            char c = json.charAt(i);

            if (escaped) {
                value.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"') {
                break;
            }

            value.append(c);
        }

        return value.toString();
    }

    /**
     * Writes the full list of stored messages back to storedMessages.json
     * as a JSON array, overwriting the existing file.
     */
    private static void writeStoredMessages(ArrayList<Message> messages) {

        try (FileWriter writer = new FileWriter(JSON_FILE, false)) {

            writer.write("[\n");

            for (int i = 0; i < messages.size(); i++) {

                Message m = messages.get(i);

                writer.write("{");
                writer.write("\"MessageID\":\"" + escapeJson(m.messageID) + "\",");
                writer.write("\"MessageNumber\":" + m.messageNumber + ",");
                writer.write("\"MessageHash\":\"" + escapeJson(m.messageHash) + "\",");
                writer.write("\"Recipient\":\"" + escapeJson(m.recipient) + "\",");
                writer.write("\"Message\":\"" + escapeJson(m.message) + "\"");
                writer.write("}");

                if (i < messages.size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("]\n");

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error storing message."
            );
        }
    }

    /**
     * Escapes double quotes and backslashes for safe JSON output.
     */
    private static String escapeJson(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    /**
     * Deletes a stored message by its message hash.
     * Returns true if a message was found and deleted, false otherwise.
     */
    public static boolean deleteStoredMessageByHash(String hash) {

        ArrayList<Message> existing = readStoredMessages();

        boolean removed = false;

        for (int i = 0; i < existing.size(); i++) {

            if (existing.get(i).getMessageHash().equalsIgnoreCase(hash)) {
                existing.remove(i);
                removed = true;
                break;
            }
        }

        if (removed) {
            writeStoredMessages(existing);
        }

        return removed;
    }
}
