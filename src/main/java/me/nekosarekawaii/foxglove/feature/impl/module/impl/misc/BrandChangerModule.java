package me.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import me.nekosarekawaii.foxglove.event.impl.PacketListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.StringValue;
import net.minecraft.client.ClientBrandRetriever;

@ModuleInfo(name = "Brand Changer", description = "Changes the brand that the Client sends to the Server.", category = FeatureCategory.MISC, isDefaultEnabled = true)
public class BrandChangerModule extends Module implements PacketListener {

    public final Value<String> brand = new StringValue("Brand", "The Brand that will used.", this, ClientBrandRetriever.VANILLA);

}
