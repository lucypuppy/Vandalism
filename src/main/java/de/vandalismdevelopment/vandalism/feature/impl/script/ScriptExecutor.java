package de.vandalismdevelopment.vandalism.feature.impl.script;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptExecutor {

    private final static ConcurrentHashMap<File, Thread> RUNNING_SCRIPTS = new ConcurrentHashMap<>();

    public static int getRunningScriptsCount() {
        return RUNNING_SCRIPTS.size();
    }

    public static boolean isScriptRunning(final File file) {
        return RUNNING_SCRIPTS.containsKey(file);
    }

    public static void killAllRunningScripts() {
        for (final File file : RUNNING_SCRIPTS.keySet()) {
            killRunningScriptByScriptFile(file);
        }
    }

    public static void killRunningScriptByScriptFile(final File file) {
        try {
            if (RUNNING_SCRIPTS.containsKey(file)) {
                final Thread thread = RUNNING_SCRIPTS.get(file);
                if (thread.isAlive()) thread.interrupt();
                RUNNING_SCRIPTS.remove(file);
            }
        } catch (final Exception exception) {
            Vandalism.getInstance().getLogger().error("Failed to kill running script: " + file.getName(), exception);
        }
    }

    public static void executeScriptByScriptFile(final File file) {
        final boolean inGame = MinecraftClient.getInstance().player != null;
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
                    final boolean executionLogging = Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.scriptExecutionLogging.getValue();
                    if (executionLogging) {
                        final String executingMessage = "Executing script " + scriptName + " ...";
                        if (inGame) ChatUtils.infoChatMessage(executingMessage);
                        else Vandalism.getInstance().getLogger().info(executingMessage);
                    }
                    final List<Pair<ScriptCommand, Pair<Integer, String>>> code = ScriptParser.parseCodeFromScriptFile(file);
                    if (code.isEmpty())
                        throw new RuntimeException("The code of the script " + scriptName + " is empty");
                    for (int i = 0; i < code.size(); i++) {
                        final Pair<ScriptCommand, Pair<Integer, String>> entry = code.get(i);
                        if (entry == null) {
                            throw new RuntimeException("The code of the script " + scriptName + " " +
                                    " contains an invalid entry at line: " + (i + 1)
                            );
                        }
                        final Pair<Integer, String> line = entry.getRight();
                        if (line == null) {
                            throw new RuntimeException("The code of the script " + scriptName +
                                    " contains an invalid line at: " + (i + 1)
                            );
                        }
                        entry.getLeft().execute(scriptName, line.getLeft(), line.getRight());
                    }
                    if (executionLogging) {
                        Thread.sleep(100);
                        final String executedMessage = "Executed script " + scriptName + ".";
                        if (inGame) ChatUtils.infoChatMessage(executedMessage);
                        else Vandalism.getInstance().getLogger().info(executedMessage);
                    }
                } catch (final Exception e) {
                    if (inGame)
                        ChatUtils.errorChatMessage("Failed to execute script '" + scriptName + "' due to: " + e);
                    else Vandalism.getInstance().getLogger().error("Failed to execute script", e);
                }
            }, "script-execution-" + (getRunningScriptsCount() + 1) + "-" + scriptName);
            RUNNING_SCRIPTS.put(file, scriptThread);
            scriptThread.start();
        } catch (final Exception e) {
            if (inGame) ChatUtils.errorChatMessage("Invalid script file: " + e);
            else Vandalism.getInstance().getLogger().error("Invalid script file", e);
        }
    }

}
