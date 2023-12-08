package re.catgirls.irc.packet.impl.c2s;

import com.google.gson.JsonObject;
import re.catgirls.irc.session.UserProfile;
import re.catgirls.packets.Packet;
import re.catgirls.packets.buffer.PacketBuffer;

import java.util.Base64;
import java.util.zip.CRC32;

public class C2SChatMessagePacket extends Packet {

    private String message;
    private UserProfile profile;

    public C2SChatMessagePacket() {}

    public C2SChatMessagePacket(UserProfile profile, String message) {
        this.message = message;
        this.profile = profile;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        final CRC32 checksum = new CRC32();
        checksum.update(message.getBytes());

        final JsonObject object = new JsonObject();
        object.addProperty("message", Base64.getEncoder().encodeToString(message.getBytes()));
        object.addProperty("checksum", checksum.getValue());
        object.addProperty("jwt", getProfile().getJwt());

        buffer.writeJson(object);
    }

    public UserProfile getProfile() {
        return profile;
    }

    public String getMessage() {
        return message;
    }
}
