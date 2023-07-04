package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.Render2DListener;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Debug Module", description = "Debug some stuff about the client.", category = FeatureCategory.MISC)
public class DebugModule extends Module implements Render2DListener, TickListener {

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    private final List<Integer> fpsHistory = new ArrayList<>();
    private final Integer[] graphSize = new Integer[100];
    private int lastFPS = 0;

    public DebugModule() {
        for (int i = 0; i < this.graphSize.length; i++) {
            this.graphSize[i] = i;
            this.fpsHistory.add(0);
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        if (fpsHistory.size() < 100)
            return;

        Foxglove.getInstance().imGuiRenderer.addRenderInterface(io -> {
            if (ImGui.begin("RoflGraph")) {
                if (ImPlot.beginPlot("lol")) {
                    ImPlot.plotLine("FPS", this.graphSize, this.fpsHistory.toArray(Integer[]::new));
                    ImPlot.endPlot();
                }

                ImGui.end();
            }
        });
    }

    @Override
    public void onTick() {
        final int curFPS = mc().getCurrentFps();

        if (curFPS != lastFPS) {
            this.fpsHistory.add(curFPS);

            if (this.fpsHistory.size() > 100) {
                this.fpsHistory.remove(0);
            }
        }

        this.lastFPS = curFPS;
    }

}
