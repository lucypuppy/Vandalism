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

@Mixin(value = FriendsCmd.class, remap = false)
public abstract class MixinFriendsCmd extends Command {

    @Unique
    private static final CheckboxSetting vandalism_MIDDLE_CLICK_FRIENDS = new CheckboxSetting(
            "Middle click friends",
            "Add/remove friends by clicking them with the middle mouse button.",
            false
    );

    public MixinFriendsCmd(final String name, final String description, final String... syntax) {
        super(name, description, syntax);
    }

    @Redirect(method = "<init>()V", at = @At(value = "INVOKE", target = "Lnet/wurstclient/commands/FriendsCmd;addSetting(Lnet/wurstclient/settings/Setting;)V"))
    private void forceWurstMiddleClickFriendsDefaultDisabled(FriendsCmd instance, Setting setting) {
        this.addSetting(vandalism_MIDDLE_CLICK_FRIENDS);
    }

    @Inject(method = "getMiddleClickFriends", at = @At(value = "RETURN"), cancellable = true)
    private void returnNewMiddleClickFriends(final CallbackInfoReturnable<CheckboxSetting> cir) {
        cir.setReturnValue(vandalism_MIDDLE_CLICK_FRIENDS);
    }

}
