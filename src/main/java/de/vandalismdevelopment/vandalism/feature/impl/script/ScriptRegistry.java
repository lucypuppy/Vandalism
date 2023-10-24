package de.vandalismdevelopment.vandalism.feature.impl.script;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import net.minecraft.util.Pair;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptRegistry {

    private final File directory;

    private final FeatureList<Script> scripts;

    private final ExecutorService scriptExecutorService;

    public ScriptRegistry(final File dir) {
        this.directory = new File(dir, "scripts");
        this.scripts = new FeatureList<>();
        this.scriptExecutorService = Executors.newFixedThreadPool(4);
        this.load();
    }

    public void load() {
        if (!this.scripts.isEmpty()) this.scripts.clear();
        if (this.directory.exists() && !this.directory.isDirectory()) {
            this.directory.delete();
        }
        if (!this.directory.exists()) {
            if (!this.directory.mkdirs()) {
                Vandalism.getInstance().getLogger().error("Failed to create scripts directory!");
                return;
            }
            Vandalism.getInstance().getLogger().info("No scripts loaded!");
            return;
        }
        final File[] files = this.directory.listFiles();
        if (files == null) return;
        for (final File file : files) this.loadScriptFromFile(file);
        final int scriptListSize = this.scripts.size();
        if (scriptListSize < 1) Vandalism.getInstance().getLogger().info("No scripts loaded!");
        else Vandalism.getInstance().getLogger().info("Loaded " + scriptListSize + " script/s.");
    }

    public void loadScriptFromFile(final File file) {
        try {
            final Script script = ScriptParser.parseScriptInfoFromFile(file);
            if (script == null) return;
            if (!this.scripts.contains(script)) {
                this.scripts.add(script);
                Vandalism.getInstance().getLogger().info("Script '" + script + "' has been loaded.");
            } else {
                Vandalism.getInstance().getLogger().error("Duplicated script found: " + script);
            }
        } catch (final Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to load script from file '" + file.getName() + "'!", e);
        }
    }

    public void executeScript(final Script script) {
        this.scriptExecutorService.submit(() -> {
            try {
                Vandalism.getInstance().getLogger().info("Executing script " + script.getName() + " v" + script.getVersion() + "...");
                final List<Pair<ScriptCommand, Pair<Integer, String>>> code = ScriptParser.parseCodeFromScriptFile(script);
                if (code == null || code.isEmpty()) {
                    throw new RuntimeException("The code of the script " + script.getName() + " v" + script.getVersion() + " is empty!");
                }
                for (int i = 0; i < code.size(); i++) {
                    final Pair<ScriptCommand, Pair<Integer, String>> entry = code.get(i);
                    if (entry == null) {
                        throw new RuntimeException("The code of the script " + script.getName() + " v" +
                                script.getVersion() + " contains a invalid entry at line " + (i + 1) + "."
                        );
                    }
                    final Pair<Integer, String> line = entry.getRight();
                    if (line == null) {
                        throw new RuntimeException("The code of the script " + script.getName() + " v" +
                                script.getVersion() + " contains a invalid line at " + (i + 1) + "."
                        );
                    }
                    entry.getLeft().execute(script, line.getLeft(), line.getRight());
                }
                Vandalism.getInstance().getLogger().info("Executed script " + script.getName() + " v" + script.getVersion() + ".");
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to execute script " + script.getName() + " v" + script.getVersion() + "!", e);
            }
        });
    }

    public File getDirectory() {
        return this.directory;
    }

    public FeatureList<Script> getScripts() {
        return this.scripts;
    }

}
