package re.catgirls.irc.packet.impl.c2s;

import com.google.gson.JsonObject;
import re.catgirls.irc.session.UserProfile;
import re.catgirls.irc.packets.Packet;
import re.catgirls.irc.packets.buffer.PacketBuffer;

import java.util.Base64;
import java.util.zip.CRC32;

@SuppressWarnings("unused")
public class C2SChatMessagePacket extends Packet {

    private final String message;
    private final UserProfile profile;

    public C2SChatMessagePacket() {
        throw new RuntimeException("Not supported");
    }

    public C2SChatMessagePacket(final UserProfile profile, final String message) {
        this.message = message;
        this.profile = profile;
    }

    @Override
    public void read(final PacketBuffer buffer) {

    }

    @Override
    public void write(final PacketBuffer buffer) {
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
