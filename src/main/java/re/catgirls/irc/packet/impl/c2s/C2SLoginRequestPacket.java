package re.catgirls.irc.packet.impl.c2s;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import re.catgirls.packets.Packet;
import re.catgirls.packets.buffer.PacketBuffer;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class C2SLoginRequestPacket extends Packet {

    private String username;
    private String password;

    // @formatter:off
    public C2SLoginRequestPacket() { throw new RuntimeException("Not implemented"); }

    public C2SLoginRequestPacket(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    // @formatter:on

    @Override
    public void write(PacketBuffer buffer) {
        final JsonObject object = new JsonObject();
        object.addProperty("username",  username);
        object.addProperty("password", Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
        buffer.writeJson(object);
    }

    @Override
    public void read(PacketBuffer buffer) {

    }
}
