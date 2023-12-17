package de.nekosarekawaii.vandalism.feature.module.impl.development;

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public class TestModule extends AbstractModule {

    public TestModule() {
        super("Test", "Just for development purposes.", Category.DEVELOPMENT);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }

}
