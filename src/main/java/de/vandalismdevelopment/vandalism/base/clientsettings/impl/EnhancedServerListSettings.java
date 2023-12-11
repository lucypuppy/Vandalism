package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.awt.KeyBindValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;
import de.vandalismdevelopment.vandalism.integration.serverlist.ServerList;
import org.lwjgl.glfw.GLFW;

public class EnhancedServerListSettings extends ValueGroup {

    public EnhancedServerListSettings(final ClientSettings parent) {
        super(parent, "Enhanced Server List", "Enhanced Server List related configs.");
    }

    public final BooleanValue enhancedServerList = new BooleanValue(
            this,
            "Enhanced Server List",
            "Enables/Disables the enhanced server list mode.",
            true).
            onValueChange((oldValue, newValue) -> {
                if (!newValue) {
                    Vandalism.getInstance().getServerListManager().setSelectedServerList(ServerList.DEFAULT_SERVER_LIST_NAME);
                }
            });

    public final KeyBindValue pasteServerKey = new KeyBindValue(
            this,
            "Paste Server Key",
            "Change the key to paste a server from your clipboard.",
            GLFW.GLFW_KEY_INSERT
    ).visibleCondition(this.enhancedServerList::getValue);

    public final KeyBindValue copyServerKey = new KeyBindValue(
            this,
            "Copy Server Key",
            "Change the key to copy a server to your clipboard.",
            GLFW.GLFW_KEY_PAGE_DOWN
    ).visibleCondition(this.enhancedServerList::getValue);

    public final KeyBindValue deleteServerKey = new KeyBindValue(
            this,
            "Delete Server Key",
            "Change the key to delete a server from the server list.",
            GLFW.GLFW_KEY_DELETE
    ).visibleCondition(this.enhancedServerList::getValue);

    public final Value<Boolean> multiplayerScreenServerInformation = new BooleanValue(
            this,
            "Multiplayer Screen Server Information",
            "If enabled the Game shows all necessary server information behind a server list entry.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    public final Value<Integer> maxServerVersionLength = new IntegerValue(
            this,
            "Max Server Version Length",
            "Sets the max display length of a server version that is being displayed in the multiplayer screen.",
            60,
            6,
            250
    ).visibleCondition(() -> this.enhancedServerList.getValue() && this.multiplayerScreenServerInformation.getValue());

}
