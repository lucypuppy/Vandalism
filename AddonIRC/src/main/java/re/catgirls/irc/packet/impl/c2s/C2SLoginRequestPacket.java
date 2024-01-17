package re.catgirls.irc.packet.impl.c2s;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import re.catgirls.irc.packets.Packet;
import re.catgirls.irc.packets.buffer.PacketBuffer;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class C2SLoginRequestPacket extends Packet {

    private final String username;
    private final String password;
    private final String client;

    public C2SLoginRequestPacket() { throw new RuntimeException("Not implemented"); }

    public C2SLoginRequestPacket(final String username, final String password, final String client) {
        this.username = username;
        this.password = password;
        this.client = client;
    }

    @Override
    public void write(final PacketBuffer buffer) {
        final JsonObject object = new JsonObject();
        object.addProperty("username", username);
        object.addProperty("password", Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
        object.addProperty("client", client);
        buffer.writeJson(object);
    }

    @Override
    public void read(final PacketBuffer buffer) {
    }
}
