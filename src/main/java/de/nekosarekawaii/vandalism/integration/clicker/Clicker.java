package de.nekosarekawaii.vandalism.integration.clicker;

import de.nekosarekawaii.vandalism.util.MinecraftWrapper;

import java.util.function.Consumer;

public abstract class Clicker implements MinecraftWrapper {

    protected Consumer<Boolean> clickAction = aBoolean -> {
    };

    public void setClickAction(final Consumer<Boolean> clickAction) {
        this.clickAction = clickAction;
    }

    public abstract void onUpdate();

}
