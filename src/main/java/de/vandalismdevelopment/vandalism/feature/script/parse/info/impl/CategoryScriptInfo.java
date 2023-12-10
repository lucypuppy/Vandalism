package de.vandalismdevelopment.vandalism.feature.script.parse.info.impl;

import de.vandalismdevelopment.vandalism.feature.script.parse.info.IScriptInfo;

public class CategoryScriptInfo implements IScriptInfo<FeatureCategory> {

    @Override
    public String tag() {
        return "category";
    }

    @Override
    public FeatureCategory parse(final String line) throws Exception {
        final FeatureCategory category = FeatureCategory.fromNormalName(line);
        if (category == null) throw new Exception("Invalid category!");
        return category;
    }

    @Override
    public FeatureCategory defaultValue() {
        return FeatureCategory.MISC;
    }

}
