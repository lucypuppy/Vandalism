package me.nekosarekawaii.foxglove.feature;

import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;

/**
 * The Feature class serves as a base class for different features in the Foxglove mod.
 * It provides common properties and methods that can be used by feature implementations.
 * It also implements the MinecraftWrapper interface.
 */
public abstract class Feature implements MinecraftWrapper {

    private String name = "Example Feature";
    private String description = "This is a Feature.";
    private FeatureType type = FeatureType.NONE;
    private FeatureCategory category = FeatureCategory.MISC;
    private boolean experimental = false;

    /**
     * Gets the name of the feature.
     *
     * @return The name of the feature.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the feature.
     *
     * @param name The name of the feature.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the description of the feature.
     *
     * @return The description of the feature.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the feature.
     *
     * @param description The description of the feature.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the type of the feature.
     *
     * @return The type of the feature.
     */
    public FeatureType getType() {
        return this.type;
    }

    /**
     * Sets the type of the feature.
     *
     * @param type The type of the feature.
     */
    public void setType(final FeatureType type) {
        this.type = type;
    }

    /**
     * Gets the category of the feature.
     *
     * @return The category of the feature.
     */
    public FeatureCategory getCategory() {
        return this.category;
    }

    /**
     * Sets the category of the feature.
     *
     * @param category The category of the feature.
     */
    public void setCategory(final FeatureCategory category) {
        this.category = category;
    }

    /**
     * Sets the experimental status of the feature.
     *
     * @param experimental The experimental status of the feature.
     */
    public void setExperimental(final boolean experimental) {
        this.experimental = experimental;
    }

    /**
     * Checks if the feature is marked as experimental.
     *
     * @return True if the feature is marked as experimental, false otherwise.
     */
    public boolean isExperimental() {
        return this.experimental;
    }

}
