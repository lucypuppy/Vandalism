package de.nekosarekawaii.vandalism.feature.script.parse.info.impl.string;

import de.nekosarekawaii.vandalism.feature.script.parse.info.impl.StringScriptInfo;

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
