/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.event.render.ScreenListener;
import de.nekosarekawaii.vandalism.render.effect.PostProcessEffect;
import de.nekosarekawaii.vandalism.render.effect.fill.ColorFillEffect;
import de.nekosarekawaii.vandalism.render.effect.fill.GaussianBlurFillEffect;
import de.nekosarekawaii.vandalism.render.effect.fill.Kawase4PassBlurFillEffect;
import de.nekosarekawaii.vandalism.render.effect.outline.FastOuterOutlineEffect;
import de.nekosarekawaii.vandalism.render.effect.outline.GlowOutlineEffect;
import de.nekosarekawaii.vandalism.render.effect.outline.InnerOutlineEffect;
import de.nekosarekawaii.vandalism.render.effect.outline.OuterOutlineEffect;
import de.nekosarekawaii.vandalism.render.gl.shader.Shader;
import de.nekosarekawaii.vandalism.render.gl.shader.ShaderProgram;
import de.nekosarekawaii.vandalism.render.gl.shader.ShaderType;
import de.nekosarekawaii.vandalism.util.math.DateUtil;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Shaders {

    private static boolean initialized;

    private static final List<ShaderProgram> shaders = new ArrayList<>();

    // Post-processing (just add the field here, and it will be automatically initialized)

    // Outlines
    @Getter private static OuterOutlineEffect outerOutlineEffect;
    @Getter private static FastOuterOutlineEffect fastOuterOutlineEffect;
    @Getter private static InnerOutlineEffect innerOutlineEffect;
    @Getter private static GlowOutlineEffect glowOutlineEffect;

    // Fills
    @Getter
    private static ColorFillEffect colorFillEffect;

    // Blur
    @Getter private static GaussianBlurFillEffect gaussianBlurFillEffect;
    @Getter private static Kawase4PassBlurFillEffect kawase4PassBlurFillEffect;

    @Getter private static ShaderProgram passThroughShader;
    private static final List<PostProcessEffect> postProcessEffects = new ArrayList<>();

    // General purpose shaders
    @Getter private static ShaderProgram positionShader;
    @Getter private static ShaderProgram instPositionShader;
    @Getter private static ShaderProgram positionColorShader;
    @Getter private static ShaderProgram instPositionColorShader;
    @Getter private static ShaderProgram positionTexShader;
    @Getter private static ShaderProgram instPositionTexShader;
    @Getter private static ShaderProgram positionTexColorShader;
    @Getter private static ShaderProgram instPositionTexColorShader;

    // Background
    @Getter
    private static ShaderProgram titleScreenBackgroundShader;
    @Getter
    private static ShaderProgram backgroundShader;
    @Getter
    private static ShaderProgram loadingScreenBackgroundShader;
    @Getter private static ShaderProgram ingameGuiBackgroundShader;

    // Font
    @Getter private static ShaderProgram fontShader;

    public static void loadAll() {
        if (initialized) return;
        initialized = true;

        shaders.add(passThroughShader = create("postprocess/passthrough", load(ShaderType.VERTEX, "postprocess/postprocess"), load(ShaderType.FRAGMENT, "postprocess/passthrough")));

        shaders.add(positionShader = create("position", load(ShaderType.VERTEX, "general_purpose/position"), load(ShaderType.FRAGMENT, "general_purpose/position")));
        shaders.add(instPositionShader = create("inst_position", load(ShaderType.VERTEX, "general_purpose/inst_position"), load(ShaderType.FRAGMENT, "general_purpose/position")));
        shaders.add(positionColorShader = create("position_color", load(ShaderType.VERTEX, "general_purpose/position_color"), load(ShaderType.FRAGMENT, "general_purpose/position_color")));
        shaders.add(instPositionColorShader = create("inst_position_color", load(ShaderType.VERTEX, "general_purpose/inst_position_color"), load(ShaderType.FRAGMENT, "general_purpose/position_color")));
        shaders.add(positionTexShader = create("position_tex", load(ShaderType.VERTEX, "general_purpose/position_tex"), load(ShaderType.FRAGMENT, "general_purpose/position_tex")));
        shaders.add(instPositionTexShader = create("inst_position_tex", load(ShaderType.VERTEX, "general_purpose/inst_position_tex"), load(ShaderType.FRAGMENT, "general_purpose/position_tex")));
        shaders.add(positionTexColorShader = create("position_tex_color", load(ShaderType.VERTEX, "general_purpose/position_tex_color"), load(ShaderType.FRAGMENT, "general_purpose/position_tex_color")));
        shaders.add(instPositionTexColorShader = create("inst_position_tex_color", load(ShaderType.VERTEX, "general_purpose/inst_position_tex_color"), load(ShaderType.FRAGMENT, "general_purpose/position_tex_color")));

        shaders.add(fontShader = create("font", load(ShaderType.VERTEX, "font/font"), load(ShaderType.FRAGMENT, "font/font")));


        shaders.add(titleScreenBackgroundShader = create("title_screen_background", load(ShaderType.VERTEX, "vertex/vertex"), load(ShaderType.FRAGMENT, "fragment/background/" + (DateUtil.isBirthday() ? "title_screen_birthday" : "title_screen_default"))));
        shaders.add(backgroundShader = create("background", load(ShaderType.VERTEX, "vertex/vertex"), load(ShaderType.FRAGMENT, "fragment/background/default")));
        shaders.add(loadingScreenBackgroundShader = create("loading_screen_background", load(ShaderType.VERTEX, "vertex/vertex"), load(ShaderType.FRAGMENT, "fragment/background/loading_screen")));
        shaders.add(ingameGuiBackgroundShader = create("ingame_gui_background", load(ShaderType.VERTEX, "vertex/vertex"), load(ShaderType.FRAGMENT, "fragment/background/ingame_gui")));

        for (Field field : Shaders.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            if (PostProcessEffect.class.isAssignableFrom(field.getType())) {
                try {
                    final PostProcessEffect effect = (PostProcessEffect) field.getType().newInstance();
                    field.set(null, effect);
                    postProcessEffects.add(effect);
                    Vandalism.getInstance().getEventSystem().subscribe(ScreenListener.ScreenEvent.ID, new ScreenListener() {
                        @Override
                        public void onResizeScreen(ScreenEvent event) {
                            final MinecraftClient mc = MinecraftClient.getInstance();
                            effect.resizeBuffers(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
                        }
                    });
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to initialize post-process effect " + field.getType().getSimpleName(), e);
                }
            }
        }
        postProcessEffects.forEach(PostProcessEffect::initialize);
    }

    public static void unloadAll() {
        if (!initialized) return;
        initialized = false;
        for (PostProcessEffect effect : postProcessEffects) {
            effect.close();
            Vandalism.getInstance().getEventSystem().unsubscribe(effect, ScreenListener.ScreenEvent.ID);
        }
        postProcessEffects.clear();
        for (ShaderProgram shader : shaders) {
            shader.close();
        }
        shaders.clear();
    }

    public static Shader load(ShaderType type, String name) {
        try {
            return Shader.compile(type, getShaderSource(type, name));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader " + name, e);
        }
    }

    public static ShaderProgram create(String name, Shader... shaders) {
        try {
            return ShaderProgram.compose(shaders);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create shader program " + name, e);
        }
    }

    public static String getShaderSource(ShaderType type, String name) throws IOException {
        final String extension = switch (type) {
            case VERTEX -> "vert";
            case FRAGMENT -> "frag";
            case GEOMETRY -> "geom";
            case TESS_CONTROL -> "tesc";
            case TESS_EVALUATION -> "tese";
            case COMPUTE -> "comp";
        };
        try (final InputStream input = Shaders.class.getResourceAsStream("/assets/" + FabricBootstrap.MOD_ID + "/shader/" + name + "." + extension)) {
            if (input == null) throw new IOException("Shader " + name + " not found");
            return new String(input.readAllBytes());
        }
    }
}
