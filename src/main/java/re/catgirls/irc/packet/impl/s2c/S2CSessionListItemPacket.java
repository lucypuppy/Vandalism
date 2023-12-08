package re.catgirls.irc.packet.impl.s2c;

import com.google.gson.JsonObject;
import re.catgirls.irc.ChatClient;
import re.catgirls.irc.interfaces.IdentifiableEnum;
import re.catgirls.irc.session.UserProfile;
import re.catgirls.packets.Packet;
import re.catgirls.packets.buffer.PacketBuffer;

import java.util.Objects;

public class S2CSessionListItemPacket extends Packet {

    public S2CSessionListItemPacket() {
    }

    @Override
    public void read(PacketBuffer buffer) {
        final JsonObject object = buffer.readJson();

        final Action action = (Action) IdentifiableEnum.getById(object.get("action").getAsInt(), Action.class);

        final UserProfile profile = new UserProfile(
                object.get("name").getAsString(),
                (UserProfile.Rank) IdentifiableEnum.getById(object.get("rank").getAsInt(), UserProfile.Rank.class)
        );

        if (object.has("minecraft_server"))
            profile.setMcServer(object.get("minecraft_server").getAsString());

        if (object.has("minecraft_username"))
            profile.setMcUsername(object.get("minecraft_username").getAsString());

        switch (action) {
            case ADD_ENTRY -> {
                if (!profile.getName().equalsIgnoreCase(ChatClient.getInstance().getSession().getProfile().getName()))
                    ChatClient.getInstance().getListeners().getProfileListener().onUserJoin(profile);

                ChatClient.getInstance().getUsers().put(object.get("name").getAsString(), profile);
            }
            case UPDATE_ENTRY -> {
                final UserProfile oldProfile = ChatClient.getInstance().getUsers().get(object.get("name").getAsString());

                // Compare mc servers
                if (profile.getMcServer() != null && !Objects.equals(oldProfile.getMcServer(), profile.getMcServer()))
                    ChatClient.getInstance().getListeners().getProfileListener().onMinecraftServerUpdate(
                            profile,
                            profile.getMcServer(),
                            oldProfile.getMcServer()
                    );

                // Compare mc usernames
                if (profile.getMcUsername() != null && !Objects.equals(oldProfile.getMcUsername(), profile.getMcUsername()))
                    ChatClient.getInstance().getListeners().getProfileListener().onMinecraftUsernameUpdate(
                            profile,
                            profile.getMcUsername(),
                            oldProfile.getMcUsername()
                    );

                // Update in map
                ChatClient.getInstance().getUsers().put(object.get("name").getAsString(), profile);
            }
            case REMOVE_ENTRY -> {
                ChatClient.getInstance().getListeners().getProfileListener().onUserLeave(profile);
                ChatClient.getInstance().getUsers().remove(object.get("name").getAsString());
            }
        }
    }

    @Override
    public void write(PacketBuffer buffer) {
    }

    // response codes lulz
    public enum Action implements IdentifiableEnum {
        ADD_ENTRY(0),
        UPDATE_ENTRY(1),
        REMOVE_ENTRY(2);

        private final int id;

        Action(final int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }
}
