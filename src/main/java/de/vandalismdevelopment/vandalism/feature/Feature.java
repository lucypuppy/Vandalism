package de.vandalismdevelopment.vandalism.feature;

import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

public abstract class Feature implements MinecraftWrapper {

    private VersionRange supportedVersions = null;

    private String name = "Example Feature", description = "This is a Feature.";
    private FeatureType type = FeatureType.NONE;
    private FeatureCategory category = FeatureCategory.MISC;
    private boolean experimental = false;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public FeatureType getType() {
        return this.type;
    }

    public void setType(final FeatureType type) {
        this.type = type;
    }

    public FeatureCategory getCategory() {
        return this.category;
    }

    public void setCategory(final FeatureCategory category) {
        this.category = category;
    }

    public void setExperimental(final boolean experimental) {
        this.experimental = experimental;
    }

    public boolean isExperimental() {
        return this.experimental;
    }

    public void setSupportedVersions(final VersionRange versionRange) {
        this.supportedVersions = versionRange;
    }

    public boolean isSupported(final VersionEnum version) {
        if (this.supportedVersions == null) return true;
        return this.supportedVersions.contains(version);
    }

}
