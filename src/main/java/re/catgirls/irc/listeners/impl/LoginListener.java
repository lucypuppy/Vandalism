package re.catgirls.irc.listeners.impl;

import re.catgirls.irc.packet.impl.s2c.S2CLoginResponsePacket;

public interface LoginListener {
    void onResponse(final S2CLoginResponsePacket packet);
}