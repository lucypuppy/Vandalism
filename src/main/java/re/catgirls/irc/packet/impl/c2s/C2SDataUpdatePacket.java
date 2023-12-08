package re.catgirls.irc.packet.impl.c2s;

import com.google.gson.JsonObject;
import re.catgirls.packets.Packet;
import re.catgirls.packets.buffer.PacketBuffer;

public class C2SDataUpdatePacket extends Packet {

    private String mcServer, mcUsername;

    public C2SDataUpdatePacket() {
    }

    public C2SDataUpdatePacket(final String mcServer, final String mcUsername) {
        this.mcServer = mcServer;
        this.mcUsername = mcUsername;
    }

    @Override
    public void read(PacketBuffer buffer) {
        final JsonObject object = buffer.readJson();
        this.mcServer = object.get("minecraft_server").getAsString();
        this.mcUsername = object.get("minecraft_username").getAsString();
    }

    @Override
    public void write(PacketBuffer buffer) {
        final JsonObject object = new JsonObject();
        object.addProperty("minecraft_server", mcServer);
        object.addProperty("minecraft_username", mcUsername);
        buffer.writeJson(object);
    }

    public String getMcServer() {
        return mcServer;
    }

    public String getMcUsername() {
        return mcUsername;
    }
}
