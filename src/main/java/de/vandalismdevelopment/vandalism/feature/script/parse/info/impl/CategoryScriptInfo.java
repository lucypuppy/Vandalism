package de.vandalismdevelopment.vandalism.feature.script.parse.info.impl;

import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.feature.script.parse.info.IScriptInfo;

public class CategoryScriptInfo implements IScriptInfo<Feature.Category> {

    @Override
    public String tag() {
        return "category";
    }

    @Override
    public Feature.Category parse(final String line) throws Exception {
        try {
            return Feature.Category.valueOf(line.toUpperCase());
        } catch (Exception e) {
            throw new Exception("Invalid category!");
        }
    }

    @Override
    public Feature.Category defaultValue() {
        return Feature.Category.MISC;
    }

}
