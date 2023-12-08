package re.catgirls.irc.packet.impl.s2c;

import com.google.gson.JsonObject;
import re.catgirls.irc.interfaces.IdentifiableEnum;
import re.catgirls.irc.session.UserProfile;
import re.catgirls.packets.Packet;
import re.catgirls.packets.buffer.PacketBuffer;

public class S2CLoginResponsePacket extends Packet {

    private Result result;
    private String reason;
    private UserProfile profile;

    // @formatter:off

    public S2CLoginResponsePacket() { }

    @Override
    public void write(PacketBuffer buffer) {}

    // @formatter:on


    @Override
    public void read(final PacketBuffer buffer) {
        final JsonObject object = buffer.readJson();
        result = Result.getById(object.get("result").getAsInt());
        switch (result) {
            case BANNED -> reason = object.get("reason").getAsString();
            case OK -> {
                final UserProfile profile = new UserProfile(
                        object.get("username").getAsString(),
                        object.get("client").getAsString(), /* we don't question this, I send the mc client in the login response too. */
                        (UserProfile.Rank) IdentifiableEnum.getById(object.get("rank").getAsInt(), UserProfile.Rank.class)
                );
                profile.setJwt(object.get("jwt").getAsString());
                this.profile = profile;
            }
        }

    }

    // response codes lulz
    public enum Result {
        OK(0),
        INVALID_HWID(1),
        BANNED(2),
        UNKNOWN_FAILURE(3);

        private final int id;

        Result(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Result getById(final int id) {
            for (final Result result : values()) {
                if (result.getId() == id)
                    return result;
            }

            throw new NullPointerException("unknown result code: %d".formatted(id));
        }
    }

    public Result getResult() {
        return result;
    }

    public String getReason() {
        return reason;
    }

    public UserProfile getProfile() {
        return profile;
    }
}
