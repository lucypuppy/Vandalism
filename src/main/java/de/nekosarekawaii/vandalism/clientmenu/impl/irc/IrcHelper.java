package de.nekosarekawaii.vandalism.clientmenu.impl.irc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.minecraft.util.Formatting;
import re.catgirls.irc.ChatClientWrapper;
import re.catgirls.irc.listeners.impl.ProfileListener;
import re.catgirls.irc.session.UserProfile;

import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A utility class for the IRC.
 * Please do not move this! It is used by the IRC menu. **Only** the IRC menu.
 *
 * @author Lucy Luna
 */
public class IrcHelper {

    private final ChatClientWrapper wrapper;
    private final CopyOnWriteArrayList<String> messages = new CopyOnWriteArrayList<>();

    /**
     * Create a new IRC helper.
     */
    public IrcHelper() {
        this.wrapper = new ChatClientWrapper();
    }

    /**
     * Connect to the IRC.
     * @param address The address of the IRC server.
     * @param username The username to use.
     * @param password The password to use.
     */
    public void connect(final String address, final String username, final String password) {
        try {
            if (username.isEmpty() || password.isEmpty()) {
                ChatUtil.errorChatMessage("You must set a username and password to use the IRC.");
                return;
            }

            /* Set a login listener */
            wrapper.getClient().getListeners().setLoginListener(packet -> {
                switch (packet.getResult()) {
                    case UNKNOWN_FAILURE -> {
                        messages.add("%sYou have provided invalid credentials.".formatted(Formatting.RED));
                        this.wrapper.disconnect();
                    }
                    case BANNED -> {
                        messages.add("%sYou have been banned.".formatted(Formatting.RED));
                        this.wrapper.disconnect();
                    }
                    case OK -> {
                        messages.add("%sYou have been authenticated as %s".
                                formatted(
                                        Formatting.GREEN,
                                        wrapper.getClient().getSession().getProfile().getName()
                                ));

                        Vandalism.getInstance().getConfigManager().save();
                    }
                }
            });

            /* Add message listeners (for IRC menu) */
            wrapper.getClient().getListeners().setMessageListener((packet, message, exception) -> {
                if (exception != null) {
                    messages.add("%s(%sIRC%s) %s%s".formatted(
                            Formatting.GRAY.toString(),
                            Formatting.RED.toString(),
                            Formatting.GRAY.toString(),
                            Formatting.RED.toString(),
                            exception.getMessage()
                    ));
                    return;
                }

                if (packet.getSender() == null) {
                    final String msg = "%s(%sIRC%s) %s%s".formatted(
                            Formatting.GRAY.toString(),
                            Formatting.WHITE.toString(),
                            Formatting.GRAY.toString(),
                            Formatting.WHITE.toString(),
                            message
                    );

                    ChatUtil.chatMessage(msg);
                    messages.add(message);
                    return;
                }

                /* Build prefix */
                String name = packet.getSender().get("username").getAsString();
                if (packet.getSender().has("prefix"))
                    name = "%s(%s%s) %s%s".formatted(
                            Formatting.GRAY,
                            packet.getSender().get("prefix").getAsString(),
                            Formatting.GRAY,
                            Formatting.WHITE,
                            packet.getSender().get("username").getAsString()
                    );

                final String msg = "%s%s%s: %s%s".formatted(
                        Formatting.WHITE.toString(),
                        name,
                        Formatting.GRAY.toString(),
                        Formatting.WHITE.toString(),
                        message
                );

                /* Add message(s) to chat & irc menu */
                messages.add(msg);
                ChatUtil.chatMessage(
                        "%s(%sIRC%s) %s".formatted(
                                Formatting.GRAY.toString(),
                                Formatting.WHITE.toString(),
                                Formatting.GRAY.toString(),
                                msg));
            });

            /* Add profile listener */
            wrapper.getClient().getListeners().setProfileListener(new ProfileListener() {

                @Override
                public void onMinecraftServerUpdate(UserProfile profile, String newServer, String oldServer) {
                }

                @Override
                public void onMinecraftUsernameUpdate(UserProfile profile, String newUsername, String oldUpdate) {
                }

                @Override
                public void onUserJoin(UserProfile profile) {
                    messages.add(Formatting.GREEN + profile.getName() + " joined the chat.");
                }

                @Override
                public void onUserLeave(UserProfile profile) {
                    messages.add(Formatting.RED + profile.getName() + " left the chat.");
                }
            });

            /* finally connect to the IRC */
            wrapper.connect(new InetSocketAddress(
                    address.split(":")[0],
                    Integer.parseInt(address.split(":")[1])
            ), 5000, username, password, "vandalism");
        } catch (Exception e) {
            ChatUtil.errorChatMessage(e.getMessage());
            messages.add(Formatting.RED + e.getMessage());
        }

    }

    public void disconnect() {
        wrapper.getClient().getUsers().clear();
        wrapper.disconnect();
    }

    public boolean isConnected() {
        return wrapper.getClient().getSession() != null && wrapper.getClient().getSession().getHandler().isConnected();
    }

    public CopyOnWriteArrayList<String> getMessages() {
        return messages;
    }
}
