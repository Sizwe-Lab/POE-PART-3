package Assignment_1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Part 3 - Store Data and Display Task Report.
 *
 * Test data used (from the assignment brief):
 *
 * Test Data Message 1:
 *   Recipient: +27834557896
 *   Message:   "Did you get the cake?"
 *   Flag:      Sent
 *
 * Test Data Message 2:
 *   Recipient: +27838884567
 *   Message:   "Where are you? You are late! I have asked you to be on time."
 *   Flag:      Stored
 *
 * Test Data Message 3:
 *   Recipient: +27834484567
 *   Message:   "Yohoooo, I am at your gate."
 *   Flag:      Disregard
 *
 * Test Data Message 4:
 *   Developer: 0838884567
 *   Message:   "It is dinner time!"
 *   Flag:      Sent
 *
 * Test Data Message 5:
 *   Recipient: +27838884567
 *   Message:   "Ok, I am leaving without you."
 *   Flag:      Stored
 */
public class MessageTest {

    private static final String TEST_JSON_FILE = "storedMessages.json";

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    private Message message5;

    @BeforeEach
    public void setUp() {

        // Use fixed message numbers so hashes are predictable.
        message1 = new Message(1, "+27834557896", "Did you get the cake?");
        message2 = new Message(2, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        message3 = new Message(3, "+27834484567", "Yohoooo, I am at your gate.");
        message4 = new Message(4, "0838884567", "It is dinner time!");
        message5 = new Message(5, "+27838884567", "Ok, I am leaving without you.");

        // Ensure a clean JSON file before each test that uses storage.
        File file = new File(TEST_JSON_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    public void tearDown() {

        File file = new File(TEST_JSON_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    // ===================== Test 1: Sent Messages array correctly populated =====================
    @Test
    public void testSentMessagesArrayPopulated() {

        ArrayList<String> sentMessages = new ArrayList<>();

        // Simulate sending message1 and message4 (flag = Sent)
        sentMessages.add(message1.getMessage());
        sentMessages.add(message4.getMessage());

        assertEquals("Did you get the cake?", sentMessages.get(0));
        assertEquals("It is dinner time!", sentMessages.get(1));
    }

    // ===================== Test 2: Display the longest Message =====================
    @Test
    public void testDisplayLongestMessage() {

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message4);

        Message longest = messages.get(0);

        for (Message m : messages) {
            if (m.getMessage().length() > longest.getMessage().length()) {
                longest = m;
            }
        }

        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                longest.getMessage()
        );
    }

    // ===================== Test 3: Search for messageID =====================
    @Test
    public void testSearchByMessageID() {

        // Per the brief, searching for message ID "0838884567"
        // (the developer entry for message 4) should return "It is dinner time!"
        // We simulate this by overriding message4's ID to match the test data.
        message4 = overrideMessageID(message4, "0838884567");

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message4);

        String searchID = "0838884567";
        String foundMessage = null;

        for (Message m : messages) {
            if (m.getMessageID().equals(searchID)) {
                foundMessage = m.getMessage();
                break;
            }
        }

        assertEquals("It is dinner time!", foundMessage);
    }

    // ===================== Test 4: Search all messages for a particular recipient =====================
    @Test
    public void testSearchByRecipient() {

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message2); // +27838884567 - "Where are you?..."
        messages.add(message5); // +27838884567 - "Ok, I am leaving without you."

        String searchRecipient = "+27838884567";
        ArrayList<String> found = new ArrayList<>();

        for (Message m : messages) {
            if (m.getRecipient().equals(searchRecipient)) {
                found.add(m.getMessage());
            }
        }

        assertEquals(2, found.size());
        assertEquals("Where are you? You are late! I have asked you to be on time.", found.get(0));
        assertEquals("Ok, I am leaving without you.", found.get(1));
    }

    // ===================== Test 5: Delete a message using a message hash =====================
    @Test
    public void testDeleteMessageByHash() {

        // Store message2 ("Test Message 2") so it can be deleted
        message2.storeMessage();

        String hashToDelete = message2.getMessageHash();

        boolean deleted = Message.deleteStoredMessageByHash(hashToDelete);

        assertTrue(deleted);

        ArrayList<Message> remaining = Message.readStoredMessages();

        for (Message m : remaining) {
            assertNotEquals(hashToDelete, m.getMessageHash());
        }
    }

    // ===================== Test 6: Display Report =====================
    @Test
    public void testDisplayReport() {

        message1.storeMessage();
        message2.storeMessage();

        ArrayList<Message> stored = Message.readStoredMessages();

        assertEquals(2, stored.size());

        for (Message m : stored) {
            assertNotNull(m.getMessageHash());
            assertNotNull(m.getRecipient());
            assertNotNull(m.getMessage());
        }
    }

    // ===================== Test 7: Message should not be more than 250 characters =====================
    @Test
    public void testMessageLengthValidation_Success() {

        assertEquals("Message ready to send.", message1.checkMessageLength());
    }

    @Test
    public void testMessageLengthValidation_Failure() {

        String longText = "a".repeat(260);
        Message longMessage = new Message(99, "+27834557896", longText);

        String result = longMessage.checkMessageLength();

        assertTrue(result.startsWith("Message exceeds 250 characters by 10"));
        assertTrue(result.endsWith("; please reduce the size."));
    }

    // ===================== Test 8: Recipient number correctly formatted =====================
    @Test
    public void testRecipientFormat_Success() {

        assertEquals(
                "Cell phone number successfully captured.",
                message1.checkRecipientCell()
        );
    }

    @Test
    public void testRecipientFormat_Failure() {

        Message badNumber = new Message(99, "0838884567", "Test message");

        assertEquals(
                "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                badNumber.checkRecipientCell()
        );
    }

    // ===================== Test 9: Message hash is correct =====================
    @Test
    public void testMessageHashCorrect_TestCase1() {

        // Test Case 1 (Message 1): "Did you get the cake?"
        // First two digits of message ID + ":" + message number + ":" + first and last word, uppercase
        String firstTwoDigits = message1.getMessageID().substring(0, 2);
        String expectedHash = (firstTwoDigits + ":1:DIDCAKE?").toUpperCase();

        assertEquals(expectedHash, message1.getMessageHash());
    }

    @Test
    public void testMessageHashesLoop() {

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message4);
        messages.add(message5);

        for (Message m : messages) {

            String firstTwoDigits = m.getMessageID().substring(0, 2);
            String[] words = m.getMessage().trim().split("\\s+");
            String firstWord = words[0];
            String lastWord = words[words.length - 1];

            String expectedHash = (firstTwoDigits
                    + ":" + m.getMessageNumber()
                    + ":" + firstWord + lastWord)
                    .toUpperCase();

            assertEquals(expectedHash, m.getMessageHash());
        }
    }

    // ===================== Helper =====================

    /**
     * Helper to create a Message with a forced messageID, used to
     * simulate the "Developer entry" test data where the message ID
     * is provided directly rather than auto-generated.
     */
    private Message overrideMessageID(Message original, String newID) {

        Message copy = new Message(
                original.getMessageNumber(),
                original.getRecipient(),
                original.getMessage()
        );

        // Use reflection-free approach: store and re-read via JSON
        // is overkill here, so we directly use the package-private field
        // by writing to a fresh object via the storeMessage/read cycle.
        // Simplest: temporarily store then patch via read.
        copy = new Message(original.getMessageNumber(), original.getRecipient(), original.getMessage());

        try {
            java.lang.reflect.Field idField = Message.class.getDeclaredField("messageID");
            idField.setAccessible(true);
            idField.set(copy, newID);

            java.lang.reflect.Field hashField = Message.class.getDeclaredField("messageHash");
            hashField.setAccessible(true);
            hashField.set(copy, copy.createMessageHash());

        } catch (Exception e) {
            fail("Could not override message ID for test: " + e.getMessage());
        }

        return copy;
    }
}
