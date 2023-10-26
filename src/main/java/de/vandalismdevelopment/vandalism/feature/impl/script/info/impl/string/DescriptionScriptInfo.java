package de.vandalismdevelopment.vandalism.feature.impl.script.info.impl.string;

import de.vandalismdevelopment.vandalism.feature.impl.script.info.impl.StringScriptInfo;

public class DescriptionScriptInfo extends StringScriptInfo {

    @Override
    public String tag() {
        return "description";
    }

    @Override
    public String defaultValue() {
        return "Example Description";
    }

}
