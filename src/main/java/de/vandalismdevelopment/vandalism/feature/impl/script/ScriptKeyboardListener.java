package de.vandalismdevelopment.vandalism.feature.impl.script;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.KeyboardListener;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ScriptKeyboardListener implements KeyboardListener {

    public ScriptKeyboardListener() {
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS ||
                MinecraftClient.getInstance().player == null ||
                MinecraftClient.getInstance().currentScreen != null
        ) return;
        for (final Script script : Vandalism.getInstance().getScriptRegistry().getScripts()) {
            if (script.getKeyCode() == key) {
                if (ScriptExecutor.isScriptRunning(script.getFile())) {
                    ScriptExecutor.killRunningScriptByScriptFile(script.getFile());
                }
                ScriptExecutor.executeScriptByScriptFile(script.getFile());
            }
        }
    }

}
