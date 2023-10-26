package de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl;

import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.IScriptInfo;

public abstract class StringScriptInfo implements IScriptInfo<String> {

    @Override
    public String parse(final String line) throws Exception {
        if (line.isBlank()) {
            throw new RuntimeException("The value is empty.");
        }
        return line;
    }

}
