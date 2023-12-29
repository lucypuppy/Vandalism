package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.modes.suicide.BoatModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;

public class SuicideModule extends AbstractModule {

    public final ModuleModeValue<SuicideModule> mode = new ModuleModeValue<>(
            this,
            "Mode",
            "The current suicide mode.",
            new BoatModuleMode(this)
    );

    public SuicideModule() {
        super(
                "Suicide",
                "Allows you to kill your self.",
                Category.EXPLOIT
        );
        this.deactivateAfterSession();
    }

}
