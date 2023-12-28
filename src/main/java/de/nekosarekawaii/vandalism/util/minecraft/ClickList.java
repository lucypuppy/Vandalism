package de.nekosarekawaii.vandalism.util.minecraft;

import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ClickList {

    private final List<Long> clicks = new ArrayList<>();

    public void onTick() {
        if (clicks.isEmpty()) return;
        clicks.removeIf((click) -> Util.getMeasuringTimeMs() - click > 1000);
    }

    public void click() {
        clicks.add(Util.getMeasuringTimeMs());
    }

    public int clicks() {
        return clicks.size();
    }

}
