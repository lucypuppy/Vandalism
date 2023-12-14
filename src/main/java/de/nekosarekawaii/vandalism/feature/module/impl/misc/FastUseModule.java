package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class FastUseModule extends AbstractModule {

    public final IntegerValue itemUseCooldown = new IntegerValue(
            this,
            "Item Use Cooldown",
            "Here you can input the custom use cooldown value.",
            0,
            0,
            3
    );

    public FastUseModule() {
        super("Fast Use", "Allows you to use items faster.", Category.MISC);
    }

}
