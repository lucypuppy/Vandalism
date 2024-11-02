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

package de.nekosarekawaii.vandalism.util.render.gl.render.passes;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.gl.render.PrimitiveType;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayouts;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.lwjgl.opengl.GL45C;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Passes {

    private static final RenderPass colorTriangle           = new SimpleRenderPass(VertexLayouts.POSITION_COLOR8, VertexLayouts.halfPosition(2, 1), Shaders::getPositionColorShader, Shaders::getInstPositionColorShader);
    private static final RenderPass textureTriangle         = new SimpleRenderPass(VertexLayouts.POSITION_TEX, VertexLayouts.halfPosition(2, 1), Shaders::getPositionTexShader, Shaders::getInstPositionTexShader);
    private static final RenderPass colorTextureTriangle    = new SimpleRenderPass(VertexLayouts.POSITION_TEX_COLOR8, VertexLayouts.halfPosition(3, 1), Shaders::getPositionTexColorShader, Shaders::getInstPositionTexColorShader);
    private static final RenderPass colorTriangleFan        = new SimpleRenderPass(PrimitiveType.TRIANGLE_FAN, VertexLayouts.POSITION_COLOR8, VertexLayouts.halfPosition(2, 1), Shaders::getPositionColorShader, Shaders::getInstPositionColorShader);
    private static final RenderPass colorRect               = new SimpleRenderPass(PrimitiveType.QUADS, VertexLayouts.POSITION_COLOR8, VertexLayouts.halfPosition(2, 1), Shaders::getPositionColorShader, Shaders::getInstPositionColorShader);
    private static final RenderPass rect                    = new SimpleRenderPass(PrimitiveType.QUADS, VertexLayouts.POSITION, VertexLayouts.halfPosition(2, 1), Shaders::getPositionShader, Shaders::getInstPositionShader);
    private static final RenderPass textureRect             = new SimpleRenderPass(PrimitiveType.QUADS, VertexLayouts.POSITION_TEX, VertexLayouts.halfPosition(2, 1), Shaders::getPositionTexShader, Shaders::getInstPositionTexShader);
    private static final RenderPass colorTextureRect        = new SimpleRenderPass(PrimitiveType.QUADS, VertexLayouts.POSITION_TEX_COLOR8, VertexLayouts.halfPosition(3, 1), Shaders::getPositionTexColorShader, Shaders::getInstPositionTexColorShader);
    private static final RenderPass colorLineStrip          = new SimpleRenderPass(PrimitiveType.GL_LINE_STRIP, VertexLayouts.POSITION_COLOR8, VertexLayouts.halfPosition(2, 1), Shaders::getPositionColorShader, Shaders::getInstPositionColorShader);
    private static final Int2ObjectFunction<RenderPass> text = memoize((Int2ObjectFunction<RenderPass>) glyphAtlasTexture -> new SimpleRenderPass(
            PrimitiveType.QUADS,
            VertexLayouts.POSITION_TEX_COLOR8,
            Shaders::getFontShader,
            RenderSystem::enableBlend,
            shader -> {
                GL45C.glBindTextureUnit(0, glyphAtlasTexture);
                shader.uniform("texture0").set(0);
            },
            () -> {
                RenderSystem.disableBlend();
                RenderSystem.activeTexture(GL45C.GL_TEXTURE0);
                RenderSystem.bindTexture(0);
            }
    ));
    /*private static final Long2ObjectFunction<RenderPass> sdfText = memoize((Long2ObjectFunction<RenderPass>) l -> new SimpleRenderPass(
            PrimitiveType.QUADS,
            VertexLayouts.SDF_TEXT,
            Shaders::getSdfFontShader,
            RenderSystem::enableBlend,
            shader -> {
                final int texture = (int) (l >> 32);
                final float onEdgeValue = (int) (l & 0xFFFFFFFFL) / 255.0f;
                GL45C.glBindTextureUnit(0, texture);
                shader.uniform("tSDF").set(0);
                shader.uniform("onEdgeValue").set(onEdgeValue);
            },
            () -> {
                RenderSystem.disableBlend();
                RenderSystem.activeTexture(GL45C.GL_TEXTURE0);
                RenderSystem.bindTexture(0);
            }
    ));*/

    public static RenderPass colorTriangle() {
        return colorTriangle;
    }

    public static RenderPass textureTriangle() {
        return textureTriangle;
    }

    public static RenderPass colorTextureTriangle() {
        return colorTextureTriangle;
    }

    public static RenderPass colorTriangleFan() {
        return colorTriangleFan;
    }

    public static RenderPass colorRect() {
        return colorRect;
    }

    public static RenderPass rect() {
        return rect;
    }

    public static RenderPass textureRect() {
        return textureRect;
    }

    public static RenderPass colorTextureRect() {
        return colorTextureRect;
    }

    public static RenderPass colorLineStrip() {
        return colorLineStrip;
    }

    public static RenderPass text(int glyphAtlasTexture) {
        return text.get(glyphAtlasTexture);
    }

    /*public static RenderPass sdfText(int glyphAtlasTexture, int onEdgeValue) {
        final long encoded = ((long) glyphAtlasTexture << 32) | (onEdgeValue & 0xFFL);
        return sdfText.get(encoded);
    }*/

    public static RenderPass makeUnique(RenderPass pass) {
        return new UniqueRenderPass(pass);
    }

    public static <T> Function<T, RenderPass> memoize(Function<T, RenderPass> function) {
        return new Function<>() {
            private final Object2ObjectMap<T, RenderPass> cache = new Object2ObjectOpenHashMap<>();

            @Override
            public RenderPass apply(T t) {
                return this.cache.computeIfAbsent(t, function);
            }
        };
    }

    public static Float2ObjectFunction<RenderPass> memoize(Float2ObjectFunction<RenderPass> function) {
        return new Float2ObjectFunction<>() {
            private final Float2ObjectMap<RenderPass> cache = new Float2ObjectOpenHashMap<>();

            @Override
            public RenderPass get(float key) {
                return this.cache.computeIfAbsent(key, function);
            }
        };
    }

    public static Int2ObjectFunction<RenderPass> memoize(Int2ObjectFunction<RenderPass> function) {
        return new Int2ObjectFunction<>() {
            private final Int2ObjectMap<RenderPass> cache = new Int2ObjectOpenHashMap<>();

            @Override
            public RenderPass get(int key) {
                return this.cache.computeIfAbsent(key, function);
            }
        };
    }

    public static Long2ObjectFunction<RenderPass> memoize(Long2ObjectFunction<RenderPass> function) {
        return new Long2ObjectFunction<>() {
            private final Long2ObjectMap<RenderPass> cache = new Long2ObjectOpenHashMap<>();

            @Override
            public RenderPass get(long key) {
                return this.cache.computeIfAbsent(key, function);
            }
        };
    }

    public static <T, U> BiFunction<T, U, RenderPass> memoize(BiFunction<T, U, RenderPass> function) {
        return new BiFunction<>() {
            private final Object2ObjectMap<Pair<T, U>, RenderPass> cache = new Object2ObjectOpenHashMap<>();

            @Override
            public RenderPass apply(T t, U u) {
                return this.cache.computeIfAbsent(Pair.of(t, u), (Function<Pair<T, U>, RenderPass>) pair -> function.apply(pair.left(), pair.right()));
            }
        };
    }
}
