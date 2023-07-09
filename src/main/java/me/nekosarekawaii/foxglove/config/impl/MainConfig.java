package me.nekosarekawaii.foxglove.config.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Pair;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.ValueableConfig;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.KeyInputValue;
import me.nekosarekawaii.foxglove.value.values.StringValue;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class MainConfig extends ValueableConfig {

    public final Value<Pair<Integer, String>> menuBarKey = new KeyInputValue(
            "Menu Bar Key",
            "Change the key to open the Menu Bar.",
            this,
            GLFW.GLFW_KEY_MENU,
            "menu"
    );

    public final Value<String> commandPrefix = new StringValue(
            "Command Prefix",
            "Change the prefix to run the commands of the Mod.",
            this,
            "."
    );

    public final Value<Boolean> multiplayerScreenServerInformation = new BooleanValue(
            "Multiplayer Screen Server Information",
            "If enabled the Game shows all necessary server information behind a server list entry.",
            this,
            true
    );

    public final Value<Integer> maxServerVersionLength = new SliderIntegerValue(
            "Max Server Version Length",
            "Sets the max display length of a server version that is being displayed in the multiplayer screen.",
            this,
            60,
            6,
            250
    ).visibleConsumer(this.multiplayerScreenServerInformation::getValue);

    public final Value<Boolean> allowColorChar = new BooleanValue(
            "Allow Color Char",
            "Disables the color char restrictions of the Game.",
            this,
            true
    );

    public MainConfig() {
        super(Foxglove.getInstance().getDir(), "main");
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject configObject = new JsonObject();
        final JsonArray valuesArray = new JsonArray();

        for (final Value<?> value : this.getValues()) {
            final JsonObject valueObject = new JsonObject();

            if (value != null) {
                valueObject.addProperty("name", value.getHashIdent());

                value.onConfigSave(valueObject);

                valuesArray.add(valueObject);
            }
        }

        configObject.add("values", valuesArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        if (jsonObject.has("values")) {
            final JsonArray valuesArray = jsonObject.getAsJsonArray("values");

            for (final JsonElement valueElement : valuesArray) {
                final JsonObject valueObject = valueElement.getAsJsonObject();
                final String valueName = valueObject.get("name").getAsString();
                final Value<?> value = this.getValue(valueName);

                if (value == null) {
                    Foxglove.getInstance().getLogger().error("Couldn't find Main Config value: " + valueName);
                    continue;
                }

                value.onConfigLoad(valueObject);
            }
        }
    }

}
