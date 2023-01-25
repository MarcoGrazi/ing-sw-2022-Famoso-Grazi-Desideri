package main.java.com.polimi.client.models;

/**
 * Message class.
 * Used to share infos between models and views
 */
public class Message {
    //The action performed
    private String action;

    /**
     * Class constructor.
     * Initializes the action
     * @param action
     */
    public Message(String action) {
        this.action = action;
    }

    /**
     * @return the action performed
     */
    public String getAction() {
        return action;
    }
}
