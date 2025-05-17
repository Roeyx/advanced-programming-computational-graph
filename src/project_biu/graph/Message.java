package project_biu.graph;

import java.util.Date;

/**
 * Represents a message exchanged between agents in the system.
 * A message can encapsulate data in multiple formats including text, binary (byte array), and numeric (double).
 * Additionally, each message includes a timestamp to indicate when it was created.
 *
 * The class provides constructors to automatically convert and store all relevant representations
 * of the input data, allowing agents to flexibly interpret the message according to their needs.
 */
public class Message {

    // The raw message data in byte format, useful for binary communication or encoding
    public final byte[] data;

    // The message represented as a plain string, suitable for logging or human-readable formats
    public final String asText;

    // If the message content is numeric, it is parsed and stored here as a double.
    // If the input cannot be parsed as a number, this field will be set to NaN.
    public final double asDouble;

    // Timestamp indicating when the message instance was created
    public final Date date;

    /**
     * Constructs a Message from a given string input.
     * Automatically stores the string as text, byte array, and attempts to parse it as a double.
     * If parsing fails, the double field is set to NaN.
     *
     * @param asText the message content represented as a string
     */
    public Message(String asText) {
        this.asText = new String(asText);      // Save original string
        this.data = asText.getBytes();         // Convert to byte array for raw data usage

        double temp;
        try {
            temp = Double.parseDouble(asText); // Try converting the string to a numeric value
        } catch (NumberFormatException e) {
            temp = Double.NaN;                 // Set to NaN if the input isn't a valid number
        }

        this.asDouble = temp;
        this.date = new Date();                // Capture the current time as the creation timestamp
    }

    /**
     * Constructs a Message from a byte array.
     * Internally converts the byte array to a string and delegates to the string constructor.
     * This enables consistent behavior regardless of the input format.
     *
     * @param data the message data in byte array format
     */
    public Message(byte[] data) {
        this(new String(data)); // Convert to string and delegate to string constructor
    }

    /**
     * Constructs a Message from a numeric (double) input.
     * Converts the number to a string for internal consistency and then stores it as text and byte array.
     *
     * @param data the numeric content to be encapsulated in the message
     */
    public Message(double data) {
        this(Double.toString(data)); // Convert to string and delegate to string constructor
    }
}
