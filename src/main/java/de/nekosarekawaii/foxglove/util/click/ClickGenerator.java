package de.nekosarekawaii.foxglove.util.click;

public abstract class ClickGenerator {

    protected final Runnable clickAction;

    public ClickGenerator(final Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public abstract void update();

}
