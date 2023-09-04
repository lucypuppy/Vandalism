package de.nekosarekawaii.foxglove.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.RenderListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.util.Uuids;

import java.util.Arrays;
import java.util.UUID;

public class DebugModule extends Module implements RenderListener {

    public DebugModule() {
        super("Debug", "Displays more infos about the game.", FeatureCategory.DEVELOPMENT, true, false);
    }

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
            if (player() != null) {
                if (world() != null) {
                    if (ImGui.begin("Entities in Range##debugModuleEntitiesInRange")) {
                        int i = 0;
                        for (final Entity entity : world().getEntities()) {
                            final String entityUUID = entity.getUuidAsString();
                            ImGui.textWrapped(entity.getName().getString() + " [" + entity.getClass().getSimpleName() + "] (" + player().distanceTo(entity) + ")");
                            if (entity instanceof final Ownable ownableEntity) {
                                final Entity owner = ownableEntity.getOwner();
                                if (owner != null) {
                                    ImGui.sameLine();
                                    final String ownerUUID = owner.getUuidAsString();
                                    ImGui.textWrapped(" (Owner: " + owner.getName().getString() + ")");
                                    ImGui.sameLine();
                                    if (ImGui.button("Copy Owner Entity UUID##copyOwnerEntityUUID" + i)) {
                                        keyboard().setClipboard(ownerUUID + " | " + Arrays.toString(Uuids.toIntArray(UUID.fromString(ownerUUID))));
                                    }
                                }
                            }
                            ImGui.sameLine();
                            if (ImGui.button("Copy Entity UUID##copyEntityUUID" + i)) {
                                keyboard().setClipboard(entityUUID + " | " + Arrays.toString(Uuids.toIntArray(UUID.fromString(entityUUID))));
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
