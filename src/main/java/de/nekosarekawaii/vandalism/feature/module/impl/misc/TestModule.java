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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.astar.AsyncPathfinder;
import de.nekosarekawaii.vandalism.util.astar.PathNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TestModule extends Module implements PlayerUpdateListener, Render3DListener {

    private final AsyncPathfinder asyncPathfinder = new AsyncPathfinder();

    public TestModule() {
        super("Test", "Just for development purposes.", Category.MISC);
    }

    @Override
    protected void onActivate() {
        ChatUtil.infoChatMessage("Hello from " + this.getName() + " Module ;3");
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, Render3DEvent.ID);

        if (this.asyncPathfinder.getLastPath() != null)
            this.asyncPathfinder.getLastPath().clear();
    }

    @Override
    protected void onDeactivate() {
        ChatUtil.infoChatMessage("Goodbye from " + this.getName() + " Module ;c");

        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, Render3DEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        this.asyncPathfinder.findPath(new PathNode(mc.world, mc.player.getBlockPos()),
                new PathNode(mc.world, new BlockPos(159, 62, 161)), path -> {
                    if (path != null) {
                        ChatUtil.infoChatMessage("Path found! Length: " + path.size());
                    } else {
                        ChatUtil.infoChatMessage("Path not found!");
                    }
                });
    }

    @Override
    public void onRender3D(float tickDelta, MatrixStack matrixStack) {
        if (this.asyncPathfinder.getLastPath() != null && mc.player != null && mc.world != null) {
            final VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
            final Vec3d vec = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().negate();
            matrixStack.push();
            matrixStack.translate(vec.x, vec.y, vec.z);

            PathNode lastRenderedNode = null;
            final List<PathNode> path = this.asyncPathfinder.getLastPath();
            for (final PathNode pathNode : path) {
                final boolean fistNode = path.getFirst().equals(pathNode);
                final boolean lastNode = path.getLast().equals(pathNode);

                if (!fistNode && !lastNode &&
                        (//(lastRenderedNode != null && lastRenderedNode.distanceTo(pathNode) < 5) ||
                                Math.sqrt(pathNode.getPos().getSquaredDistance(mc.player.getBlockPos())) > 50))
                    continue;

                float r = 0, g = 0, b = 1f;
                if (fistNode) {
                    b = 0f;
                    g = 1f;
                } else if (lastNode) {
                    b = 0f;
                    r = 1f;
                }


                DebugRenderer.drawBox(
                        matrixStack,
                        immediate,
                        pathNode.getPos().getX(),
                        pathNode.getPos().getY(),
                        pathNode.getPos().getZ(),
                        pathNode.getPos().getX() + 1,
                        pathNode.getPos().getY() + 2,
                        pathNode.getPos().getZ() + 1,
                        r, g, b, 0.5f
                );

                lastRenderedNode = pathNode;
            }

            immediate.draw();
            matrixStack.pop();
        }
    }


}
