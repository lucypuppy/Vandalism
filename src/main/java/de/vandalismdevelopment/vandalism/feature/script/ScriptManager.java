package de.vandalismdevelopment.vandalism.feature.script;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.InputListener;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.feature.script.parse.ScriptParser;
import de.vandalismdevelopment.vandalism.feature.script.parse.command.ScriptCommand;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptManager implements TickListener, InputListener, MinecraftWrapper {

    private final File directory;
    private final FeatureList<Script> scripts;
    private final ConcurrentHashMap<File, Thread> runningScripts;

    public ScriptManager(final File dir) {
        this.directory = new File(dir, "scripts");
        this.scripts = new FeatureList<>();
        this.load();
        this.runningScripts = new ConcurrentHashMap<>();
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    public void load() {
        Vandalism.getInstance().getLogger().info("Loading scripts...");
        if (this.directory.exists() && !this.directory.isDirectory()) {
            if (!this.directory.delete()) {
                Vandalism.getInstance().getLogger().error("Failed to delete invalid scripts directory!");
            }
        } else {
            if (!this.directory.exists()) {
                if (!this.directory.mkdirs()) {
                    Vandalism.getInstance().getLogger().error("Failed to create scripts directory!");
                }
            } else {
                final File[] files = this.directory.listFiles();
                if (files != null) {
                    for (final File file : files) {
                        this.loadScriptFromFile(file, false);
                    }
                }
            }
        }
        final int scriptListSize = this.scripts.size();
        if (scriptListSize < 1) Vandalism.getInstance().getLogger().info("No scripts loaded!");
        else Vandalism.getInstance().getLogger().info("Loaded " + scriptListSize + " scripts.");
    }

    public void loadScriptFromFile(final File file, final boolean save) {
        try {
            if (!file.exists() || !file.isFile() || file.length() < 1 || !file.getName().endsWith(ScriptParser.SCRIPT_FILE_EXTENSION) || file.getName().contains(" ")) {
                return;
            }
            final Script script = ScriptParser.parseScriptObjectFromFile(file);
            final Script existingScript = this.scripts.get(script.getName());
            if (existingScript != null) {
                script.setKeyBind(existingScript.getKeyBind());
                this.scripts.remove(existingScript);
            }
            this.scripts.add(script);
            if (save) {
                Vandalism.getInstance().getConfigManager().save(); // TODO |Only save this config
            }
            Vandalism.getInstance().getLogger().info("Script '" + script + "' has been loaded.");
        } catch (final Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to load script from file '" + file.getName() + "'", e);
        }
    }

    public File getDirectory() {
        return this.directory;
    }

    public FeatureList<Script> getScripts() {
        return this.scripts;
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN || this.mc.player == null || this.mc.currentScreen != null) {
            return;
        }
        for (final Script script : Vandalism.getInstance().getScriptRegistry().getScripts()) {
            if (script.getKeyBind().getKeyCode() == key) {
                if (this.isScriptRunning(script.getFile())) {
                    this.killRunningScriptByScriptFile(script.getFile());
                }
                this.executeScriptByScriptFile(script.getFile());
            }
        }
    }

    public int getRunningScriptsCount() {
        return this.runningScripts.size();
    }

    public boolean isScriptRunning(final File file) {
        return this.runningScripts.containsKey(file);
    }

    public void killAllRunningScripts() {
        for (final File file : this.runningScripts.keySet()) {
            this.killRunningScriptByScriptFile(file);
        }
    }

    public void killRunningScriptByScriptFile(final File file) {
        try {
            if (this.runningScripts.containsKey(file)) {
                final Thread thread = this.runningScripts.get(file);
                if (thread.isAlive()) thread.interrupt();
                this.runningScripts.remove(file);
            }
        } catch (final Exception exception) {
            Vandalism.getInstance().getLogger().error("Failed to kill running script: " + file.getName(), exception);
        }
    }

    public void executeScriptByScriptFile(final File file) {
        final boolean inGame = this.mc.player != null;
        try {
            if (isScriptRunning(file)) {
                throw new RuntimeException("Failed to execute script '" + file.getName() + "' because it is already running");
            }
            if (file == null || !file.exists() || !file.isFile() || file.length() < 1) {
                throw new RuntimeException("Failed to parse script code from script file because the file is null");
            }
            final String scriptName = file.getName().replaceFirst(ScriptParser.SCRIPT_FILE_EXTENSION, "");
            final Thread scriptThread = new Thread(() -> {
                try {
                    final boolean executionLogging = Vandalism.getInstance().getClientSettings().getMenuSettings().scriptExecutionLogging.getValue();
                    if (executionLogging) {
                        final String executingMessage = "Executing script " + scriptName + " ...";
                        if (inGame) ChatUtil.infoChatMessage(executingMessage);
                        else Vandalism.getInstance().getLogger().info(executingMessage);
                    }
                    final List<Pair<ScriptCommand, Pair<Integer, String>>> code = ScriptParser.parseCodeFromScriptFile(file);
                    if (code.isEmpty())
                        throw new RuntimeException("The code of the script " + scriptName + " is empty");
                    for (int i = 0; i < code.size(); i++) {
                        final Pair<ScriptCommand, Pair<Integer, String>> entry = code.get(i);
                        if (entry == null) {
                            throw new RuntimeException("The code of the script " + scriptName + " " + " contains an invalid entry at line: " + (i + 1));
                        }
                        final Pair<Integer, String> line = entry.getRight();
                        if (line == null) {
                            throw new RuntimeException("The code of the script " + scriptName + " contains an invalid line at: " + (i + 1));
                        }
                        entry.getLeft().execute(scriptName, line.getLeft(), line.getRight());
                    }
                    if (executionLogging) {
                        Thread.sleep(100);
                        final String executedMessage = "Executed script " + scriptName + ".";
                        if (inGame) ChatUtil.infoChatMessage(executedMessage);
                        else Vandalism.getInstance().getLogger().info(executedMessage);
                    }
                } catch (final Exception e) {
                    if (inGame) {
                        ChatUtil.errorChatMessage("Failed to execute script '" + scriptName + "' due to: " + e);
                    } else Vandalism.getInstance().getLogger().error("Failed to execute script", e);
                }
            }, "script-execution-" + (getRunningScriptsCount() + 1) + "-" + scriptName);
            this.runningScripts.put(file, scriptThread);
            scriptThread.start();
        } catch (final Exception e) {
            if (inGame) ChatUtil.errorChatMessage("Invalid script file: " + e);
            else Vandalism.getInstance().getLogger().error("Invalid script file", e);
        }
    }

    @Override
    public void onTick() {
        for (final Script script : this.scripts) {
            if (!script.getFile().exists()) {
                this.scripts.remove(script);
                Vandalism.getInstance().getLogger().info("Script '" + script + "' has been unloaded because the file does not exist anymore.");
                Vandalism.getInstance().getConfigManager().save(); // TODO |Only save this config
            }
        }
    }

}
