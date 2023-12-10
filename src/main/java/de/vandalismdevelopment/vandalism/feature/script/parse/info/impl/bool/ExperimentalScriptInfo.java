package de.vandalismdevelopment.vandalism.feature.script.parse.info.impl.bool;

import de.vandalismdevelopment.vandalism.feature.script.parse.info.impl.BooleanScriptInfo;

public class ExperimentalScriptInfo extends BooleanScriptInfo {

    @Override
    public String tag() {
        return "experimental";
    }

    @Override
    public Boolean defaultValue() {
        return false;
    }

}
