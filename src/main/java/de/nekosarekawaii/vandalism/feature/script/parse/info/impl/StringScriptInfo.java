package de.nekosarekawaii.vandalism.feature.script.parse.info.impl;

import de.nekosarekawaii.vandalism.feature.script.parse.info.IScriptInfo;

public abstract class StringScriptInfo implements IScriptInfo<String> {

    @Override
    public String parse(final String line) throws Exception {
        if (line.isBlank()) {
            throw new RuntimeException("The value is empty.");
        }
        return line;
    }

}
