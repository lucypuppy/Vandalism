package re.catgirls.irc.packet;

import re.catgirls.irc.interfaces.IdentifiableEnum;
import re.catgirls.irc.packet.events.KeyExchangePacketListener;
import re.catgirls.irc.packet.events.LoginPacketListener;
import re.catgirls.irc.packet.events.MessagePacketListener;
import re.catgirls.irc.packet.impl.c2s.C2SChatMessagePacket;
import re.catgirls.irc.packet.impl.c2s.C2SDataUpdatePacket;
import re.catgirls.irc.packet.impl.c2s.C2SLoginRequestPacket;
import re.catgirls.irc.packet.impl.s2c.S2CChatMessagePacket;
import re.catgirls.irc.packet.impl.s2c.S2CLoginResponsePacket;
import re.catgirls.irc.packet.impl.s2c.S2CSessionListItemPacket;
import re.catgirls.irc.packet.impl.shared.SharedKeepAlivePacket;
import re.catgirls.irc.packet.impl.shared.SharedKeyExchangePacket;
import re.catgirls.irc.packets.event.EventRegistry;
import re.catgirls.irc.packets.registry.SimplePacketRegistry;

public class PacketRegistry extends SimplePacketRegistry {

    private final EventRegistry eventRegistry = new EventRegistry();

    public PacketRegistry() throws RuntimeException {
        registerPacket(PacketId.KEEP_ALIVE, SharedKeepAlivePacket.class);
        registerPacket(PacketId.KEY_EXCHANGE, SharedKeyExchangePacket.class);
        registerPacket(PacketId.LOGIN_REQUEST, C2SLoginRequestPacket.class);
        registerPacket(PacketId.LOGIN_RESPONSE, S2CLoginResponsePacket.class);

        registerPacket(PacketId.MESSAGE_REQUEST, C2SChatMessagePacket.class);
        registerPacket(PacketId.MESSAGE_RESPONSE, S2CChatMessagePacket.class);

        registerPacket(PacketId.DATA_UPDATE_REQUEST, C2SDataUpdatePacket.class);
        registerPacket(PacketId.SESSION_LIST_ITEM, S2CSessionListItemPacket.class);

        eventRegistry.registerEvents(new KeyExchangePacketListener());
        eventRegistry.registerEvents(new LoginPacketListener());
        eventRegistry.registerEvents(new MessagePacketListener());
    }

    public EventRegistry getEventRegistry() {
        return eventRegistry;
    }

    public enum PacketId implements IdentifiableEnum {
        KEEP_ALIVE(0),
        KEY_EXCHANGE(1),

        // login
        LOGIN_REQUEST(2),
        LOGIN_RESPONSE(3),

        // messages
        MESSAGE_REQUEST(4),
        MESSAGE_RESPONSE(5),

        // user data
        DATA_UPDATE_REQUEST(6),
        SESSION_LIST_ITEM(7);

        private final int id;

        PacketId(final int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }
}
