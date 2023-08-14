package me.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node;

import me.nekosarekawaii.foxglove.util.string.EnumNameNormalizer;

public enum NodeTypes implements EnumNameNormalizer {

    ON_ENABLE, ON_DISABLE, SEND_CHAT_MESSAGE;

    private final String normalName;

    NodeTypes() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}
