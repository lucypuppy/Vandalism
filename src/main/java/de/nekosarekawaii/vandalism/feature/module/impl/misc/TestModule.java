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

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.render.Buffers;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.gl.render.*;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.Passes;
import de.nekosarekawaii.vandalism.util.render.shape.UVSphere;
import de.nekosarekawaii.vandalism.util.render.text.AtlasFont;
import de.nekosarekawaii.vandalism.util.render.text.AtlasFontRenderer;
import de.nekosarekawaii.vandalism.util.render.text.SimpleFont;
import de.nekosarekawaii.vandalism.util.render.text.TextAlign;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

public class TestModule extends Module implements Render2DListener, Render3DListener {

    private AtlasFont testFont;

    public TestModule() {
        super("Test", "Just for development purposes.", Category.MISC);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(Render2DListener.Render2DEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(Render3DListener.Render3DEvent.ID, this);
        try {
            testFont = SimpleFont.compose(72, Files.readAllBytes(new File("C:\\Users\\Bruce\\Desktop\\bertofont.ttf").toPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeactivate() {
        if (testFont != null) {
            testFont.close();
            testFont = null;
        }
        Vandalism.getInstance().getEventSystem().unsubscribe(Render2DListener.Render2DEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(Render3DListener.Render3DEvent.ID, this);
        if (this.mesh != null) {
            this.mesh.close();
            this.mesh = null;
        }
    }

    private PersistentMesh mesh;

    @Override
    public void onRender2DInGame(DrawContext context, float delta) {
        Shaders.getGlowOutlineEffect().configure(10.0f, 1.0f, 0.5f);
        Shaders.getGlowOutlineEffect().bindMask();
        context.drawText(mc.textRenderer, Text.literal("Hallo welters!"), 200, 200, 0xFFFF0000, false);
        Shaders.getGlowOutlineEffect().setNoClear(true);
        Shaders.getGlowOutlineEffect().renderFullscreen(Shaders.getOuterOutlineEffect().maskFramebuffer().get(), false);
        Shaders.getGlowOutlineEffect().renderFullscreen(mc.getFramebuffer(), false);
        Shaders.getGlowOutlineEffect().setNoClear(false);
        Shaders.getOuterOutlineEffect().configure(2.0f, 1.0f);
        Shaders.getOuterOutlineEffect().renderFullscreen(mc.getFramebuffer(), false);
        context.drawText(mc.textRenderer, Text.literal("Hallo welters!"), 200, 200, 0xFFFF7FFF, false);

        if (testFont != null) {
            try (final ImmediateRenderer renderer = new ImmediateRenderer(Buffers.getImmediateBufferPool())) {
                final AtlasFontRenderer fontRenderer = new AtlasFontRenderer(this.testFont);

                fontRenderer.drawScaled(
                        "§c§n§mBert ist dick",
                        context.getScaledWindowWidth() / 2.0f,
                        50.0f,
                        0.0f,
                        true,
                        0xFFFFFFFF,
                        TextAlign.X_CENTER,
                        context.getMatrices().peek().getPositionMatrix(),
                        renderer,
                        null
                );
                renderer.draw();
            }
        }
        /*context.getMatrices().push();
        context.getMatrices().translate(50.0f, 20.0f, 0.0f);
        if (this.mesh == null) {
            System.out.println("Building mesh");
            try (final PersistentMeshProducer renderer = new PersistentMeshProducer(Buffers.getPersistentBufferPool())) {
                final Matrix4f mat = context.getMatrices().peek().getPositionMatrix();
                final AttribConsumerSet sphereSet = renderer.getAttribConsumers(Passes.colorTriangle());
                final InstancedAttribConsumer consumer = sphereSet.main();
                final IndexConsumer indices = sphereSet.indexData();

                UVSphere.generateIndexed(32, 32, 250, (pos, uv, normal) -> {
                    consumer.pos(mat, pos).putColor8(0xFF000000 | (ThreadLocalRandom.current().nextInt() & 0xFFFFFF)).next();
                }, indices::index);

                this.mesh = renderer.buildMesh();
            }
        } else {
            this.mesh.draw(context.getMatrices().peek().getPositionMatrix());
        }
        context.getMatrices().pop();*/
    }

    @Override
    public void onRender2DOutGamePre(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void onRender2DOutGamePost(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void onRender3D(final float tickDelta, final MatrixStack matrixStack) {
        matrixStack.push();
        matrixStack.translate(0, 120.0f, 0.0f);
        try (final ImmediateRenderer renderer = new ImmediateRenderer(Buffers.getImmediateBufferPool())) {
            final Vec3d camPos = mc.gameRenderer.getCamera().getPos();
            matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
            matrixStack.multiply(new Quaternionf().rotateX((float) Math.toRadians(-90.0)));
            final Matrix4f mat = matrixStack.peek().getPositionMatrix();
            final AttribConsumerSet sphereSet = renderer.getAttribConsumers(Passes.colorTextureTriangle());
            final InstancedAttribConsumer consumer = sphereSet.main();
            final IndexConsumer indices = sphereSet.indexData();

            UVSphere.generateIndexed(64, 64, 25, (pos, uv, normal) -> {
                consumer.pos(mat, pos).putUV(uv).putColor8(0xFF000000 | (ThreadLocalRandom.current().nextInt() & 0xFFFFFF)).next();
            }, indices::index);

            RenderSystem.bindTexture(mc.getTextureManager().getTexture(Identifier.of(FabricBootstrap.MOD_ID, "textures/8k_earth_daymap.png")).getGlId());
            RenderSystem.disableCull();
            renderer.draw();
            RenderSystem.enableCull();
        }
        matrixStack.pop();
    }
}
