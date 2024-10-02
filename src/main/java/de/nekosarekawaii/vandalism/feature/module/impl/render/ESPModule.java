/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiRegistryBlacklistValue;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.BlockStateListener;
import de.nekosarekawaii.vandalism.event.game.BlockStateUpdateListener;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.template.target.TargetGroup;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ESPModule extends Module implements BlockStateListener, BlockStateUpdateListener, WorldListener, DisconnectListener, Render3DListener {

    private final TargetGroup entityGroup = new TargetGroup(this, "Entities", "The entities to target.");

    private final ColorValue entityColor = new ColorValue(
            this.entityGroup,
            "Entity Color",
            "The color of the ESP for entities."
    );

    private final ColorValue friendsColor = new ColorValue(
            this.entityGroup,
            "Friends Color",
            "The color of the ESP for friends.",
            Color.GREEN
    );

    private final BooleanValue items = new BooleanValue(
            this,
            "Items",
            "Whether items should also have an ESP.",
            false
    );

    private final MultiRegistryBlacklistValue<Item> itemList = new MultiRegistryBlacklistValue<>(
            this,
            "Item List",
            "The items to target.",
            Registries.ITEM,
            Collections.singletonList(
                    Items.AIR
            )
    ).visibleCondition(this.items::getValue);

    private final BooleanValue blocks = new BooleanValue(
            this,
            "Blocks",
            "Whether blocks should have an ESP.",
            false
    );

    private final DoubleValue maxBlockDistance = new DoubleValue(
            this,
            "Max Block Distance",
            "The maximum distance to render the ESP for blocks.",
            50d,
            1d,
            100d
    ).visibleCondition(this.blocks::getValue);

    private final MultiRegistryBlacklistValue<Block> blockList = new MultiRegistryBlacklistValue<>(
            this,
            "Block List",
            "The blocks to target.",
            Registries.BLOCK,
            Arrays.asList(
                    Blocks.AIR,
                    Blocks.CAVE_AIR,
                    Blocks.VOID_AIR
            )
    ).visibleCondition(this.blocks::getValue);

    private final ColorValue blockColor = new ColorValue(
            this,
            "Block Color",
            "The color of the ESP for blocks.",
            new Color(221, 120, 246, 255)
    ).visibleCondition(this.blocks::getValue);

    private final List<BlockPos> espBlocks = new CopyOnWriteArrayList<>();

    public ESPModule() {
        super(
                "ESP",
                "Lets you see blocks or entities trough blocks.",
                Category.RENDER
        );
    }

    public boolean isTarget(final Entity entity) {
        if (!this.entityGroup.isTarget(entity)) {
            return false;
        }
        if (entity instanceof final PlayerEntity player) {
            final GameProfile gameProfile = player.getGameProfile();
            if (gameProfile != null) {
                if (Vandalism.getInstance().getFriendsManager().isFriend(gameProfile.getName(), true)) {
                    return true;
                }
            }
        }
        return entity instanceof final ItemEntity itemEntity && this.itemList.isSelected(itemEntity.getStack().getItem()) && this.items.getValue();
    }

    public Color getEntityColor(final Entity entity) {
        if (entity instanceof final PlayerEntity player) {
            final GameProfile gameProfile = player.getGameProfile();
            if (gameProfile != null) {
                if (Vandalism.getInstance().getFriendsManager().isFriend(gameProfile.getName(), true)) {
                    return this.friendsColor.getColor();
                }
            }
        }
        return this.entityColor.getColor();
    }

    @Override
    public void onActivate() {
        this.espBlocks.clear();
        Vandalism.getInstance().getEventSystem().subscribe(this, BlockStateEvent.ID, BlockStateUpdateEvent.ID, WorldLoadEvent.ID, DisconnectEvent.ID, Render3DEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, BlockStateEvent.ID, BlockStateUpdateEvent.ID, WorldLoadEvent.ID, DisconnectEvent.ID, Render3DEvent.ID);
        this.espBlocks.clear();
    }

    @Override
    public void onPreWorldLoad() {
        this.espBlocks.clear();
    }

    @Override
    public void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason) {
        this.espBlocks.clear();
    }

    @Override
    public void onBlockState(final BlockPos pos, final BlockState state) {
        if (!this.espBlocks.contains(pos) && this.blockList.isSelected(state.getBlock())) {
            this.espBlocks.add(pos);
        }
    }

    @Override
    public void onBlockStateUpdate(final BlockPos pos, final BlockState previousState, final BlockState state) {
        if (this.espBlocks.contains(pos) && previousState != state) {
            this.espBlocks.remove(pos);
        }
    }

    @Override
    public void onRender3D(final float tickDelta, final MatrixStack matrixStack) {
        if (!this.blocks.getValue() || this.mc.player == null) {
            return;
        }
        this.espBlocks.removeIf(pos -> pos.toCenterPos().distanceTo(this.mc.player.getPos()) > this.maxBlockDistance.getValue() ||
                !this.blockList.isSelected(this.mc.world.getBlockState(pos).getBlock()));
        final VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        matrixStack.push();
        for (final BlockPos pos : this.espBlocks) {
            final float[] color = ColorUtils.rgba(this.blockColor.getColor().getRGB());
            DebugRenderer.drawBox(
                    matrixStack,
                    immediate,
                    pos,
                    pos,
                    color[0],
                    color[1],
                    color[2],
                    Math.min(color[3], 0.5f)
            );
        }
        matrixStack.pop();
        immediate.draw();
    }

}
