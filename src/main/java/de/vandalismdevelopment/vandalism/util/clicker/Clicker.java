package de.vandalismdevelopment.vandalism.util.clicker;

public abstract class Clicker {

    protected Runnable clickAction = () -> {
    };

    public void setClickAction(final Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public abstract void update();

    public long cpsToMs(final int cps) {
        return 1000L / cps;
    }

}
