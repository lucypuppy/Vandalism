package me.nekosarekawaii.foxglove.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.ImGui;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.Render2DListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.imgui.ImGuiUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.screen.ScreenHandler;

@ModuleInfo(name = "Debug Module", description = "Debug some stuff about the client.", category = FeatureCategory.DEVELOPMENT)
public class DebugModule extends Module implements Render2DListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        Foxglove.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
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

                            if (entity instanceof final Ownable ownableEntity) {
                                final Entity owner = ownableEntity.getOwner();
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

}
