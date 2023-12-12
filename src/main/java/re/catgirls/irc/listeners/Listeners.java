package re.catgirls.irc.listeners;

import re.catgirls.irc.listeners.impl.DisconnectListener;
import re.catgirls.irc.listeners.impl.LoginListener;
import re.catgirls.irc.listeners.impl.MessageListener;
import re.catgirls.irc.listeners.impl.ProfileListener;

/**
 * <h2>Listeners for the IRC client</h2>
 * These listeners are used to handle events from the IRC client.
 *
 * @author Lucy Luna
 */
public class Listeners {

    private LoginListener loginListener;
    private DisconnectListener disconnectListener;
    private MessageListener messageListener;
    private ProfileListener profileListener;

    /**
     * Get the login listener
     *
     * @return the login listener
     */
    public LoginListener getLoginListener() {
        if (loginListener == null)
            throw new NullPointerException("login listener can not be null!");

        return loginListener;
    }

    /**
     * Get the disconnect listener
     *
     * @return the disconnect listener
     */
    public DisconnectListener getDisconnectListener() {
        return disconnectListener;
    }

    /**
     * Get the message listener
     *
     * @return the message listener
     */
    public MessageListener getMessageListener() {
        return messageListener;
    }

    /**
     * Get the profile listener
     *
     * @return the profile listener
     */
    public ProfileListener getProfileListener() {
        return profileListener;
    }

    /**
     * Set the login listener
     *
     * @param loginListener the login listener
     */
    public void setLoginListener(final LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    /**
     * Set the disconnect listener
     *
     * @param disconnectListener the disconnect listener
     */
    public void setDisconnectListener(final DisconnectListener disconnectListener) {
        this.disconnectListener = disconnectListener;
    }

    /**
     * Set the message listener
     *
     * @param messageListener the message listener
     */
    public void setMessageListener(final MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Set the profile listener
     *
     * @param profileListener the profile listener
     */
    public void setProfileListener(final ProfileListener profileListener) {
        this.profileListener = profileListener;
    }
}
