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
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderFloatValue;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class MainConfig extends ValueableConfig {

    public final ValueCategory menuCategory = new ValueCategory("Menu", "Menu Settings", this);

    public final Value<Pair<Integer, String>> menuBarKey = new KeyInputValue(
            "Menu Bar Key",
            "Change the key to open the Menu Bar.",
            this.menuCategory,
            GLFW.GLFW_KEY_MENU,
            "menu"
    );

    public final Value<Boolean> manageContainerButtons = new BooleanValue(
            "Manage Container Buttons",
            "Adds the store/steal buttons to containers.",
            this.menuCategory,
            true
    );

    public final Value<Boolean> multiplayerScreenServerInformation = new BooleanValue(
            "Multiplayer Screen Server Information",
            "If enabled the Game shows all necessary server information behind a server list entry.",
            this.menuCategory,
            true
    );

    public final Value<Integer> maxServerVersionLength = new SliderIntegerValue(
            "Max Server Version Length",
            "Sets the max display length of a server version that is being displayed in the multiplayer screen.",
            this.menuCategory,
            60,
            6,
            250
    ).visibleConsumer(this.multiplayerScreenServerInformation::getValue);

    public final Value<Boolean> resourcePackSpoof = new BooleanValue(
            "Resource Pack Spoof",
            "Allows you to spoof the server resource pack.",
            this.menuCategory,
            true
    );

    public final ValueCategory chatCategory = new ValueCategory("Chat", "Chat settings", this);

    public final Value<String> commandPrefix = new StringValue(
            "Command Prefix",
            "Change the prefix to run the commands of the Mod.",
            this.chatCategory,
            "."
    );

    public final Value<Boolean> allowColorChar = new BooleanValue(
            "Allow Color Char",
            "Disables the color char restrictions of the Game.",
            this.chatCategory,
            true
    );

    public final Value<Boolean> dontClearChatHistory = new BooleanValue(
            "Dont Clear Chat History",
            "Prevents the Game from clearing your chat history.",
            this.chatCategory,
            true
    );

    public final Value<Boolean> customChatLength = new BooleanValue(
            "Custom Chat Length",
            "Allows you to enable or disable a custom chat length.",
            this.chatCategory,
            true
    );

    public final Value<Integer> maxChatLength = new SliderIntegerValue(
            "Max Chat Length",
            "Set the Max Chat Length",
            this.chatCategory,
            1000,
            1,
            10000
    ).visibleConsumer(this.customChatLength::getValue);

    public final ValueCategory exploitCategory = new ValueCategory("Exploit", "Exploit settings", this);

    public final Value<Boolean> lecternCrasher = new BooleanValue(
            "Lectern Crasher",
            "Allows you to crash the Server with a Lectern.",
            this.exploitCategory,
            true
    );

    public final Value<Boolean> craftingDupe = new BooleanValue(
            "Crafting Dupe",
            "Allows you to dupe items.",
            this.exploitCategory,
            true
    );

    public final ValueCategory accessibilityCategory = new ValueCategory("Accessibility", "Accessibility settings", this);

    public final Value<Boolean> spoofIsCreativeLevelTwoOp = new BooleanValue(
            "Spoof Is Creative Level Two Op",
            "Makes the Game think you are a in Creative Mode and you have Level Two Op.",
            this.accessibilityCategory,
            true
    );

    public final Value<Boolean> antiTelemetry = new BooleanValue(
            "Anti Telemetry",
            "Blocks the Telemetry of the Game.",
            this.accessibilityCategory,
            true
    );

    public final Value<Boolean> antiServerBlockList = new BooleanValue(
            "Anti Server Block List",
            "Blocks the Server Block List from the Game.",
            this.accessibilityCategory,
            true
    );

    public final Value<Boolean> antiTimeoutKick = new BooleanValue(
            "Anti Timeout Kick",
            "Prevents the Game from disconnecting after 30 seconds if the server doesn't response.",
            this.accessibilityCategory,
            true
    );

    public final Value<Boolean> eliminateHitDelay = new BooleanValue(
            "Eliminate Hit Delay",
            "Eliminates the Hit Delay of the Game.",
            this.accessibilityCategory,
            false
    );

    public final ValueCategory visualsCategory = new ValueCategory("Visuals", "Visuals settings", this);

    public final Value<Float> fireOverlayOffset = new SliderFloatValue(
            "Fire Overlay Offset",
            "Change the Fire Overlay Offset",
            this.visualsCategory,
            0.0f,
            0.0f,
            0.4f,
            "%.2f"
    );

    public final Value<Boolean> waterOverlay = new BooleanValue(
            "Water Overlay",
            "Enable/Disable Water Overlay",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> inWallOverlay = new BooleanValue(
            "In Wall Overlay",
            "Enable/Disable In Wall Overlay",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> freezeOverlay = new BooleanValue(
            "Freeze Overlay",
            "Enable/Disable Freeze Overlay",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> pumpkinOverlay = new BooleanValue(
            "Pumpkin Overlay",
            "Enable/Disable Pumpkin Overlay",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> spyGlassOverlay = new BooleanValue(
            "Spyglass Overlay",
            "Enable/Disable Spyglass Overlay",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> nauseaOverlay = new BooleanValue(
            "Nausea Overlay",
            "Enable/Disable Nausea Overlay.",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> blindnessEffect = new BooleanValue(
            "Blindness Effect",
            "Enable/Disable Blindness Effect.",
            this.visualsCategory,
            true
    );
    public final Value<Boolean> hurtCam = new BooleanValue(
            "Hurt Cam",
            "Enable/Disable Hurt Cam",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> fullBright = new BooleanValue(
            "Full Bright",
            "Enable/Disable Full Bright",
            this.visualsCategory,
            false
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
