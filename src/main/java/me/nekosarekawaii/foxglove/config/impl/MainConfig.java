package me.nekosarekawaii.foxglove.config.impl;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Pair;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.ValueableConfig;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.ValueCategory;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.KeyInputValue;
import me.nekosarekawaii.foxglove.value.values.StringValue;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class MainConfig extends ValueableConfig {

    public final ValueCategory menuCategory = new ValueCategory("Menu", "Menu Settings", this);

    public final Value<Pair<Integer, String>> menuBarKey = new KeyInputValue(
            "Menu Bar Key",
            "Change the key to open the Menu Bar.",
            menuCategory,
            GLFW.GLFW_KEY_MENU,
            "menu"
    );

    public final Value<Boolean> manageContainerButtons = new BooleanValue(
            "Manage Container Buttons",
            "Adds the store/steal buttons to containers.",
            menuCategory,
            true
    );

    public final Value<Boolean> multiplayerScreenServerInformation = new BooleanValue(
            "Multiplayer Screen Server Information",
            "If enabled the Game shows all necessary server information behind a server list entry.",
            menuCategory,
            true
    );

    public final Value<Integer> maxServerVersionLength = new SliderIntegerValue(
            "Max Server Version Length",
            "Sets the max display length of a server version that is being displayed in the multiplayer screen.",
            menuCategory,
            60,
            6,
            250
    ).visibleConsumer(this.multiplayerScreenServerInformation::getValue);

    public final ValueCategory chatCategory = new ValueCategory("Chat", "Chat settings", this);

    public final Value<String> commandPrefix = new StringValue(
            "Command Prefix",
            "Change the prefix to run the commands of the Mod.",
            chatCategory,
            "."
    );

    public final Value<Boolean> allowColorChar = new BooleanValue(
            "Allow Color Char",
            "Disables the color char restrictions of the Game.",
            chatCategory,
            true
    );

    public final Value<Boolean> dontClearChatHistory = new BooleanValue(
            "Dont Clear Chat History",
            "Prevents the Game from clearing your chat history.",
            chatCategory,
            true
    );

    public final Value<Boolean> customChatLength = new BooleanValue(
            "Custom Chat Length",
            "Allows you to enable or disable a custom chat length.",
            chatCategory,
            true
    );

    public final Value<Integer> maxChatLength = new SliderIntegerValue(
            "Max Chat Length",
            "Set the Max Chat Length",
            chatCategory,
            1000,
            1,
            10000
    ).visibleConsumer(this.customChatLength::getValue);

    public final ValueCategory accessibilityCategory = new ValueCategory("Accessibility", "Accessibility settings", this);

    public final Value<Boolean> antiTelemetry = new BooleanValue(
            "Anti Telemetry",
            "Blocks the Telemetry of the Game.",
            accessibilityCategory,
            true
    );

    public final Value<Boolean> antiServerBlockList = new BooleanValue(
            "Anti Server Block List",
            "Blocks the Server Block List from the Game.",
            accessibilityCategory,
            true
    );

    public final Value<Boolean> antiTimeoutKick = new BooleanValue(
            "Anti Timeout Kick",
            "Prevents the Game from disconnecting after 30 seconds if the server doesn't response.",
            accessibilityCategory,
            true
    );

    public MainConfig() {
        super(Foxglove.getInstance().getDir(), "main");
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject configObject = new JsonObject();
        final JsonObject valuesArray = new JsonObject();
        saveValues(valuesArray, this.getValues());
        configObject.add("values", valuesArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        if (jsonObject.has("values")) {
            loadValues(jsonObject.getAsJsonObject("values"), this.getValues());
        }
    }

}
