package de.nekosarekawaii.vandalism.gui.config;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.config.AbstractConfig;
import de.nekosarekawaii.vandalism.gui.ClientMenuManager;
import de.nekosarekawaii.vandalism.gui.base.ClientMenuWindow;

public class ClientMenuConfig extends AbstractConfig<JsonObject> {

    private final ClientMenuManager clientMenuManager;

    public ClientMenuConfig(final ClientMenuManager clientMenuManager) {
        super(JsonObject.class, "client-menu");

        this.clientMenuManager = clientMenuManager;
    }

    @Override
    public JsonObject save0() {
        final var mainNode = new JsonObject();
        for (ClientMenuWindow window : this.clientMenuManager.getList()) {
            mainNode.addProperty(window.getName(), window.isActive());
        }

        return mainNode;
    }

    @Override
    public void load0(JsonObject mainNode) {
        for (ClientMenuWindow window : this.clientMenuManager.getList()) {
            if (mainNode.has(window.getName())) {
                window.setActive(mainNode.get(window.getName()).getAsBoolean());
            }
        }
    }

}
