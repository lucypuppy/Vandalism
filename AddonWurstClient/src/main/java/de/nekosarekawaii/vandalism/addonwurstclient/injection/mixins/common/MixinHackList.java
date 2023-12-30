package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.common;

import net.wurstclient.hack.HackList;
import net.wurstclient.hacks.HealthTagsHack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.TreeMap;

@Mixin(value = HackList.class, remap = false)
public abstract class MixinHackList {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/TreeMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object removeSomeWurstHacks(TreeMap instance, Object key, Object value) {
        if (value.getClass().equals(HealthTagsHack.class)) {
            return value;
        }
        return instance.put(key, value);
    }

}
