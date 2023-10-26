package de.vandalismdevelopment.vandalism.feature;

import java.util.concurrent.CopyOnWriteArrayList;

public class FeatureList<A extends Feature> extends CopyOnWriteArrayList<A> {

    public A get(final String name) {
        return this.get(name, true);
    }

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

    public <B extends A> B get(final Class<B> clazz) {
        for (final A feature : this) {
            if (clazz.isInstance(feature)) {
                return clazz.cast(feature);
            }
        }
        return null;
    }

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
