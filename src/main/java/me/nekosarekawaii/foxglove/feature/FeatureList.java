package me.nekosarekawaii.foxglove.feature;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * The FeatureList class extends ObjectArrayList to represent a list of features.
 * It provides additional methods for retrieving features based on various criteria.
 *
 * @param <A> The type of features stored in the list.
 */
public class FeatureList<A extends Feature> extends ObjectArrayList<A> {

    /**
     * Retrieves a feature from the list by its name.
     *
     * @param name       The name of the feature to retrieve.
     * @param ignoreCase Whether to ignore case when comparing the feature names.
     * @return The feature with the specified name, or null if not found.
     */
    public A get(final String name, final boolean ignoreCase) {
        for (final A feature : this) {
            if (ignoreCase) {
                if (name.equalsIgnoreCase(feature.getName())) {
                    return feature;
                }
            } else if (name.equals(feature.getName())) {
                return feature;
            }
        }
        return null;
    }

    /**
     * Retrieves a feature from the list by its name, ignoring case.
     *
     * @param name The name of the feature to retrieve.
     * @return The feature with the specified name, or null if not found.
     */
    public A get(final String name) {
        return this.get(name, true);
    }

    /**
     * Retrieves a feature from the list by its class type.
     *
     * @param clazz The class type of the feature to retrieve.
     * @param <B>   The specific subclass of the feature to retrieve.
     * @return The feature of the specified class type, or null if not found.
     */
    public <B extends A> B get(final Class<B> clazz) {
        for (final A feature : this) {
            if (clazz.isInstance(feature)) {
                return clazz.cast(feature);
            }
        }
        return null;
    }

    /**
     * Retrieves a sublist of features from the list based on the specified feature category.
     *
     * @param featureCategory The feature category used for filtering the list.
     * @return A new FeatureList containing features belonging to the specified category.
     */
    public FeatureList<A> get(final FeatureCategory featureCategory) {
        final FeatureList<A> featuresByCategory = new FeatureList<>();
        for (final A feature : this) {
            if (feature.getCategory().equals(featureCategory)) {
                featuresByCategory.add(feature);
            }
        }
        return featuresByCategory;
    }

}
