package de.vandalismdevelopment.vandalism.config.impl.main.impl;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.main.MainConfig;
import de.vandalismdevelopment.vandalism.enhancedserverlist.ServerList;
import de.vandalismdevelopment.vandalism.util.GlfwKeyName;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.KeyInputValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderIntegerValue;

public class EnhancedServerListCategory extends ValueCategory {

    public EnhancedServerListCategory(final MainConfig parent) {
        super(
                "Enhanced Server List",
                "Enhanced Server List related configs.",
                parent
        );
    }

    public final Value<Boolean> enhancedServerList = new BooleanValue(
            "Enhanced Server List",
            "Enables/Disables the enhanced server list mode.",
            this,
            true
    ).valueChangedConsumer(value -> {
        if (!value) {
            Vandalism.getInstance().getServerListManager().setSelectedServerList(
                    ServerList.DEFAULT_SERVER_LIST_NAME
            );
        }
    });

    public final Value<GlfwKeyName> pasteServerKey = new KeyInputValue(
            "Paste Server Key",
            "Change the key to paste a server from your clipboard.",
            this,
            GlfwKeyName.INSERT
    ).visibleConsumer(this.enhancedServerList::getValue);

    public final Value<GlfwKeyName> copyServerKey = new KeyInputValue(
            "Copy Server Key",
            "Change the key to copy a server to your clipboard.",
            this,
            GlfwKeyName.PAGE_DOWN
    ).visibleConsumer(this.enhancedServerList::getValue);

    public final Value<GlfwKeyName> deleteServerKey = new KeyInputValue(
            "Delete Server Key",
            "Change the key to delete a server from the server list.",
            this,
            GlfwKeyName.DELETE
    ).visibleConsumer(this.enhancedServerList::getValue);

    public final Value<Boolean> multiplayerScreenServerInformation = new BooleanValue(
            "Multiplayer Screen Server Information",
            "If enabled the Game shows all necessary server information behind a server list entry.",
            this,
            true
    ).visibleConsumer(this.enhancedServerList::getValue);

    public final Value<Integer> maxServerVersionLength = new SliderIntegerValue(
            "Max Server Version Length",
            "Sets the max display length of a server version that is being displayed in the multiplayer screen.",
            this,
            60,
            6,
            250
    ).visibleConsumer(() -> this.enhancedServerList.getValue() && this.multiplayerScreenServerInformation.getValue());

}
