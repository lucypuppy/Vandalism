package de.vandalismdevelopment.vandalism.gui.imgui.impl.widget.serverinfo;

import de.vandalismdevelopment.vandalism.util.interfaces.EnumNameNormalizer;

public enum PlayersTableColumn implements EnumNameNormalizer {

    USERNAME, UUID, ACTIONS;

    private final String normalName;

    PlayersTableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}