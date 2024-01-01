package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.common;

import de.nekosarekawaii.vandalism.addonwurstclient.hack.WurstClientOptionsHack;
import net.wurstclient.hack.Hack;
import net.wurstclient.hack.HackList;
import net.wurstclient.hacks.HealthTagsHack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.TreeMap;

@Mixin(value = HackList.class, remap = false)
public abstract class MixinHackList {

    @Shadow
    @Final
    private TreeMap<String, Hack> hax;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addSomeWurstHacks(final Path enabledHacksFile, final CallbackInfo ci) {
        this.hax.put("Options", new WurstClientOptionsHack());
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/TreeMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object removeSomeWurstHacks(TreeMap instance, Object key, Object value) {
        if (value.getClass().equals(HealthTagsHack.class)) {
            return value;
        }
        return instance.put(key, value);
    }

}
