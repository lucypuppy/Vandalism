package re.catgirls.irc.packet.events;

import re.catgirls.irc.ChatClient;
import re.catgirls.irc.listeners.impl.LoginListener;
import re.catgirls.irc.packet.impl.s2c.S2CLoginResponsePacket;
import re.catgirls.packets.connection.PacketHandler;
import re.catgirls.packets.event.interfaces.IPacketSubscriber;

/**
 * <h2>Handle incoming login response packets</h2>
 * <p>
 * This is used to handle the response from the server
 * after we send a {@link re.catgirls.irc.packet.impl.c2s.C2SLoginRequestPacket}
 * to the server.
 * This packet will contain the profile of the user if the login was successful.
 * If the login was successful, the profile will be set in the session.
 * If the login was not successful, the profile will be null.
 * </p>
 *
 * @author Lucy Luna
 * @see S2CLoginResponsePacket
 */
public class LoginPacketListener {

    @IPacketSubscriber
    public void onPacketReceive(final S2CLoginResponsePacket packet, final PacketHandler ctx) {
        if (packet.getResult() == S2CLoginResponsePacket.Result.OK)
            ChatClient.getInstance().getSession().setProfile(packet.getProfile());

        final LoginListener listener = ChatClient.getInstance().getListeners().getLoginListener();
        if (listener != null) listener.onResponse(packet);
    }

}
