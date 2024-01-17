/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.base.event.normal.internal.ModuleToggleListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.awt.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.raphimc.vialoader.util.VersionRange;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModule extends Feature implements ValueParent {

    public static final String EXPERIMENTAL_WARNING_TEXT = "Warning: This module is experimental and may not work as expected.";
    public static final String SUPPORTED_VERSIONS_TEXT = "Supported Versions:";

    private final List<Value<?>> values = new ArrayList<>();

    private final BooleanValue active;
    private final BooleanValue favorite;
    private final BooleanValue showInHUD;
    private final KeyBindValue keyBind;

    private boolean deactivateOnQuit = false;
    private boolean deactivateOnShutdown = false;

    public AbstractModule(String name, String description, Category category) {
        this(name, description, category, null);
    }

    public AbstractModule(String name, String description, Category category, VersionRange supportedVersions) {
        super(name, description, category, supportedVersions);
        this.active = new BooleanValue(
                this,
                "Active",
                "Whether this module is active.",
                false
        ).onValueChange((oldValue, newValue) -> {
            final var event = new ModuleToggleListener.ModuleToggleEvent(this, newValue);
            Vandalism.getInstance().getEventSystem().postInternal(ModuleToggleListener.ModuleToggleEvent.ID, event);
            //Allows the event to change the active state of the module
            //It's important that people don't use the setActive method from the module itself in the event
            //because that would cause an infinite loop
            newValue = event.active;
            if (oldValue != newValue) {
                if (newValue) {
                    this.onActivate();
                } else {
                    this.onDeactivate();
                }
                if (Vandalism.getInstance().getClientSettings().getMenuSettings().moduleStateLogging.getValue() && this.mc.player != null) {
                    ChatUtil.infoChatMessage(this.getName() + " has been " + (newValue ? "activated" : "deactivated") + ".");
                }
                this.recursiveUpdateActiveState(newValue, this.values);
            }
        });
        this.favorite = new BooleanValue(
                this,
                "Favorite",
                "Whether this module is a favorite.",
                false
        );
        this.showInHUD = new BooleanValue(
                this,
                "Show in HUD",
                "Whether this module should be shown in the HUD.",
                true
        );
        this.keyBind = new KeyBindValue(
                this,
                "Key Bind",
                "The key bind of this module."
        );
    }

    public void activateDefault() {
        this.active.setValue(true);
    }

    public void deactivateAfterSession() {
        this.deactivateOnQuit();
        this.deactivateOnShutdown();
    }

    public void deactivateOnQuit() {
        this.deactivateOnQuit = true;
    }

    public void deactivateOnShutdown() {
        this.deactivateOnShutdown = true;
    }

    public void onActivate() {
    }

    public void onDeactivate() {
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

    public KeyBindValue getKeyBind() {
        return keyBind;
    }

    public boolean isDeactivateOnQuit() {
        return deactivateOnQuit;
    }

    public boolean isDeactivateOnShutdown() {
        return deactivateOnShutdown;
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
