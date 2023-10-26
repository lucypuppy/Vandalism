package de.vandalismdevelopment.vandalism.feature.impl.script;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureList;

import java.io.File;

public class ScriptRegistry {

    private final File directory;

    private final FeatureList<Script> scripts;

    private final ScriptKeyboardListener scriptKeyboardListener;

    public ScriptRegistry(final File dir) {
        this.directory = new File(dir, "scripts");
        this.scripts = new FeatureList<>();
        this.load();
        this.scriptKeyboardListener = new ScriptKeyboardListener();
    }

    public void load() {
        if (!this.scripts.isEmpty()) this.scripts.clear();
        if (this.directory.exists() && !this.directory.isDirectory()) {
            if (!this.directory.delete()) {
                Vandalism.getInstance().getLogger().error("Failed to delete invalid scripts directory!");
                return;
            }
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
            if (!file.exists() || !file.isFile() || file.length() < 1 || !file.getName().endsWith(ScriptParser.SCRIPT_FILE_EXTENSION) || file.getName().contains(" ")) {
                return;
            }
            final Script script = ScriptParser.parseScriptObjectFromFile(file);
            final Script existingScript = this.scripts.get(script.getName());
            if (existingScript != null) this.scripts.remove(existingScript);
            this.scripts.add(script);
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

}
