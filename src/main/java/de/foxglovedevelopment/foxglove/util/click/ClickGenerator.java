package de.foxglovedevelopment.foxglove.util.click;

public abstract class ClickGenerator {

    protected Runnable clickAction = () -> {
    };

    public void setClickAction(final Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public abstract void update();

}
