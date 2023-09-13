package de.nekosarekawaii.foxglove.config.impl;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.google.gson.JsonObject;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.config.ValueableConfig;
import de.nekosarekawaii.foxglove.util.MinecraftWrapper;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.ValueCategory;
import de.nekosarekawaii.foxglove.value.values.BooleanValue;
import de.nekosarekawaii.foxglove.value.values.KeyInputValue;
import de.nekosarekawaii.foxglove.value.values.ListValue;
import de.nekosarekawaii.foxglove.value.values.StringValue;
import de.nekosarekawaii.foxglove.value.values.number.slider.SliderFloatValue;
import de.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.text.Text;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class MainConfig extends ValueableConfig implements MinecraftWrapper {

    public MainConfig() {
        super(Foxglove.getInstance().getDir(), "main");
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {

                @Override
                public void nativeKeyPressed(final NativeKeyEvent nativeEvent) {
                    if (Foxglove.getInstance().getConfigManager().getMainConfig().forceDisconnectKeybind.getValue()) {
                        if (nativeEvent.getKeyCode() == 3663) { // END Key
                            if (networkHandler() != null) {
                                networkHandler().getConnection().disconnect(Text.literal("Manual force disconnect."));
                            }
                        }
                    }
                }

            });
            Foxglove.getInstance().getLogger().info("Successfully registered native input hook disconnect listener.");
        } catch (final NativeHookException e) {
            Foxglove.getInstance().getLogger().error("Failed to register native input hook disconnect listener.", e);
        }
    }

    private final ValueCategory menuCategory = new ValueCategory("Menu", "Menu settings", this);

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

    public final Value<Boolean> moreResourcePackOptions = new BooleanValue(
            "More Server Resource Pack Options",
            "Shows more options in the Server Resource Pack Download Screen.",
            this.menuCategory,
            true
    );

    private final ValueCategory chatCategory = new ValueCategory(
            "Chat",
            "Chat settings",
            this
    );

    public final Value<String> commandPrefix = new StringValue(
            "Command Prefix",
            "Change the prefix to run the commands of the Mod.",
            this.chatCategory,
            "."
    );

    public final Value<Boolean> displayTypedChars = new BooleanValue(
            "Display Typed Chars",
            "Displays the current char count of from the chat input field.",
            this.chatCategory,
            true
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

    private final ValueCategory exploitCategory = new ValueCategory(
            "Exploit",
            "Exploit settings",
            this
    );

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

    private final ValueCategory accessibilityCategory = new ValueCategory(
            "Accessibility",
            "Accessibility settings",
            this
    );

    public final Value<Boolean> forceDisconnectKeybind = new BooleanValue(
            "Force Disconnect Keybind",
            "Enables that you can disconnect with the key END even if the Game is frozen.",
            this.accessibilityCategory,
            true
    );

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

    private final ValueCategory visualsCategory = new ValueCategory(
            "Visuals",
            "Visuals settings",
            this
    );

    private final ValueCategory blockHitCategory = new ValueCategory(
            "BlockHit",
            "BlockHit settings (<=1.8.x)",
            this.visualsCategory
    );

    public final ListValue blockHitAnimations = new ListValue(
            "BlockHit Animations",
            "Change the BlockHit Animation.",
            this.blockHitCategory,
            "None",
            "Foxglove",
            "Suicide"
    );

    public final Value<Boolean> blockHitAnimation = new BooleanValue(
            "BlockHit Animation",
            "Enable/Disable BlockHit Animation.",
            this.blockHitCategory,
            true
    );

    public final Value<Float> blockItemSize = new SliderFloatValue(
            "Item Size",
            "Change the size of items.",
            this.blockHitCategory,
            1.0f,
            0.1f,
            2.0f,
            "%.2f"
    );

    public final Value<Boolean> customBobView = new BooleanValue(
            "Custom Bob View",
            "If enabled allows you to customize the bob view camera effect.",
            this.visualsCategory,
            false
    );

    public final Value<Float> customBobViewValue = new SliderFloatValue(
            "Custom Bob View Value",
            "Here you can change the custom bob view value.-",
            this.visualsCategory,
            5.0f,
            0.0f,
            50.0f,
            "%.2f"
    ).visibleConsumer(this.customBobView::getValue);

    public final static String SIGN_HIDE_SECRET = RandomStringUtils.randomAlphanumeric(4);

    public final Value<Boolean> hideSignText = new BooleanValue(
            "Hide Sign Text",
            "Hides text of signs when creating a new one.",
            this.visualsCategory,
            false
    );

    public final Value<Float> shieldAlpha = new SliderFloatValue(
            "Shield Alpha",
            "Change the alpha of a shield.",
            this.visualsCategory,
            1.0f,
            0.1f,
            1.0f,
            "%.2f"
    );

    public final Value<Float> fireOverlayOffset = new SliderFloatValue(
            "Fire Overlay Offset",
            "Change the Fire Overlay Offset.",
            this.visualsCategory,
            0.0f,
            0.0f,
            0.4f,
            "%.2f"
    );

    public final Value<Boolean> portalScreen = new BooleanValue(
            "Portal Screen",
            "If this option is enabled you are allowed to use screens in portals.",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> waterOverlay = new BooleanValue(
            "Water Overlay",
            "Enable/Disable Water Overlay.",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> inWallOverlay = new BooleanValue(
            "In Wall Overlay",
            "Enable/Disable In Wall Overlay.",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> freezeOverlay = new BooleanValue(
            "Freeze Overlay",
            "Enable/Disable Freeze Overlay.",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> pumpkinOverlay = new BooleanValue(
            "Pumpkin Overlay",
            "Enable/Disable Pumpkin Overlay.",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> spyGlassOverlay = new BooleanValue(
            "Spyglass Overlay",
            "Enable/Disable Spyglass Overlay.",
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
            "Enable/Disable Hurt Cam.",
            this.visualsCategory,
            true
    );

    public final Value<Boolean> fullBright = new BooleanValue(
            "Full Bright",
            "Enable/Disable Full Bright.",
            this.visualsCategory,
            false
    );

    public final ValueCategory movementCategory = new ValueCategory(
            "Movement",
            "Movement settings",
            this
    );

    public final Value<Boolean> customizeRiptideBoostMultiplier = new BooleanValue(
            "Customize Riptide Boost Multiplier",
            "If enabled shows you a slider to modify the riptide boost multiplier.",
            this.movementCategory,
            false
    );

    public final Value<Float> riptideBoostMultiplier = new SliderFloatValue(
            "Riptide Boost Multiplier",
            "Lets you modify the riptide boost multiplier.",
            this.movementCategory,
            1.0f,
            -5.0f,
            5.0f
    ).visibleConsumer(this.customizeRiptideBoostMultiplier::getValue);

    @Override
    public JsonObject save() throws IOException {
        final JsonObject configObject = new JsonObject(), valuesArray = new JsonObject();
        this.saveValues(valuesArray, this.getValues());
        configObject.add("values", valuesArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        if (jsonObject.has("values")) {
            this.loadValues(jsonObject.getAsJsonObject("values"), this.getValues());
        }
    }

}
