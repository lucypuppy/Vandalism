package me.nekosarekawaii.foxglove.config.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.Config;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.KeyInputValue;
import me.nekosarekawaii.foxglove.value.values.StringValue;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class MainConfig extends Config {

    public final Value<Integer> mainMenuKeyCode = new KeyInputValue(
            "Main Menu Key",
            "Change the key to open this Menu.",
            this,
            GLFW.GLFW_KEY_RIGHT_SHIFT
    );

    public final Value<String> commandPrefix = new StringValue(
            "Command Prefix",
            "Change the prefix to run the commands of the Mod.",
            this,
            "."
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
