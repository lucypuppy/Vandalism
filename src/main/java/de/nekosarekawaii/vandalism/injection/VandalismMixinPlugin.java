package de.nekosarekawaii.vandalism.injection;

import net.lenni0451.reflect.stream.RStream;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class VandalismMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(final String mixinPackage) {
        final RStream loadedMixinsStream = RStream.of("org.spongepowered.asm.mixin.transformer.MixinConfig");
        final Set<String> loadedMixins = loadedMixinsStream.fields().by("globalMixinList").get();
        final String wurstClientMixinPackage = "net.wurstclient.mixin.";
        loadedMixins.add(wurstClientMixinPackage + "GameMenuScreenMixin");
        loadedMixins.add(wurstClientMixinPackage + "ShulkerBoxScreenMixin");
        loadedMixins.add(wurstClientMixinPackage + "GenericContainerScreenMixin");
        loadedMixins.add(wurstClientMixinPackage + "MultiplayerScreenMixin");
        loadedMixins.add(wurstClientMixinPackage + "TitleScreenMixin");
        loadedMixins.add(wurstClientMixinPackage + "DisconnectedScreenMixin");
        loadedMixins.add(wurstClientMixinPackage + "DisconnectedRealmsScreenMixin");
        loadedMixins.add(wurstClientMixinPackage + "StatsScreenMixin");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
    }

}
