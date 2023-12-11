package de.vandalismdevelopment.vandalism.base.value.impl.selection;

import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.template.ValueModeGeneric;

import java.util.function.Function;

public class ModeValue extends ValueModeGeneric<String> {

    public ModeValue(ValueParent parent, String name, String description, String defaultValue, String... options) {
        super(parent, name, description, defaultValue, Function.identity(), Function.identity(), options);
    }

}
