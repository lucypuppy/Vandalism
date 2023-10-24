package de.vandalismdevelopment.vandalism.feature.impl.script;

import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.FeatureType;

import java.io.File;

public class Script extends Feature {

    private final File file;
    private final String version, author;

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

    @Override
    public String toString() {
        return '{' +
                "name=" + this.getName() +
                ", category=" + this.getCategory() +
                ", experimental=" + this.isExperimental() +
                ", file=" + this.file.getName() +
                ", version='" + this.version + '\'' +
                ", author='" + this.author + '\'' +
                '}';
    }

}
