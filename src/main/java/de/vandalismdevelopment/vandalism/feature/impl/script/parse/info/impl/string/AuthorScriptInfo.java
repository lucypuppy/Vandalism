package de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.string;

import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.StringScriptInfo;

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
