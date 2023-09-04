package de.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;

public class MessageEncryptorModule extends Module {

    public MessageEncryptorModule() {
        super(
                "Message Encryptor",
                "This module encrypts your chat messages.",
                FeatureCategory.MISC,
                false,
                false
        );
    }

}
