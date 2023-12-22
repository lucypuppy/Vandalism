package de.nekosarekawaii.vandalism.feature.script;

import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.awt.KeyBindValue;
import de.nekosarekawaii.vandalism.feature.Feature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Script extends Feature implements ValueParent {

    private final List<Value<?>> values = new ArrayList<>();

    private final UUID uuid;
    private final File file;
    private final String version;
    private final String author;
    private final KeyBindValue keyBind;

    public Script(String name, String description, Category category, File file, String version, String author) {
        super(name, description, category);
        this.uuid = UUID.randomUUID();
        this.file = file;
        this.version = version;
        this.author = author;
        this.keyBind = new KeyBindValue(this, "Key Bind", "The key bind of this script.");
    }

    public UUID getUuid() {
        return this.uuid;
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
        return this.keyBind;
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

}
