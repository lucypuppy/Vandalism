package de.vandalismdevelopment.vandalism.base.value.impl.awt;

import com.google.gson.JsonObject;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.InputListener;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.util.render.InputType;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public class KeyBindValue extends Value<Integer> implements InputListener {

    public KeyBindValue(ValueParent parent, String name, String description) {
        this(parent, name, description, GLFW.GLFW_KEY_UNKNOWN);
    }

    public KeyBindValue(ValueParent parent, String name, String description, Integer defaultValue) {
        super(parent, name, description, defaultValue);
    }

    @Override
    public void load(final JsonObject mainNode) {
        this.setValue(mainNode.get(getName()).getAsInt());
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(getName(), getValue());
    }

    private boolean waitingForInput;

    @Override
    public void render() {
        if (!this.waitingForInput) {
            if (ImGui.button(InputType.getKeyName(this.getValue()) + "##" + this.getName(), 0, 25)) {
                this.waitingForInput = true;
                DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
            }
        } else {
            ImGui.textWrapped("Listening for key input...");
            if (ImGui.button("Cancel##" + this.getName())) {
                this.finishInput();
            }
            ImGui.sameLine();
            if (ImGui.button("Reset##" + this.getName())) {
                this.finishInput();
                this.resetValue();
            }
        }
    }

    private void finishInput() {
        this.waitingForInput = false;
        DietrichEvents2.global().unsubscribe(KeyboardEvent.ID, this);
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (key != GLFW.GLFW_KEY_UNKNOWN && action == GLFW.GLFW_PRESS) {
            this.finishInput();

            this.setValue(key);
        }
    }

    public boolean isPressed() {
        return InputType.isPressed(this.getValue());
    }

    public boolean isValid() {
        return this.getValue() != GLFW.GLFW_KEY_UNKNOWN;
    }

}

