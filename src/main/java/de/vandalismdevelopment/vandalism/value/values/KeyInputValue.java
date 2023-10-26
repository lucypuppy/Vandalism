package de.vandalismdevelopment.vandalism.value.values;

import com.google.gson.JsonObject;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.KeyboardListener;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import it.unimi.dsi.fastutil.Pair;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeyInputValue extends Value<Pair<Integer, String>> implements KeyboardListener {

    private boolean listen = false;

    public KeyInputValue(final String name, final String description, final IValue parent, final Integer defaultKeyCodeValue, final String defaultKeyNameValue) {
        super(name, description, parent, "key input", new Pair<>() {
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
                return pairContainer.get("keyCode").getAsInt();
            }

            @Override
            public String right() {
                return pairContainer.get("displayName").getAsString();
            }
        });
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        final JsonObject pairContainer = new JsonObject();
        pairContainer.addProperty("keyCode", this.getValue().left());
        pairContainer.addProperty("displayName", this.getValue().right());
        valueObject.add("value", pairContainer);
    }

    @Override
    public void render() {
        if (!this.listen) {
            if (ImGui.button(this.getValue().right() + "##" + this.getName() + this.getSaveIdentifier())) {
                this.listen = true;
                DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
            }
            ImGui.sameLine();
            ImGui.textWrapped(this.getName());
        } else {
            ImGui.text("Listening for key input...");
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

    private final static List<Integer> KEYPAD_KEY_CODES = new ArrayList<>();

    static {
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_0);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_1);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_2);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_3);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_4);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_5);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_6);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_7);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_8);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_9);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_DECIMAL);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_DIVIDE);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_MULTIPLY);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_SUBTRACT);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_ADD);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_ENTER);
        KEYPAD_KEY_CODES.add(GLFW.GLFW_KEY_KP_EQUAL);
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS) return;
        String tempKeyName = GLFW.glfwGetKeyName(key, scanCode);
        if (tempKeyName != null) {
            final String keyName;
            if (KEYPAD_KEY_CODES.contains(key)) keyName = "kp " + tempKeyName;
            else keyName = tempKeyName;
            this.notListeningAnymore();
            this.setValue(new Pair<>() {
                @Override
                public Integer left() {
                    return key;
                }

                @Override
                public String right() {
                    return keyName;
                }
            });
        }
    }

    private void notListeningAnymore() {
        if (!this.listen) return;
        DietrichEvents2.global().unsubscribe(KeyboardEvent.ID, this);
        this.listen = false;
    }

}
