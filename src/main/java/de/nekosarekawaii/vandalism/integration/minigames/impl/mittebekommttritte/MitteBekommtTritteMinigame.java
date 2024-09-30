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

package de.nekosarekawaii.vandalism.integration.minigames.impl.mittebekommttritte;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.integration.minigames.Minigame;
import de.nekosarekawaii.vandalism.integration.minigames.impl.mittebekommttritte.shoe.Shoe;
import de.nekosarekawaii.vandalism.integration.minigames.impl.mittebekommttritte.shoe.impl.DefaultShoe;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.Percentage;
import de.nekosarekawaii.vandalism.util.RandomUtils;
import de.nekosarekawaii.vandalism.util.SoundHooks;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import de.nekosarekawaii.vandalism.util.render.util.GLStateTracker;
import de.nekosarekawaii.vandalism.util.render.util.PlayerSkinRenderer;
import de.nekosarekawaii.vandalism.util.render.util.RenderUtil;
import imgui.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MitteBekommtTritteMinigame extends Minigame {

    private final String backgroundPath;
    private final String shoePath;
    private final int maxBackgrounds;
    private final List<Shoe> shoes;
    private final MSTimer hurtAnimationTimer;
    private final MSTimer levelUpTimer;
    private final ImBoolean commentary;
    private final ImBoolean blood;
    private final ImBoolean hurtAnimation;
    private final PlayerSkinRenderer playerSkinRenderer;

    private int count;
    private boolean clicked;
    private Color currentColor;
    private int alpha;
    private boolean levelUp;
    private int currentShoe;
    private boolean shop;

    public MitteBekommtTritteMinigame() {
        super(
                "Mitte bekommt Tritte",
                "GleichMitte bekommt Tritte in die Mitte.",
                "NekosAreKawaii"
        );
        final String texturePath = "textures/minigames/mitte_bekommt_tritte/";
        this.backgroundPath = texturePath + "background/";
        this.maxBackgrounds = 9;
        this.shoePath = texturePath + "shoe/";
        this.shoes = new ArrayList<>();
        this.shoes.add(new DefaultShoe("Waifulism Team"));
        this.shoes.add(new Shoe("Minecraft", 10));
        this.shoes.add(new Shoe("Minecraft 2", 50));
        this.shoes.add(new Shoe("Hello Kitty", 80));
        this.shoes.add(new Shoe("Fortnite", 100));
        this.shoes.add(new Shoe("Hello Kitty 2", 600));
        this.shoes.add(new Shoe("Linux", 1337));
        this.shoes.add(new Shoe("Minecraft 3", 3000));
        this.shoes.add(new Shoe("My Little Pony", 5000));
        this.shoes.add(new Shoe("Arch", 7000));
        this.shoes.add(new Shoe("Hello Kitty 3", 9000));
        this.shoes.add(new Shoe("Bob der Baumeister", 12000));
        this.shoes.add(new Shoe("Cars", 20000));
        this.shoes.add(new Shoe("Valorant", 50000));
        this.shoes.add(new Shoe("ALDI", 80000));
        this.shoes.add(new Shoe("Transformers", 100000));
        this.hurtAnimationTimer = new MSTimer();
        this.levelUpTimer = new MSTimer();
        this.commentary = new ImBoolean(true);
        this.blood = new ImBoolean(true);
        this.hurtAnimation = new ImBoolean(true);
        this.playerSkinRenderer = new PlayerSkinRenderer(UUID.fromString("e4314058-987e-41ee-93b0-33bebfe726a0"));
        this.count = 0;
        this.clicked = false;
        this.currentColor = null;
        this.alpha = 100;
        this.levelUp = false;
        this.currentShoe = 0;
        this.shop = false;
    }

    @Override
    public Pair<Integer, Boolean> getTextureId() {
        return new Pair<>(this.playerSkinRenderer.getGlId(), true);
    }

    @Override
    protected void playInfoSound() {
        SoundHooks.playGleichMitteZufrieden();
    }

    @Override
    protected long getInfoSoundPlayingTime() {
        return 7250;
    }

    @Override
    public void save(final JsonObject configNode) {
        configNode.addProperty("count", this.count);
        configNode.addProperty("commentary", this.commentary.get());
        configNode.addProperty("blood", this.blood.get());
        configNode.addProperty("hurtAnimation", this.hurtAnimation.get());
        final JsonArray shoeStates = new JsonArray();
        for (final Shoe shoe : this.shoes) {
            final JsonObject shoeState = new JsonObject();
            shoeState.addProperty("name", shoe.getName());
            shoeState.addProperty("unlocked", shoe.isUnlocked());
            shoeStates.add(shoeState);
        }
        configNode.add("shoesStates", shoeStates);
        final Shoe currentShoe = this.shoes.get(this.currentShoe);
        if (currentShoe != null) {
            configNode.addProperty("shoe", currentShoe.getName());
        }
    }

    @Override
    public void load(final JsonObject configNode) {
        if (configNode.has("count")) {
            this.count = configNode.get("count").getAsInt();
        }
        if (configNode.has("commentary")) {
            this.commentary.set(configNode.get("commentary").getAsBoolean());
        }
        if (configNode.has("blood")) {
            this.blood.set(configNode.get("blood").getAsBoolean());
        }
        if (configNode.has("hurtAnimation")) {
            this.hurtAnimation.set(configNode.get("hurtAnimation").getAsBoolean());
        }
        if (configNode.has("shoesStates")) {
            final JsonArray shoeStates = configNode.getAsJsonArray("shoesStates");
            for (int i = 0; i < shoeStates.size(); i++) {
                final JsonObject shoeState = shoeStates.get(i).getAsJsonObject();
                if (shoeState.has("name") && shoeState.has("unlocked")) {
                    for (final Shoe shoe : this.shoes) {
                        if (shoe.getName().equals(shoeState.get("name").getAsString())) {
                            shoe.setUnlocked(shoeState.get("unlocked").getAsBoolean());
                            break;
                        }
                    }
                }
            }
        }
        if (configNode.has("shoe")) {
            final String shoeName = configNode.get("shoe").getAsString();
            for (int i = 0; i < this.shoes.size(); i++) {
                final Shoe shoe = this.shoes.get(i);
                if (shoe.getName().equals(shoeName)) {
                    if (shoe.isUnlocked()) {
                        this.currentShoe = i;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onRender(final DrawContext context, final int mouseX, final int mouseY, final float startX, final float startY, final float endX, final float endY, final int width, final int height) {
        final String id = "##mitteBekommtTritte";
        final Matrix4f oldMatrix = RenderSystem.modelViewMatrix;
        ImGui.checkbox("Commentary" + id + "Commentary", this.commentary);
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text("GleichMitte will say something every 100th kick.");
            ImGui.endTooltip();
        }
        ImGui.sameLine();
        ImGui.checkbox("Blood" + id + "Blood", this.blood);
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text("GleichMitte will bleed when you kick him too often.");
            ImGui.endTooltip();
        }
        ImGui.sameLine();
        ImGui.checkbox("Hurt Animation" + id + "HurtAnimation", this.hurtAnimation);
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text("Will show a hurt animation when you kick GleichMitte.");
            ImGui.endTooltip();
        }
        ImGui.sameLine();
        if (ImGui.button((this.shop ? "Close Shop" : "Open Shop") + id + "Shop")) {
            this.shop = !this.shop;
        }
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(!this.shop ? "Open the shop where you can unlock or select other shoes." : "Close the shop to continue playing.");
            ImGui.endTooltip();
        }
        if (this.shop) {
            final Color shopBackground = Color.GRAY;
            context.setShaderColor(
                    shopBackground.getRed() / 255f,
                    shopBackground.getGreen() / 255f,
                    shopBackground.getBlue() / 255f,
                    shopBackground.getAlpha() / 255f
            );
        }
        final boolean isMouseInside = (mouseX >= startX && mouseX <= endX && (mouseY >= startY && mouseY <= endY)) && !this.shop;
        if (this.clicked && isMouseInside) {
            this.count++;
            this.currentColor = Color.RED;
            this.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_HURT, 1f));
            if (this.count > 0 && this.count % 10 == 0) {
                this.levelUp = true;
            }
            if (this.commentary.get() && this.count % 100 == 0) {
                switch (RandomUtils.randomInt(0, 3)) {
                    case 0 -> SoundHooks.playGleichMitteRichtigSauer();
                    case 1 -> SoundHooks.playGleichMitteScheisseMan();
                    case 2 -> SoundHooks.playGleichMitteDuHurensohn();
                    default -> SoundHooks.playGleichMitteFickDich();
                }
            }
        }
        if (this.levelUp && this.levelUpTimer.hasReached(10, true)) {
            this.levelUp = false;
            this.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_LEVELUP, this.count % 1000 == 0 ? 0.7f : this.count % 100 == 0 ? 0.8f : 1f));
        }
        final int maxScore = 5000;
        final float percentage = this.blood.get() ? Percentage.percentage(Math.min(maxScore, this.count), maxScore) : 0;
        final Identifier background = Identifier.of(FabricBootstrap.MOD_ID, this.backgroundPath + (int) (percentage / 100 * this.maxBackgrounds) + ".png");
        this.mc.getTextureManager().getTexture(background).setFilter(
                true,
                true
        );
        GLStateTracker.BLEND.save(true);
        context.drawTexture(background, 0, 0, 0, 0, width, height, width, height);
        GLStateTracker.BLEND.revert();
        if (this.currentColor != null) {
            if (this.hurtAnimationTimer.hasReached(15, true)) {
                this.alpha -= 9;
            }
            if (this.alpha <= 0) {
                this.currentColor = null;
                this.alpha = 100;
            } else if (this.hurtAnimation.get()) {
                context.fill(
                        0,
                        0,
                        width,
                        height,
                        ColorUtils.withAlpha(this.currentColor, this.alpha).getRGB()
                );
            }
        }
        final String kps = Formatting.WHITE.toString() + Vandalism.getInstance().getCpsTracker().getLeftClicks() + " « Tritte pro Sekunde";
        context.drawText(
                this.mc.textRenderer,
                kps,
                width - this.mc.textRenderer.getWidth(kps) - 2,
                height - 12,
                -1,
                false
        );
        int currentShoeX = width / 2, currentShoeY = height / 2;
        final int currentShoeMoveX = currentShoeX - 140;
        final int currentShoeMoveY = currentShoeY - 40;
        final boolean currentShoeMove = this.alpha < 100;
        if (currentShoeMove) {
            currentShoeX = currentShoeMoveX;
            currentShoeY = currentShoeMoveY;
        } else {
            if (isMouseInside) {
                currentShoeX = 3 * (mouseX / 10);
                if (currentShoeX < currentShoeMoveX) {
                    currentShoeX = currentShoeMoveX;
                }
                currentShoeY = 3 * (mouseY / 10);
                if (currentShoeY < currentShoeMoveY) {
                    currentShoeY = currentShoeMoveY;
                }
            }
            RenderSystem.modelViewMatrix = new Matrix4f();
            RenderSystem.modelViewMatrix.rotate((float) Math.toRadians(-20), 0, 0, 1);
        }
        final Identifier currentShoe = Identifier.of(FabricBootstrap.MOD_ID, this.shoePath + this.currentShoe + ".png");
        this.mc.getTextureManager().getTexture(currentShoe).setFilter(
                true,
                true
        );
        GLStateTracker.BLEND.save(true);
        context.drawTexture(currentShoe, currentShoeX, currentShoeY, 0, 0, width / 2, height / 3, width / 2, height / 3);
        GLStateTracker.BLEND.revert();
        RenderSystem.modelViewMatrix = oldMatrix;
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.drawText(
                this.mc.textRenderer,
                Formatting.WHITE + "Tritte in die Mitte » " + this.count,
                2,
                height - 12,
                -1,
                false
        );
        if (this.shop) {
            int x = 2;
            int y = 10;
            for (int i = 0; i < this.shoes.size(); i++) {
                final Shoe shoe = this.shoes.get(i);
                final Identifier shoeId = Identifier.of(FabricBootstrap.MOD_ID, this.shoePath + i + ".png");
                this.mc.getTextureManager().getTexture(shoeId).setFilter(
                        true,
                        true
                );
                GLStateTracker.BLEND.save(true);
                context.drawTexture(shoeId, x, y, 0, 0, width / 4, height / 5, width / 4, height / 5);
                GLStateTracker.BLEND.revert();
                final boolean isHovered = mouseX >= startX + x && mouseX <= startX + x + width / 4f && mouseY >= startY + y && mouseY <= startY + y + height / 5f;
                if (this.currentShoe != i) {
                    if (isHovered && this.clicked) {
                        if (shoe.isUnlocked()) {
                            this.currentShoe = i;
                            this.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
                        } else {
                            final int price = shoe.getPrice();
                            if (this.count >= price) {
                                this.count -= price;
                                shoe.setUnlocked(true);
                                this.currentShoe = i;
                                this.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_USE, 1f));
                            } else {
                                this.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_DESTROY, 1f));
                            }
                        }
                    }
                } else if (!shoe.isUnlocked()) {
                    this.currentShoe = 0;
                }
                final Color inColor = isHovered ? ColorUtils.withAlpha(Color.WHITE, 60) : ColorUtils.withAlpha(Color.BLACK, 50);
                final Color outColor = shoe.isUnlocked() ? ColorUtils.withAlpha(this.currentShoe != i ? Color.GREEN : Color.MAGENTA, 110) : ColorUtils.withAlpha(Color.RED, 100);
                RenderUtil.fillOutlined(context, x + 2, y, width / 4 + x, height / 5 + y, 2, inColor.getRGB(), outColor.getRGB());
                final String name = Formatting.WHITE + Formatting.BOLD.toString() + shoe.getName() + (this.currentShoe == i ? " (Selected)" : "");
                context.drawText(
                        this.mc.textRenderer,
                        name,
                        (x + width / 8) - this.mc.textRenderer.getWidth(name) / 2,
                        height / 5 + y - 80,
                        -1,
                        false
                );
                if (this.currentShoe != i) {
                    final String text = shoe.isUnlocked() ? Formatting.GREEN + "Select" : Formatting.RED + "Unlock for " + shoe.getPrice();
                    context.drawText(
                            this.mc.textRenderer,
                            text,
                            (x + width / 8) - this.mc.textRenderer.getWidth(text) / 2,
                            height / 5 + y + 2,
                            -1,
                            false
                    );
                }
                x += (int) ImUtils.modulateDimension(161);
                if (i % 4 == 3) {
                    x = 2;
                    y += 100;
                }
            }
        }
        this.clicked = false;
    }

    @Override
    public void mouseClicked(final double mouseX, final double mouseY, final int button, final boolean release) {
        if (release || button != this.mc.options.attackKey.boundKey.getCode()) {
            return;
        }
        this.clicked = true;
    }

    @Override
    public boolean keyPressed(final int key, final int scanCode, final int modifiers, final boolean release) {
        if (!release && key == this.mc.options.attackKey.boundKey.getCode()) {
            this.clicked = true;
        }
        return super.keyPressed(key, scanCode, modifiers, release);
    }

}
