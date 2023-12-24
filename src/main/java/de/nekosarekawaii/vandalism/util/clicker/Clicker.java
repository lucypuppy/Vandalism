package de.nekosarekawaii.vandalism.util.clicker;

import de.nekosarekawaii.vandalism.util.MinecraftWrapper;

public abstract class Clicker implements MinecraftWrapper {

    protected Runnable clickAction = () -> {
    };

    public void setClickAction(final Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public abstract void onUpdate();

}
