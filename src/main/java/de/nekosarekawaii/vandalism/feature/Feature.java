package de.nekosarekawaii.vandalism.feature;

import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

public abstract class Feature implements IName, MinecraftWrapper {

    private final String name;
    private final String description;
    private final Category category;
    private final VersionRange supportedVersions;
    private boolean experimental;

    public Feature(String name, String description, Category category) {
        this(name, description, category, null);
    }

    public Feature(String name, String description, Category category, VersionRange supportedVersions) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.supportedVersions = supportedVersions;
    }

    public enum Category {

        DEVELOPMENT, COMBAT, EXPLOIT, MOVEMENT, RENDER, MISC;

        public String getName() {
            return StringUtils.normalizeEnumName(name());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isExperimental() {
        return experimental;
    }

    public void setExperimental(boolean experimental) {
        this.experimental = experimental;
    }

    public VersionRange getSupportedVersions() {
        return supportedVersions;
    }

    public boolean isSupportedVersion(final VersionEnum version) {
        if (this.supportedVersions == null) {
            return true;
        }
        return this.supportedVersions.contains(version);
    }

}
