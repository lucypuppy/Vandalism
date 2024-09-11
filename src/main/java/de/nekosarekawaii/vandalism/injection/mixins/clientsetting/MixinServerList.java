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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(ServerList.class)
public abstract class MixinServerList {

    @Shadow
    @Final
    private List<ServerInfo> servers;

    @Inject(method = "loadFile", at = @At(value = "RETURN"))
    private void enhancedServerListSyncServerListSizeOnLoad(final CallbackInfo ci) {
        this.vandalism$enhancedServerListSyncServerList();
    }

    @Inject(method = "saveFile", at = @At(value = "RETURN"))
    private void enhancedServerListUpdateServerListSizeOnSave(final CallbackInfo ci) {
        this.vandalism$enhancedServerListSyncServerList();
    }

    @ModifyArgs(method = "loadFile", at = @At(value = "INVOKE", target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;"))
    private void enhancedServerListChangeLoadFileName(final Args args) {
        args.set(0, this.vandalism$enhancedServerListGetSelectedServerListName() + ".dat");
    }

    @ModifyArgs(method = "saveFile", at = @At(value = "INVOKE", target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;", ordinal = 0))
    private void enhancedServerListChangeOldSaveFileName(final Args args) {
        args.set(0, this.vandalism$enhancedServerListGetSelectedServerListName() + ".dat_old");
    }

    @ModifyArgs(method = "saveFile", at = @At(value = "INVOKE", target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;", ordinal = 1))
    private void enhancedServerListChangeSaveFileName(final Args args) {
        args.set(0, this.vandalism$enhancedServerListGetSelectedServerListName() + ".dat");
    }

    @Unique
    private String vandalism$enhancedServerListGetSelectedServerListName() {
        return Vandalism.getInstance().getServerListManager().getSelectedServerList().getName();
    }

    @Unique
    private void vandalism$enhancedServerListSyncServerList() {
        Vandalism.getInstance().getServerListManager().get(this.vandalism$enhancedServerListGetSelectedServerListName()).setSize(this.servers.size());
        Vandalism.getInstance().getServerListManager().saveConfig();
    }

}
