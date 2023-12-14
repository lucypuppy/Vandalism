package de.nekosarekawaii.vandalism.gui.config.impl;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.config.AbstractConfig;
import de.nekosarekawaii.vandalism.gui.ImGuiManager;
import de.nekosarekawaii.vandalism.gui.base.ImWindow;

public class ImWindowConfig extends AbstractConfig<JsonObject> {

    private final ImGuiManager imGuiManager;

    public ImWindowConfig(final ImGuiManager imGuiManager) {
        super(JsonObject.class, "imwindow");

        this.imGuiManager = imGuiManager;
    }

    @Override
    public JsonObject save0() {
        final var mainNode = new JsonObject();
        for (ImWindow window : this.imGuiManager.getList()) {
            mainNode.addProperty(window.getName(), window.isActive());
        }

        return mainNode;
    }

    @Override
    public void load0(JsonObject mainNode) {
        for (ImWindow window : this.imGuiManager.getList()) {
            if (mainNode.has(window.getName())) {
                window.setActive(mainNode.get(window.getName()).getAsBoolean());
            }
        }
    }

}
