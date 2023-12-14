package re.catgirls.irc.session;

import re.catgirls.irc.packet.impl.c2s.C2SChatMessagePacket;
import re.catgirls.irc.util.EncryptionHelper;
import re.catgirls.packets.Packet;
import re.catgirls.packets.connection.PacketHandler;

import java.security.KeyPair;
import java.security.PublicKey;

/**
 * This class represents a session.
 * It contains the packet handler, the user profile, the keys and the status of the encryption
 *
 * @author Lucy Luna
 * @see PacketHandler
 * @see UserProfile
 * @see EncryptionHelper
 */
public class Session {

    /* packet handler for the session */
    private final PacketHandler handler;

    /* keys */
    private PublicKey serverPublicKey;
    private final KeyPair clientKeyPair;

    /* user data */
    private UserProfile profile;

    /* status of the encryption */
    private boolean encryptionReady = false;

    /**
     * Create a new session
     *
     * @param handler the packet handler
     */
    public Session(final PacketHandler handler) {
        this.handler = handler;
        clientKeyPair = EncryptionHelper.generateKeyPair();
    }

    /**
     * Get the user profile
     *
     * @return the user profile
     */
    public UserProfile getProfile() {
        return profile;
    }

    /**
     * Get the packet handler
     *
     * @return the packet handler
     */
    public PacketHandler getHandler() {
        return handler;
    }

    /**
     * Get the client key pair
     *
     * @return the client key pair
     */
    public KeyPair getClientKeyPair() {
        return clientKeyPair;
    }

    /**
     * Get the public key provided by the server
     *
     * @return the server public key
     */
    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    /**
     * Check if the encryption is ready
     *
     * @return if the encryption is ready
     */
    public boolean isEncryptionNotReady() {
        return !encryptionReady;
    }

    /**
     * Set the public key provided by the server
     *
     * @param publicKey the public key
     */
    public void setServerPublicKey(PublicKey publicKey) {
        this.serverPublicKey = publicKey;
    }

    /**
     * Set the user profile
     *
     * @param profile the user profile
     */
    public void setProfile(final UserProfile profile) {
        this.profile = profile;
    }

    /**
     * Set the status of the encryption
     *
     * @param encryptionReady the status of the encryption
     */
    public void setEncryptionReady(boolean encryptionReady) {
        this.encryptionReady = encryptionReady;
    }

    /**
     * Send a packet to the server
     *
     * @param packet the packet
     */
    public void send(final Packet packet) {
        if (getHandler() == null || !getHandler().isConnected())
            throw new NullPointerException("handler == null (not connected to the server?)");

        getHandler().send(packet);
    }

    /**
     * Send a message to the server
     *
     * @param message the message
     */
    public void requestMessage(final String message) {
        if (getHandler() == null || !getHandler().isConnected())
            throw new NullPointerException("handler == null (not connected to the server?)");

        getHandler().send(new C2SChatMessagePacket(getProfile(), message));
    }
}
