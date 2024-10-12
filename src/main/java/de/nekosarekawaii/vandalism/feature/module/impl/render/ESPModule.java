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
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.target.TargetGroup;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.game.BlockStateListener;
import de.nekosarekawaii.vandalism.event.game.BlockStateUpdateListener;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ESPModule extends Module implements PlayerUpdateListener, BlockStateListener, BlockStateUpdateListener, WorldListener, DisconnectListener, Render3DListener {

    // Entity settings
    private final BooleanValue renderEntities = new BooleanValue(
            this,
            "Render entities",
            "Whether entities should have an ESP.",
            true
    );

    private final TargetGroup entityGroup = new TargetGroup(
            this,
            "Entity settings",
            "The ESP settings for entities."
    ).visibleCondition(this.renderEntities::getValue);

    private final ColorValue entityColor = new ColorValue(
            this.entityGroup,
            "Entity Color",
            "The color of the ESP for entities."
    ).visibleCondition(this.renderEntities::getValue);

    private final ColorValue playerColor = new ColorValue(
            this.entityGroup,
            "Player Color",
            "The color of the ESP for players.",
            Color.RED
    ).visibleCondition(this.renderEntities::getValue);

    private final ColorValue friendsColor = new ColorValue(
            this.entityGroup,
            "Friends Color",
            "The color of the ESP for friends.",
            Color.GREEN
    ).visibleCondition(this.renderEntities::getValue);

    // Item settings
    private final BooleanValue renderItems = new BooleanValue(
            this,
            "Render items",
            "Whether items should have an ESP.",
            false
    );

    private final ValueGroup itemsGroup = new ValueGroup(
            this,
            "Item settings",
            "The ESP settings for items."
    ).visibleCondition(this.renderItems::getValue);

    private final MultiRegistryBlacklistValue<Item> itemList = new MultiRegistryBlacklistValue<>(
            this.itemsGroup,
            "Item List",
            "The items to target.",
            Registries.ITEM,
            Collections.singletonList(
                    Items.AIR
            )
    ).visibleCondition(this.renderItems::getValue);

    private final ColorValue itemColor = new ColorValue(
            this.itemsGroup,
            "Item color",
            "The color of the ESO for items.",
            new Color(50, 255, 255)
    ).visibleCondition(this.renderItems::getValue);

    // Block settings
    private final BooleanValue renderBlocks = new BooleanValue(
            this,
            "Render blocks",
            "Whether blocks should have an ESP.",
            false
    ).onValueChange((oldValue, newValue) -> {
        if (!newValue) this.espBlocks.clear();
    });

    private final ValueGroup blocksGroup = new ValueGroup(
            this,
            "Block settings",
            "The ESP settings for blocks."
    ).visibleCondition(this.renderBlocks::getValue);

    private final DoubleValue maxBlockDistance = new DoubleValue(
            this.blocksGroup,
            "Max Block Distance",
            "The maximum ESP render distance for blocks.",
            50d,
            1d,
            100d
    ).visibleCondition(this.renderBlocks::getValue);

    private final IntegerValue maxBlockAmount = new IntegerValue(
            this.blocksGroup,
            "Max block amount",
            "The maximum amount of blocks to render",
            150,
            50,
            1000
    ).visibleCondition(this.renderBlocks::getValue);

    private final BooleanValue fovCheck = new BooleanValue(
            this.blocksGroup,
            "FOV check",
            "Checks if the block is in fov",
            true
    ).visibleCondition(this.renderBlocks::getValue);

    private final MultiRegistryBlacklistValue<Block> blockList = new MultiRegistryBlacklistValue<>(
            this.blocksGroup,
            "Block List",
            "The blocks to target.",
            Registries.BLOCK,
            Arrays.asList(
                    Blocks.AIR,
                    Blocks.CAVE_AIR,
                    Blocks.VOID_AIR
            )
    ).visibleCondition(this.renderBlocks::getValue);

    private final ColorValue blockColor = new ColorValue(
            this.blocksGroup,
            "Block Color",
            "The color of the ESP for blocks.",
            new Color(255, 255, 255, 100)
    ).visibleCondition(this.renderBlocks::getValue);

    private final List<BlockPos> espBlocks = new ArrayList<>();

    public ESPModule() {
        super(
                "ESP",
                "Lets you see blocks or entities trough blocks.",
                Category.RENDER
        );
    }

    public boolean isTarget(final Entity entity) {
        if (entity instanceof final ItemEntity itemEntity) {
            return this.renderItems.getValue() && this.itemList.isSelected(itemEntity.getStack().getItem());
        } else {
            return this.renderEntities.getValue() && this.entityGroup.isTarget(entity, true);
        }
    }

    public Color getEntityColor(final Entity entity) {
        if (entity instanceof final PlayerEntity player) {
            final GameProfile gameProfile = player.getGameProfile();
            if (gameProfile != null) {
                if (Vandalism.getInstance().getFriendsManager().isFriend(gameProfile.getName(), true)) {
                    return this.friendsColor.getColor();
                }
            }
            return this.playerColor.getColor();
        } else if (entity instanceof ItemEntity) {
            return this.itemColor.getColor();
        }
        return this.entityColor.getColor();
    }

    @Override
    public void onActivate() {
        this.espBlocks.clear();
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, BlockStateEvent.ID, BlockStateUpdateEvent.ID, WorldLoadEvent.ID, DisconnectEvent.ID, Render3DEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, BlockStateEvent.ID, BlockStateUpdateEvent.ID, WorldLoadEvent.ID, DisconnectEvent.ID, Render3DEvent.ID);
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
        if (this.renderBlocks.getValue() && !this.espBlocks.contains(pos) && this.blockList.isSelected(state.getBlock())) {
            double distance = pos.toCenterPos().distanceTo(this.mc.player.getPos());
            if (distance <= this.maxBlockDistance.getValue()) {
                if (this.fovCheck.getValue()) {
                    Vec2f deltaRotation = this.getDeltaRotation(pos);
                    if (Math.abs(deltaRotation.x) > this.mc.options.getFov().getValue() || Math.abs(deltaRotation.y) > this.mc.options.getFov().getValue() * 0.5) {
                        return;
                    }
                }
                if (this.espBlocks.size() < this.maxBlockAmount.getValue()) {
                    this.espBlocks.add(pos);
                }
            }
        }
    }

    @Override
    public void onBlockStateUpdate(final BlockPos pos, final BlockState previousState, final BlockState state) {
        if (this.renderBlocks.getValue() && state != null && !this.blockList.isSelected(state.getBlock())) {
            this.espBlocks.remove(pos);
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        for (int i = 0; i < this.espBlocks.size(); i++) {
            final BlockPos blockPos = this.espBlocks.get(i);
            final double distance = blockPos.toCenterPos().distanceTo(this.mc.player.getPos());
            if (distance > this.maxBlockDistance.getValue() || !this.blockList.isSelected(this.mc.world.getBlockState(blockPos).getBlock())) {
                this.espBlocks.remove(blockPos);
            } else if (this.fovCheck.getValue()) {
                Vec2f deltaRotation = this.getDeltaRotation(blockPos);
                if (Math.abs(deltaRotation.x) > this.mc.options.getFov().getValue() || Math.abs(deltaRotation.y) > this.mc.options.getFov().getValue() * 0.5) {
                    this.espBlocks.remove(blockPos);
                }
            }
        }
    }

    @Override
    public void onRender3D(final float tickDelta, final MatrixStack matrixStack) {
        if (!this.renderBlocks.getValue() || this.mc.player == null) {
            return;
        }
        final VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        final int color = this.blockColor.getColor().getRGB();
        final float red = ((color >> 16) & 0xff) / 255f;
        final float green = ((color >> 8) & 0xff) / 255f;
        final float blue = ((color) & 0xff) / 255f;
        final float alpha = ((color >> 24) & 0xff) / 255f;
        final Vec3d vec = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().negate();
        matrixStack.push();
        matrixStack.translate(vec.x, vec.y, vec.z);
        for (int i = 0; i < this.espBlocks.size(); i++) {
            final BlockPos blockPos = this.espBlocks.get(i);
            DebugRenderer.drawBox(
                    matrixStack,
                    immediate,
                    blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ(),
                    blockPos.getX() + 1,
                    blockPos.getY() + 1,
                    blockPos.getZ() + 1,
                    red,
                    green,
                    blue,
                    alpha
            );
        }
        immediate.draw();
        matrixStack.pop();
    }

    private Vec2f getDeltaRotation(final BlockPos blockPos) {
        final double rotationDeltaX = blockPos.toCenterPos().x - this.mc.player.getPos().x;
        final double rotationDeltaY = blockPos.toCenterPos().y - (this.mc.player.getPos().y + this.mc.player.getEyeHeight(this.mc.player.getPose()));
        final double rotationDeltaZ = blockPos.toCenterPos().z - this.mc.player.getPos().z;

        final float rotationYaw = (float) (Math.atan2(rotationDeltaZ, rotationDeltaX) * 180.0D / Math.PI) - 90.0F;
        final float rotationPitch = (float) (-(Math.atan2(rotationDeltaY, Math.hypot(rotationDeltaX, rotationDeltaZ)) * 180.0D / Math.PI));

        final float deltaYaw = MathHelper.wrapDegrees(rotationYaw - this.mc.player.getYaw() - (this.mc.options.getPerspective() == Perspective.THIRD_PERSON_FRONT ? 180 : 0));
        final float deltaPitch = rotationPitch - this.mc.player.getPitch();
        return new Vec2f(deltaYaw, deltaPitch);
    }

}
