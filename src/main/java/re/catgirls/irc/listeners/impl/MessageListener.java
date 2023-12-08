package re.catgirls.irc.listeners.impl;

import re.catgirls.irc.packet.impl.s2c.S2CChatMessagePacket;

public interface MessageListener {
    void onResponse(final S2CChatMessagePacket packet, final String message, final Exception e);
}