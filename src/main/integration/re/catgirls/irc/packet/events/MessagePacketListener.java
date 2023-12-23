package re.catgirls.irc.packet.events;

import re.catgirls.irc.packet.impl.s2c.S2CChatMessagePacket;
import re.catgirls.irc.ChatClient;
import re.catgirls.irc.listeners.impl.MessageListener;
import re.catgirls.irc.packets.connection.PacketHandler;
import re.catgirls.irc.packets.event.interfaces.IPacketSubscriber;

import java.util.Base64;
import java.util.zip.CRC32;

/**
 * <h2>Handle incoming chat messages</h2>
 * <p>
 * This is used to handle incoming chat messages from the server.
 * The server will send us a {@link S2CChatMessagePacket} with the message
 * encoded in base64 and a checksum.
 * We will decode the message from base64 and verify the checksum.
 * If the checksum is invalid, we will throw an exception.
 * </p>
 *
 * @author Lucy Luna
 */
public class MessagePacketListener {

    /**
     * Handle incoming chat message packet(s)
     *
     * @param packet the packet
     * @param ctx    the packet handler
     * @see S2CChatMessagePacket
     */
    @IPacketSubscriber
    public void handleMessagePacket(final S2CChatMessagePacket packet, final PacketHandler ctx) {
        final MessageListener listener = ChatClient.getInstance().getListeners().getMessageListener();

        try {
            final String message = new String(Base64.getDecoder().decode(packet.getMessage().get("message").getAsString()));
            final CRC32 checksum = new CRC32();
            checksum.update(message.getBytes());

            if (packet.getMessage().get("checksum").getAsLong() != checksum.getValue())
                throw new RuntimeException("invalid checksum");


            if (listener != null) listener.onResponse(packet, message, null);
        } catch (Exception e) {
            if (listener != null) listener.onResponse(packet, null, e);
        }
    }
}
