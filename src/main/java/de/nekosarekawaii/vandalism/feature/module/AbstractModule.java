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

package de.nekosarekawaii.vandalism.feature.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.SeparatorValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.internal.ModuleToggleListener;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleModeValue;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.game.SoundHooks;
import lombok.Getter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialoader.util.VersionRange;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModule extends Feature implements ValueParent {

    public static final String EXPERIMENTAL_WARNING_TEXT = "Warning: This module is experimental and may not work as expected.";
    public static final String SUPPORTED_VERSIONS_TEXT = "Supported Versions:";

    private final List<Value<?>> values = new ArrayList<>();

    @Getter
    private final List<Value<?>> defaultValues;

    private final BooleanValue active = new BooleanValue(
            this,
            "Active",
            "Whether this module is active.",
            false
    );

    private final SeparatorValue defaultSettingsSeparator = new SeparatorValue(
            this,
            "Default Settings",
            "Default settings of this module."
    );

    private final ValueGroup defaultSettings = new ValueGroup(
            this,
            "Default Settings",
            "Default settings of this module."
    );

    private final BooleanValue favorite = new BooleanValue(
            this.defaultSettings,
            "Favorite",
            "Whether this module is a favorite.",
            false
    );

    private final BooleanValue showInHUD = new BooleanValue(
            this.defaultSettings,
            "Show in HUD",
            "Whether this module should be shown in the HUD.",
            true
    ).onValueChange((oldValue, newValue) -> {
        Vandalism.getInstance().getHudManager().moduleListHUDElement.markForSorting();
    });

    private final BooleanValue moduleStateLogging = new BooleanValue(
            this.defaultSettings,
            "Module State Logging",
            "Activates/Deactivates the logging for the module state in this module.",
            true
    ).visibleCondition(() -> Vandalism.getInstance().getClientSettings().getChatSettings().moduleStateLogging.getValue());

    private final BooleanValue moduleStateSound = new BooleanValue(
            this.defaultSettings,
            "Module State Sound",
            "Activates/Deactivates the sound for the module state in this module.",
            true
    ).visibleCondition(() -> Vandalism.getInstance().getClientSettings().getMenuSettings().moduleStateSound.getValue());

    @Getter
    private final KeyBindValue keyBind = new KeyBindValue(
            this.defaultSettings,
            "Key Bind",
            "The key bind of this module."
    );

    private final ValueGroup deactivationSettings = new ValueGroup(
            this,
            "Deactivation Settings",
            "Deactivation settings of this module."
    );

    private final BooleanValue deactivateOnQuit = new BooleanValue(
            this.deactivationSettings,
            "Deactivate on Quit",
            "Whether this module should be deactivated on quit.",
            false
    );

    private final BooleanValue deactivateOnShutdown = new BooleanValue(
            this.deactivationSettings,
            "Deactivate on Shutdown",
            "Whether this module should be deactivated on shutdown.",
            false
    );

    private final BooleanValue deactivateOnWorldLoad = new BooleanValue(
            this.deactivationSettings,
            "Deactivate on World Load",
            "Whether this module should be deactivated on world load.",
            false
    );

    private final BooleanValue deactivateOnDeath = new BooleanValue(
            this.deactivationSettings,
            "Deactivate on Death",
            "Whether this module should be deactivated on death.",
            false
    );

    private final BooleanValue deactivateOnRelease = new BooleanValue(
            this.deactivationSettings,
            "Deactivate on Release",
            "Whether this module should be deactivated on key release.",
            false
    );

    private final SeparatorValue settingsSeparator = new SeparatorValue(
            this,
            "Settings",
            "Settings of this module."
    );

    public AbstractModule(final String name, final String description, final Category category) {
        this(name, description, category, null);
    }

    public AbstractModule(final String name, final String description, final Category category, final VersionRange supportedVersions) {
        super(name, description, category, supportedVersions);
        this.defaultValues = new ArrayList<>(this.values);
        this.defaultValues.remove(this.deactivationSettings);

        this.active.onValueChange((oldValue, newValue) -> {
            final ModuleToggleListener.ModuleToggleEvent event = new ModuleToggleListener.ModuleToggleEvent(this, newValue);
            Vandalism.getInstance().getEventSystem().callExceptionally(ModuleToggleListener.ModuleToggleEvent.ID, event);
            // Allows the event to change the active state of the module
            // It's important that people don't use the setActive method from the module itself in the event
            // because that would cause an infinite loop
            newValue = event.active;
            if (oldValue != newValue) {
                if (newValue) {
                    this.onActivate();
                } else {
                    this.onDeactivate();
                }
                final ClientSettings clientSettings = Vandalism.getInstance().getClientSettings();
                if (this.mc.player != null) {
                    if (clientSettings.getChatSettings().moduleStateLogging.getValue() && this.moduleStateLogging.getValue()) {
                        final MutableText text = Text.literal(Formatting.DARK_AQUA + this.getName() + Formatting.GRAY + " has been ");
                        final MutableText state = newValue ? Text.literal("activated") : Text.literal("deactivated");
                        state.withColor(newValue ? Color.GREEN.getRGB() : Color.RED.getRGB());
                        text.append(state);
                        ChatUtil.chatMessage(text, true, true);
                    }
                    if (clientSettings.getMenuSettings().moduleStateSound.getValue() && this.moduleStateSound.getValue()) {
                        if (newValue) SoundHooks.playModuleActivate();
                        else SoundHooks.playModuleDeactivate();
                    }
                }
                this.recursiveUpdateActiveState(newValue, this.values);
            }
        });
    }

    public void activateDefault() {
        this.active.setValue(true);
    }

    public void deactivateAfterSessionDefault() {
        this.deactivateOnQuitDefault();
        this.deactivateOnShutdownDefault();
        this.deactivateOnWorldLoadDefault();
        this.deactivateOnDeathDefault();
    }

    public void deactivateOnQuitDefault() {
        this.deactivateOnQuit.setValue(true);
    }

    public void deactivateOnShutdownDefault() {
        this.deactivateOnShutdown.setValue(true);
    }

    public void deactivateOnWorldLoadDefault() {
        this.deactivateOnWorldLoad.setValue(true);
    }

    public void deactivateOnDeathDefault() {
        this.deactivateOnDeath.setValue(true);
    }

    public void deactivateOnReleaseDefault() {
        this.deactivateOnRelease.setValue(true);
    }

    protected void onActivate() {
    }

    protected void onDeactivate() {
    }

    public boolean isActive() {
        return this.active.getValue();
    }

    private void setActive(final boolean active) {
        if (this.active.getValue() == active) return;
        this.active.setValue(active);
    }

    public void activate() {
        this.setActive(true);
    }

    public void deactivate() {
        this.setActive(false);
    }

    public void toggle() {
        this.setActive(!this.isActive());
    }

    public boolean isFavorite() {
        return this.favorite.getValue();
    }

    public boolean isShowInHUD() {
        return this.showInHUD.getValue();
    }

    public boolean isDeactivateOnQuit() {
        return this.deactivateOnQuit.getValue();
    }

    public boolean isDeactivateOnShutdown() {
        return this.deactivateOnShutdown.getValue();
    }

    public boolean isDeactivateOnWorldLoad() {
        return this.deactivateOnWorldLoad.getValue();
    }

    public boolean isDeactivateOnDeath() {
        return this.deactivateOnDeath.getValue();
    }

    public boolean isDeactivateOnRelease() {
        return this.deactivateOnRelease.getValue();
    }

    private void recursiveUpdateActiveState(final boolean active, final List<Value<?>> values) {
        if (values == null) return;
        for (final Value<?> value : values) {
            if (value instanceof final ValueGroup valueGroup) {
                recursiveUpdateActiveState(active, valueGroup.getValues());
            } else if (value instanceof final ModuleModeValue<?> moduleModeValue) {
                if (active) {
                    moduleModeValue.getValue().onActivate();
                } else {
                    moduleModeValue.getValue().onDeactivate();
                }
            }
        }
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

}
