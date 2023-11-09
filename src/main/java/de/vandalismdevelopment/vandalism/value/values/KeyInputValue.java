package de.vandalismdevelopment.vandalism.value.values;

import com.google.gson.JsonObject;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.KeyboardListener;
import de.vandalismdevelopment.vandalism.util.GlfwKeyName;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public class KeyInputValue extends Value<GlfwKeyName> implements KeyboardListener {

    private boolean listen;

    public KeyInputValue(final String name, final String description, final IValue parent, final GlfwKeyName defaultValue) {
        super(name, description, parent, "key input", defaultValue);
        this.listen = false;
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        final JsonObject pairContainer = valueObject.get("value").getAsJsonObject();
        this.setValue(GlfwKeyName.getGlfwKeyNameByKeyCode(pairContainer.get("keyCode").getAsInt()));
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        final JsonObject pairContainer = new JsonObject();
        pairContainer.addProperty("keyCode", this.getValue().getKeyCode());
        valueObject.add("value", pairContainer);
    }

    @Override
    public void render() {
        if (!this.listen) {
            ImGui.text(this.getName());
            if (ImGui.button(this.getValue().normalName() + "##" + this.getSaveIdentifier(), 0, 25)) {
                this.listen = true;
                DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
            }
        } else {
            ImGui.textWrapped("Listening for key input...");
            if (ImGui.button("Cancel##" + this.getName() + this.getSaveIdentifier())) {
                this.notListeningAnymore();
            }
            ImGui.sameLine();
            if (ImGui.button("Reset##" + this.getName() + this.getSaveIdentifier())) {
                this.notListeningAnymore();
                this.resetValue();
            }
        }
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action == GLFW.GLFW_PRESS) this.setValueByKeyCode(key);
    }

    private void notListeningAnymore() {
        if (!this.listen) return;
        DietrichEvents2.global().unsubscribe(KeyboardEvent.ID, this);
        this.listen = false;
    }

    public void setValueByKeyCode(final int key) {
        final GlfwKeyName keyName = GlfwKeyName.getGlfwKeyNameByKeyCode(key);
        if (keyName.equals(GlfwKeyName.UNKNOWN)) return;
        this.notListeningAnymore();
        this.setValue(keyName);
    }

}
