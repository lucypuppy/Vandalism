package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import net.wurstclient.other_feature.OtfList;
import net.wurstclient.other_features.HackListOtf;
import net.wurstclient.other_features.ZoomOtf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.TreeMap;

@Mixin(value = OtfList.class, remap = false)
public abstract class MixinOtfList {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/TreeMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object removeSomeWurstOtfs(TreeMap instance, Object key, Object value) {
        if (value.getClass().equals(HackListOtf.class) || value.getClass().equals(ZoomOtf.class)) {
            return value;
        }
        return instance.put(key, value);
    }

}
