package de.nekosarekawaii.vandalism.feature.script.parse.info.impl.string;

import de.nekosarekawaii.vandalism.feature.script.parse.info.impl.StringScriptInfo;

public class VersionScriptInfo extends StringScriptInfo {

    @Override
    public String tag() {
        return "version";
    }

    @Override
    public String defaultValue() {
        return "1.0.0";
    }

}
