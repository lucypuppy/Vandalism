package re.catgirls.irc.packet.impl.c2s;

import com.google.gson.JsonObject;
import re.catgirls.irc.packets.Packet;
import re.catgirls.irc.packets.buffer.PacketBuffer;

public class C2SDataUpdatePacket extends Packet {

    private final String mcServer, mcUsername;

    public C2SDataUpdatePacket() {
        throw new RuntimeException("Not supported");
    }

    public C2SDataUpdatePacket(final String mcServer, final String mcUsername) {
        this.mcServer = mcServer;
        this.mcUsername = mcUsername;
    }

    @Override
    public void write(final PacketBuffer buffer) {
        final JsonObject object = new JsonObject();
        object.addProperty("minecraft_server", mcServer);
        object.addProperty("minecraft_username", mcUsername);
        buffer.writeJson(object);
    }

    @Override
    public void read(final PacketBuffer buffer) {
    }

    public String getMcServer() {
        return mcServer;
    }

    public String getMcUsername() {
        return mcUsername;
    }
}
