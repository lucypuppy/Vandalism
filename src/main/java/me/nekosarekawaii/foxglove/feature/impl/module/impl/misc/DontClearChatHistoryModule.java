package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import me.nekosarekawaii.foxglove.event.impl.PacketListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;

@ModuleInfo(name = "Dont Clear Chat History", description = "Prevents the Game from clearing your chat history.", category = FeatureCategory.MISC)
public class DontClearChatHistoryModule extends Module implements PacketListener {
}
