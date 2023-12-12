package de.vandalismdevelopment.vandalism.feature.script;

import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.awt.KeyBindValue;
import net.raphimc.vialoader.util.VersionRange;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Script extends Feature implements ValueParent {

    private final List<Value<?>> values = new ArrayList<>();

    private final File file;
    private final String version;
    private final String author;
    private final KeyBindValue keyBind;

    public Script(String name, String description, Category category, File file, String version, String author) {
        super(name, description, category);
        this.file = file;
        this.version = version;
        this.author = author;

        this.keyBind = new KeyBindValue(this, "Key Bind", "The key bind of this script.");
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

    public KeyBindValue getKeyBind() {
        return keyBind;
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

}
