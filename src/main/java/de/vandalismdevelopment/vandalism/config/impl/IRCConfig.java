package de.vandalismdevelopment.vandalism.config.impl;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.impl.irc.IrcImGuiMenu;
import imgui.type.ImString;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class IRCConfig extends ValueableConfig {

    private final HashMap<String, ImString> values = new HashMap<>();

    public IRCConfig(final File configDir) {
        super(configDir, "irc");

        this.values.put("address", IrcImGuiMenu.ADDRESS);
        this.values.put("username", IrcImGuiMenu.USERNAME);
        this.values.put("password", IrcImGuiMenu.PASSWORD);
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject object = new JsonObject();
        this.values.forEach((key, value) -> object.addProperty(key, value.get()));
        return object;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        this.values.forEach((key, value) -> {
            if (jsonObject.has(key))
                value.set(jsonObject.get(key).getAsString());
        });
    }
}
