package re.catgirls.packets;

import re.catgirls.packets.buffer.PacketBuffer;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Packet {

    private long sessionId = ThreadLocalRandom.current().nextLong();

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public abstract void read(PacketBuffer buffer) throws Exception;

    public abstract void write(PacketBuffer buffer) throws Exception;
}