package de.vandalismdevelopment.vandalism.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.PacketListener;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Uuids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DebugModule extends Module implements RenderListener, PacketListener {

    private final List<String> openCustomPayloadChannels;

    private final Value<Boolean> showEntitiesInRange = new BooleanValue(
            "Show Entities in Range",
            "Shows entities in range and some infos from them.",
            this,
            true
    );

    private final Value<Boolean> showOpenCustomPayloadChannels = new BooleanValue(
            "Show Open Custom Payload Channels",
            "Shows open custom payload channels.",
            this,
            true
    );

    public DebugModule() {
        super("Debug", "Displays more infos about the game.", FeatureCategory.DEVELOPMENT, true, false);
        this.openCustomPayloadChannels = new ArrayList<>();
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }


    @Override
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final CustomPayloadS2CPacket customPayloadS2CPacket) {
            final String channel = customPayloadS2CPacket.payload().id().toString();
            if (!this.openCustomPayloadChannels.contains(channel)) {
                this.openCustomPayloadChannels.add(channel);
            }
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().addRenderInterface(io -> {
            if (player() != null) {
                if (world() != null) {
                    if (this.showEntitiesInRange.getValue()) {
                        if (ImGui.begin("Entities in Range##debugModuleEntitiesInRange", ImGuiWindowFlags.NoCollapse)) {
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
                    if (this.showOpenCustomPayloadChannels.getValue()) {
                        if (ImGui.begin("Open Custom Payload Channels##debugModuleShowOpenCustomPayloadChannels", ImGuiWindowFlags.NoCollapse)) {
                            for (final String channel : this.openCustomPayloadChannels) {
                                ImGui.text(channel);
                            }
                            ImGui.end();
                        }
                    }
                }
            }
        });
    }

}