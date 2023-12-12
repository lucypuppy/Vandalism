package de.vandalismdevelopment.vandalism.gui.impl.menu.impl.irc;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui_v2.ImWindow;
import de.vandalismdevelopment.vandalism.util.minecraft.ChatUtil;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import re.catgirls.irc.ChatClient;
import re.catgirls.irc.session.UserProfile;

/**
 * The IRC menu.
 * WARNING: This is an experimental feature and may change a lot in the future.
 */
public class IrcImGuiMenu extends ImWindow {

    public IrcImGuiMenu() {
        super("IRC");
    }

    public static final ImString ADDRESS = new ImString(64);
    public static final ImString USERNAME = new ImString(32);
    public static final ImString PASSWORD = new ImString(128);
    public static final ImBoolean AUTO_CONNECT = new ImBoolean(true);

    private final ImString messageField = new ImString(512);
    private final IrcHelper helper = new IrcHelper();

    /**
     * The text input for the IRC messages.
     *
     * @param message The message to send.
     */
    private void drawText(String message) {
        String[] colorCodes = message.split("§");
        int partsDrawn = 0;

        for (String part : colorCodes) {
            if (part.isEmpty()) continue;
            int red = 255;
            int green = 255;
            int blue = 255;

            Formatting colorC = Formatting.byCode(part.charAt(0));
            if (colorC != null && colorC.getColorValue() != null) {
                red = (colorC.getColorValue() >> 16) & 0xFF;
                green = (colorC.getColorValue() >> 8) & 0xFF;
                blue = colorC.getColorValue() & 0xFF;
            }

            ImGui.textColored(red, green, blue, 255, part.substring(1));

            partsDrawn++;
            if (partsDrawn != colorCodes.length)
                ImGui.sameLine(0, 0);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (ImGui.begin("IRC##irc", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar)) {

            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("Connection")) {
                    ImGui.pushItemWidth(250);

                    /* server info */
                    ImGui.inputText("Server address", ADDRESS);
                    ImGui.inputText("Username", USERNAME);
                    ImGui.inputText("Password", PASSWORD, ImGuiInputTextFlags.Password);

                    /* connect/disconnect buttons */
                    if (ImGui.button("Connect", helper.isConnected() ? 115 : 250, 30)) {
                        try {
                            helper.disconnect();
                            helper.connect(ADDRESS.get(), USERNAME.get(), PASSWORD.get());
                        } catch (Exception ignored) {
                        }
                    }

                    if (helper.isConnected()) {
                        ImGui.sameLine();
                        if (ImGui.button("Disconnect", 125, 30)) {
                            try {
                                helper.disconnect();
                            } catch (Exception ignored) {
                            }
                        }
                    }

                    ImGui.popItemWidth();
                    ImGui.endMenu();
                }

                if (ImGui.button("Clear history")) helper.getMessages().clear();

                ImGui.endMenuBar();
            }

            ImGui.beginChild("##irc_channel_pane", 100, ImGui.getWindowHeight() - 118, false);

            /* Channel selection */
            ImGui.selectable("Public chat");

            ImGui.separator();

            /* Online users */

            for (final UserProfile value : ChatClient.getInstance().getUsers().values()) {
                ImGui.selectable((value.getName()));

                if (ImGui.isItemHovered() && ImGui.isMouseClicked(1)) {
                    ImGui.openPopup("##" + value.getName(), ImGuiPopupFlags.MouseButtonRight);
                }

                if (ImGui.beginPopup("##" + value.getName())) {
                    if (ImGui.menuItem("Join %s's server".formatted(value.getName()))) {
                        ChatUtil.chatMessage("gay sex! :3");
                    }

                    ImGui.endPopup();
                }
            }

            ImGui.endChild();

            /* put channels list & message box in the same line */

            ImGui.sameLine();

            ImGui.beginChild("##messagebox_scrollable", 0, ImGui.getWindowHeight() - 118, false, ImGuiWindowFlags.HorizontalScrollbar);
            ImGui.setScrollY(ImGui.getScrollMaxY());

            /* message history in child */

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 1.5f);

            for (String message : helper.getMessages()) {
                drawText(" " + message);
            }

            ImGui.popStyleVar();

            /* end child */

            ImGui.endChild();

            /* message input */

            ImGui.pushItemWidth(ImGui.getWindowWidth() - (40 * 2));

            // wtf
            if (ImGui.inputText(
                    "##gaysexomg",
                    messageField,
                    ImGuiInputTextFlags.EnterReturnsTrue
            )) sendMessage(messageField);

            ImGui.popItemWidth();

            /* send button */

            ImGui.sameLine();

            /* hhh */
            if (ImGui.button("Send")) {
                sendMessage(messageField);
            }

            /* end child */
            ImGui.end();
        }
    }

    /**
     * Sends a message to the IRC server.
     *
     * @param message The message to send.
     */
    private void sendMessage(final ImString message) {
        if (message.get().isEmpty() || !helper.isConnected()) {
            return;
        }

        // send message request
        ChatClient.getInstance().getSession().requestMessage(message.get());

        // reset message
        message.set("");

        // set keyboard focus
        ImGui.setKeyboardFocusHere(-1);
    }

}
