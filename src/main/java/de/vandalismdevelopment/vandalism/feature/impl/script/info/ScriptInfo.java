package de.vandalismdevelopment.vandalism.feature.impl.script.info;

import de.vandalismdevelopment.vandalism.feature.impl.script.info.impl.CategoryScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.info.impl.bool.ExperimentalScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.info.impl.string.AuthorScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.info.impl.string.DescriptionScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.info.impl.string.VersionScriptInfo;

public enum ScriptInfo {

    VERSION(new VersionScriptInfo()),
    AUTHOR(new AuthorScriptInfo()),
    DESCRIPTION(new DescriptionScriptInfo()),
    CATEGORY(new CategoryScriptInfo()),
    EXPERIMENTAL(new ExperimentalScriptInfo());

    private final IScriptInfo<?> IScriptInfo;


    ScriptInfo(final IScriptInfo<?> IScriptInfo) {
        this.IScriptInfo = IScriptInfo;
    }

    public String getTag() {
        return this.IScriptInfo.tag();
    }

    public <T> T parseValue(final String line) throws Exception {
        return (T) this.IScriptInfo.parse(line);
    }

    public <T> T getDefaultValue() {
        return (T) this.IScriptInfo.defaultValue();
    }

    public IScriptInfo<?> get() {
        return this.IScriptInfo;
    }

}
