package de.vandalismdevelopment.vandalism.feature.impl.script.info.impl;

import de.florianmichael.rclasses.common.object.ObjectTypeChecker;
import de.vandalismdevelopment.vandalism.feature.impl.script.info.IScriptInfo;

public abstract class BooleanScriptInfo implements IScriptInfo<Boolean> {

    @Override
    public Boolean parse(final String line) throws Exception {
        if (ObjectTypeChecker.isBoolean(line)) {
            return Boolean.parseBoolean(line);
        }
        throw new Exception("Invalid boolean!");
    }

}
