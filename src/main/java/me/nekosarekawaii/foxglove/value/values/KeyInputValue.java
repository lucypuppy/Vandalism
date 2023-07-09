package me.nekosarekawaii.foxglove.value.values;

import com.google.gson.JsonObject;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.ImGui;
import it.unimi.dsi.fastutil.Pair;
import me.nekosarekawaii.foxglove.event.impl.KeyboardListener;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.Value;
import org.lwjgl.glfw.GLFW;

public class KeyInputValue extends Value<Pair<Integer, String>> implements KeyboardListener {

    private boolean listen = false;

    public KeyInputValue(final String name, final String description, final IValue parent, final Integer defaultKeyCodeValue, final String defaultKeyNameValue) {
        super(name, description, parent, new Pair<>() {
            @Override
            public Integer left() {
                return defaultKeyCodeValue;
            }

            @Override
            public String right() {
                return defaultKeyNameValue.toLowerCase();
            }
        });
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        final JsonObject pairContainer = valueObject.get("value").getAsJsonObject();
        this.setValue(new Pair<>() {
            @Override
            public Integer left() {
                return pairContainer.get("left").getAsInt();
            }

            @Override
            public String right() {
                return pairContainer.get("right").getAsString();
            }
        });
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        final JsonObject pairContainer = new JsonObject();
        pairContainer.addProperty("left", this.getValue().left());
        pairContainer.addProperty("right", this.getValue().right());
        valueObject.add("value", pairContainer);
    }

    @Override
    public void render() {
        if (!this.listen) {
            if (ImGui.button(this.getValue().right() + "##" + this.getName())) {
                this.listen = true;
                DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
            }
        } else {
            ImGui.text("Listening for key input...##" + this.getName());
            if (ImGui.button("...##" + this.getName())) {
                this.notListeningAnymore();
            }
            ImGui.sameLine();
            if (ImGui.button("Cancel##" + this.getName())) {
                this.notListeningAnymore();
            }
        }
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS) return;
        this.setValue(new Pair<>() {
            @Override
            public Integer left() {
                return key;
            }

            @Override
            public String right() {
                return GLFW.glfwGetKeyName(key, scanCode);
            }
        });
        this.notListeningAnymore();
    }

    private void notListeningAnymore() {
        if (!this.listen) return;
        DietrichEvents2.global().unsubscribe(KeyboardEvent.ID, this);
        this.listen = false;
    }

}
