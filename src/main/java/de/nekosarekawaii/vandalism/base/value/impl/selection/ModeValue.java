package de.nekosarekawaii.vandalism.base.value.impl.selection;

import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.template.ValueModeGeneric;

import java.util.function.Function;

public class ModeValue extends ValueModeGeneric<String> {

    public ModeValue(ValueParent parent, String name, String description, String... options) {
        super(parent, name, description, Function.identity(), Function.identity(), options);
    }

}
