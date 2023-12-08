package re.catgirls.irc.packet.events;

import re.catgirls.irc.ChatClient;
import re.catgirls.irc.packet.impl.c2s.C2SLoginRequestPacket;
import re.catgirls.irc.packet.impl.shared.SharedKeyExchangePacket;
import re.catgirls.packets.connection.PacketHandler;
import re.catgirls.packets.event.PacketSubscriber;

/**
 * <h2>Handle incoming key exchange packets</h2>
 * <p>
 * This is used to exchange RSA keys with the server
 * so that we can encrypt our data.
 * The server will send us its public key and we will send ours.
 * After that, we can encrypt our data with the server's public key
 * and the server can decrypt it with its private key.
 * This is used to encrypt every packet that we send to the server.
 * </p>
 *
 * @author Lucy Luna
 */
public class KeyExchangePacketListener {

    /**
     * Handle incoming key exchange packets
     *
     * @param packet the packet
     * @param ctx    the packet handler
     * @see SharedKeyExchangePacket
     */
    @PacketSubscriber
    public void handleKeyExchange(SharedKeyExchangePacket packet, PacketHandler ctx) {
        ChatClient.getInstance().getSession().setServerPublicKey(packet.getKey());

        ctx.send(new SharedKeyExchangePacket(ChatClient.getInstance().getSession().getClientKeyPair().getPublic()));
        ChatClient.getInstance().getSession().setEncryptionReady(true);

        ChatClient.getInstance().getSession().getHandler().send(new C2SLoginRequestPacket(ChatClient.getInstance().getUsername(), ChatClient.getInstance().getPassword()));
    }

}
