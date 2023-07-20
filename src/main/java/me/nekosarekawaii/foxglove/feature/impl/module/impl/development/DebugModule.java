package me.nekosarekawaii.foxglove.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.impl.Render2DListener;
import me.nekosarekawaii.foxglove.event.impl.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.imgui.ImGuiUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.screen.ScreenHandler;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Debug Module", description = "Debug some stuff about the client.", category = FeatureCategory.DEVELOPMENT)
public class DebugModule extends Module implements Render2DListener, TickListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
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

        Foxglove.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {

            if (ImGui.begin("Debug Graph", ImGuiUtil.getInGameFlags(0))) {
                if (ImPlot.beginPlot("Debug Graph")) {
                    ImPlot.plotLine("FPS", this.graphSize, this.fpsHistory.toArray(Integer[]::new));
                    ImPlot.endPlot();
                }

                ImGui.end();
            }

            final ClientPlayerEntity player = mc.player;
            if (player != null) {

                final ScreenHandler screenHandler = player.currentScreenHandler;
                if (screenHandler != null) {
                    if (ImGui.begin("Current Screen Handler", ImGuiUtil.getInGameFlags(0))) {
                        ImGui.text("Current Sync ID: " + screenHandler.syncId);
                        ImGui.text("Current Revision: " + screenHandler.getRevision());
                        ImGui.end();
                    }
                }

                final ClientWorld world = mc.world;
                if (world != null) {

                    if (ImGui.begin("Entities in Range##debugModuleEntitiesInRange")) {
                        int i = 0;
                        for (final Entity entity : world.getEntities()) {
                            final String entityUUID = entity.getUuidAsString();
                            ImGui.text(entity.getName().getString() + " [" + entity.getClass().getSimpleName() + "] (" + player.distanceTo(entity) + ")");
                            if (entity instanceof final EnderPearlEntity enderPearlEntity) {
                                final Entity owner = enderPearlEntity.getOwner();
                                if (owner != null) {
                                    ImGui.sameLine();
                                    final String ownerUUID = owner.getUuidAsString();
                                    ImGui.text(" (Owner: " + owner.getName().getString() + ")");
                                    ImGui.sameLine();
                                    if (ImGui.button("Copy Owner Entity UUID##copyOwnerEntityUUID" + i)) {
                                        mc.keyboard.setClipboard(ownerUUID);
                                    }
                                }
                            }
                            ImGui.sameLine();
                            if (ImGui.button("Copy Entity UUID##copyEntityUUID" + i)) {
                                mc.keyboard.setClipboard(entityUUID);
                            }
                            i++;
                        }
                        ImGui.end();
                    }

                }

            }

        });
    }

    @Override
    public void onTick() {
        final int curFPS = mc.getCurrentFps();

        if (curFPS != lastFPS) {
            this.fpsHistory.add(curFPS);

            if (this.fpsHistory.size() > 100) {
                this.fpsHistory.remove(0);
            }
        }

        this.lastFPS = curFPS;
    }

}
