package de.vandalismdevelopment.vandalism.feature.script;

import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.awt.KeyBindValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Script extends Feature implements ValueParent {

    private final File file;
    private final String version, author;
    private final List<Value<?>> values;
    private final KeyBindValue keyBind;

    public Script(final File file, final String name, final String version, final String author, final String description, final FeatureCategory category, final boolean experimental) {
        this.setName(name);
        this.setDescription(description);
        this.setType(FeatureType.SCRIPT);
        this.setCategory(category);
        this.setExperimental(experimental);
        this.file = file;
        this.version = version;
        this.author = author;
        this.values = new ArrayList<>();
        this.keyBind = new KeyBindValue("Key Bind", "The key bind of this script.", this, GlfwKeyName.UNKNOWN);
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

    public void setKeyBind(final GlfwKeyName glfwKeyName) {
        this.keyBind.setValue(glfwKeyName);
    }

    public GlfwKeyName getKeyBind() {
        return this.keyBind.getValue();
    }

    @Override
    public String toString() {
        return '{' + "name=" + this.getName() + ", category=" + this.getCategory().normalName() + ", experimental=" + this.isExperimental() + ", file=" + this.file.getName() + ", version='" + this.version + '\'' + ", author='" + this.author + '\'' + ", keyBind=" + this.keyBind.getValue().normalName() + '}';
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public String getValueName() {
        return this.getName();
    }

}
