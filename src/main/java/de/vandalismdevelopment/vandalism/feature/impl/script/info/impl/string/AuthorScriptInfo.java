package de.vandalismdevelopment.vandalism.feature.impl.script.info.impl.string;

import de.vandalismdevelopment.vandalism.feature.impl.script.info.impl.StringScriptInfo;

public class AuthorScriptInfo extends StringScriptInfo {

    @Override
    public String tag() {
        return "author";
    }

    @Override
    public String defaultValue() {
        return "Example Author";
    }

}
