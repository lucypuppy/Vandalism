package de.vandalismdevelopment.vandalism.base.config.impl;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.config.AbstractConfig;
import de.vandalismdevelopment.vandalism.gui.impl.irc.IrcImGuiMenu;
import imgui.type.ImString;

import java.util.HashMap;

public class IRCConfig extends AbstractConfig<JsonObject> {

    private final HashMap<String, ImString> values = new HashMap<>();

    public IRCConfig() {
        super(JsonObject.class, "irc");

        this.values.put("address", IrcImGuiMenu.ADDRESS);
        this.values.put("username", IrcImGuiMenu.USERNAME);
        this.values.put("password", IrcImGuiMenu.PASSWORD);
    }

    @Override
    public JsonObject save0() {
        final var mainNode = new JsonObject();
        this.values.forEach((key, value) -> {
            mainNode.addProperty(key, value.get());
        });

        return mainNode;
    }

    @Override
    public void load0(final JsonObject jsonObject) {
        this.values.forEach((key, value) -> {
            if (jsonObject.has(key)) {
                value.set(jsonObject.get(key).getAsString());
            }
        });
    }

}
