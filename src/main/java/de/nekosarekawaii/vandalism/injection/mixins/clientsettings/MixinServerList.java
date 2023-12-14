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

    @Unique
    private String vandalism$enhancedServerListGetSelectedServerListName() {
        return Vandalism.getInstance().getServerListManager().getSelectedServerList().getName();
    }

    @Unique
    private void vandalism$enhancedServerListSyncServerList() {
        Vandalism.getInstance().getServerListManager().get(this.vandalism$enhancedServerListGetSelectedServerListName()).setSize(this.servers.size());
        Vandalism.getInstance().getServerListManager().saveConfig();
    }

    @Inject(method = "loadFile", at = @At(value = "RETURN"))
    private void vandalism$enhancedServerListSyncServerListSizeOnLoad(final CallbackInfo ci) {
        this.vandalism$enhancedServerListSyncServerList();
    }

    @Inject(method = "saveFile", at = @At(value = "RETURN"))
    private void vandalism$enhancedServerListUpdateServerListSizeOnSave(final CallbackInfo ci) {
        this.vandalism$enhancedServerListSyncServerList();
    }

    @ModifyArgs(method = "loadFile", at = @At(value = "INVOKE", target = "Ljava/io/File;<init>(Ljava/io/File;Ljava/lang/String;)V"))
    private void vandalism$enhancedServerListChangeLoadFileName(final Args args) {
        args.set(1, this.vandalism$enhancedServerListGetSelectedServerListName() + ".dat");
    }

    @ModifyArgs(method = "saveFile", at = @At(value = "INVOKE", target = "Ljava/io/File;createTempFile(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;)Ljava/io/File;"))
    private void vandalism$enhancedServerListChangeTempSaveFileName(final Args args) {
        args.set(0, this.vandalism$enhancedServerListGetSelectedServerListName());
    }

    @ModifyArgs(method = "saveFile", at = @At(value = "INVOKE", target = "Ljava/io/File;<init>(Ljava/io/File;Ljava/lang/String;)V", ordinal = 0))
    private void vandalism$enhancedServerListChangeOldSaveFileName(final Args args) {
        args.set(1, this.vandalism$enhancedServerListGetSelectedServerListName() + ".dat_old");
    }

    @ModifyArgs(method = "saveFile", at = @At(value = "INVOKE", target = "Ljava/io/File;<init>(Ljava/io/File;Ljava/lang/String;)V", ordinal = 1))
    private void vandalism$enhancedServerListChangeSaveFileName(final Args args) {
        args.set(1, this.vandalism$enhancedServerListGetSelectedServerListName() + ".dat");
    }

}
