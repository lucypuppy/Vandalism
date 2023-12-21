package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

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
