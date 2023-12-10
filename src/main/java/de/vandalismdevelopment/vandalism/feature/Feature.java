package de.vandalismdevelopment.vandalism.feature;

import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

public abstract class Feature implements MinecraftWrapper {

    private String name;
    private String description;
    private FeatureType type;
    private FeatureCategory category;
    private boolean experimental;
    private VersionRange supportedVersions;

    protected Feature() {
        this.name = "Example Feature";
        this.description = "This is a Feature.";
        this.type = FeatureType.NONE;
        this.category = FeatureCategory.MISC;
        this.experimental = false;
        this.supportedVersions = null;
    }

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

    public boolean isExperimental() {
        return this.experimental;
    }

    public void setExperimental(final boolean experimental) {
        this.experimental = experimental;
    }

    public VersionRange getSupportedVersions() {
        return this.supportedVersions;
    }

    public void setSupportedVersions(final VersionRange supportedVersions) {
        this.supportedVersions = supportedVersions;
    }

    public boolean isSupportedVersion(final VersionEnum version) {
        if (this.supportedVersions == null) return true;
        return this.supportedVersions.contains(version);
    }

}
