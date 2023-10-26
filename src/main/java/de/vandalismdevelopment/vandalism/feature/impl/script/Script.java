package de.vandalismdevelopment.vandalism.feature.impl.script;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.Config;
import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.FeatureType;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.values.KeyInputValue;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Script extends Feature implements IValue {

    private final File file;
    private final String version, author;
    private final List<Value<?>> values;
    private final KeyInputValue keyCode;

    public Script(
            final File file,
            final String name,
            final String version,
            final String author,
            final String description,
            final FeatureCategory category,
            final boolean experimental
    ) {
        this.setName(name);
        this.setDescription(description);
        this.setType(FeatureType.SCRIPT);
        this.setCategory(category);
        this.setExperimental(experimental);
        this.file = file;
        this.version = version;
        this.author = author;
        this.values = new ArrayList<>();
        this.keyCode = new KeyInputValue("Keybind", "The keybind of this script.", this, GLFW.GLFW_KEY_UNKNOWN, "Unknown");
    }

    public File getFile() {
        return this.file;
    }

    public String getVersion() {
        return this.version;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getKeyCode() {
        return this.keyCode.getValue().getLeft();
    }

    public void setKeyCode(final int keyCode) {
        this.keyCode.setKeyCode(keyCode);
    }

    @Override
    public String toString() {
        return '{' +
                "name=" + this.getName() +
                ", category=" + this.getCategory() +
                ", experimental=" + this.isExperimental() +
                ", file=" + this.file.getName() +
                ", version='" + this.version + '\'' +
                ", author='" + this.author + '\'' +
                ", keyCode=" + this.keyCode.getValue().getRight() +
                '}';
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public Config getConfig() {
        return Vandalism.getInstance().getConfigManager().getScriptConfig();
    }

    @Override
    public String getValueName() {
        return this.getName();
    }

}
