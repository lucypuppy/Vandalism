/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.script;

import de.florianmichael.rclasses.pattern.storage.named.NamedStorage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.event.normal.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.script.gui.ScriptsClientWindow;
import de.nekosarekawaii.vandalism.feature.script.parse.ScriptParser;
import de.nekosarekawaii.vandalism.feature.script.parse.command.ScriptCommand;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptManager extends NamedStorage<Script> implements PlayerUpdateListener, KeyboardInputListener, MinecraftWrapper {

    private final ConcurrentHashMap<UUID, Thread> runningScripts = new ConcurrentHashMap<>();
    private final File directory;
    private final ConfigManager configManager;

    public ScriptManager(final ConfigManager configManager, final ClientWindowManager clientWindowManager, final File runDirectory) {
        this.configManager = configManager;
        clientWindowManager.add(new ScriptsClientWindow());
        this.directory = new File(runDirectory, "scripts");
        Vandalism.getInstance().getEventSystem().subscribe(KeyboardInputEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void init() {
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
        this.configManager.add(new ConfigWithValues("scripts", getList()));
    }

    public void loadScriptFromFile(final File file, final boolean save) {
        try {
            if (!file.exists() || !file.isFile() || file.length() < 1 || !file.getName().endsWith(ScriptParser.SCRIPT_FILE_EXTENSION) || file.getName().contains(" ")) {
                return;
            }
            final Script script = ScriptParser.parseScriptObjectFromFile(file);
            final Script existingScript = this.getByName(script.getName(), true);
            if (existingScript != null) {
                script.getKeyBind().setValue(existingScript.getKeyBind().getValue());
                this.remove(existingScript);
            }
            this.add(script);
            if (save) {
                Vandalism.getInstance().getConfigManager().save();
            }
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to load script from file '" + file.getName() + "'", e);
        }
    }

    public File getDirectory() {
        return this.directory;
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        // Cancel if the key is unknown to prevent the script from being executed multiple times.
        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN) {
            return;
        }
        for (final Script script : this.getList()) {
            if (script.getKeyBind().isPressed(key)) {
                if (this.isScriptRunning(script.getUuid())) {
                    this.killRunningScript(script.getUuid());
                }
                this.executeScript(script.getUuid());
            }
        }
    }

    public int getRunningScriptsCount() {
        return this.runningScripts.size();
    }

    public boolean isScriptRunning(final UUID uuid) {
        return this.runningScripts.containsKey(uuid);
    }

    public void killAllRunningScripts() {
        for (final UUID uuid : this.runningScripts.keySet()) {
            this.killRunningScript(uuid);
        }
    }

    public void killRunningScript(final UUID uuid) {
        try {
            if (this.runningScripts.containsKey(uuid)) {
                final Thread thread = this.runningScripts.get(uuid);
                thread.interrupt();
                thread.stop();
                this.runningScripts.remove(uuid);
            }
        } catch (Exception exception) {
            Vandalism.getInstance().getLogger().error("Failed to kill running script.", exception);
        }
    }

    public void executeScript(final UUID uuid) {
        final boolean inGame = this.mc.player != null;
        try {
            if (this.isScriptRunning(uuid)) {
                throw new RuntimeException("Failed to execute script because it's already running");
            }
            final Script script = this.getList().stream().filter(s -> s.getUuid().equals(uuid)).findFirst().orElse(null);
            if (script == null) {
                throw new RuntimeException("Failed to execute script because the script doesn't exist");
            }
            final File file = script.getFile();
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
                } catch (Exception e) {
                    if (inGame) {
                        ChatUtil.errorChatMessage("Failed to execute script '" + scriptName + "' due to: " + e);
                    } else Vandalism.getInstance().getLogger().error("Failed to execute script", e);
                }
                this.runningScripts.remove(uuid);
            }, "script-execution-" + (getRunningScriptsCount() + 1) + "-" + scriptName);
            this.runningScripts.put(script.getUuid(), scriptThread);
            scriptThread.start();
        } catch (Exception e) {
            if (inGame) ChatUtil.errorChatMessage("Invalid script file: " + e);
            else Vandalism.getInstance().getLogger().error("Invalid script file", e);
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        for (final Script script : this.getList()) {
            if (!script.getFile().exists()) {
                this.remove(script);
                Vandalism.getInstance().getLogger().info("Script '" + script + "' has been unloaded because the file does not exist anymore.");
                Vandalism.getInstance().getConfigManager().save();
            }
        }
    }

}
