package de.vandalismdevelopment.vandalism.gui.base;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.config.AbstractConfig;
import de.vandalismdevelopment.vandalism.gui.ImGuiManager;

public class ImGuiConfig extends AbstractConfig<JsonObject> {

    private final ImGuiManager imGuiManager;

    public ImGuiConfig(final ImGuiManager imGuiManager) {
        super(JsonObject.class, "imgui");

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
