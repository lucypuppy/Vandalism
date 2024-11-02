/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.command;

import net.wurstclient.command.Command;
import net.wurstclient.commands.FriendsCmd;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.Setting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FriendsCmd.class)
public abstract class MixinFriendsCmd extends Command {

    @Unique
    private static final CheckboxSetting vandalism$MIDDLE_CLICK_FRIENDS = new CheckboxSetting(
            "Middle click friends",
            "Add/remove friends by clicking them with the middle mouse button.",
            false
    );

    public MixinFriendsCmd(final String name, final String description, final String... syntax) {
        super(name, description, syntax);
    }

    @Redirect(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/wurstclient/commands/FriendsCmd;addSetting(Lnet/wurstclient/settings/Setting;)V"), remap = false)
    private void forceWurstMiddleClickFriendsDefaultDisabled(final FriendsCmd instance, final Setting setting) {
        this.addSetting(vandalism$MIDDLE_CLICK_FRIENDS);
    }

    @Inject(method = "getMiddleClickFriends", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void returnNewMiddleClickFriends(final CallbackInfoReturnable<CheckboxSetting> cir) {
        cir.setReturnValue(vandalism$MIDDLE_CLICK_FRIENDS);
    }

}
