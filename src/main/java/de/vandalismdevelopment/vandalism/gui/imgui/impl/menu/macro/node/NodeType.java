package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro.node;

import de.vandalismdevelopment.vandalism.util.interfaces.EnumNameNormalizer;

public enum NodeType implements EnumNameNormalizer {

    ON_ENABLE, ON_DISABLE, SEND_CHAT_MESSAGE;

    private final String normalName;

    NodeType() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}
