package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.port;

public enum PortsTableColumn implements EnumNameNormalizer {

    PORT, STATE, QUERY_STATE, ACTIONS;

    private final String normalName;

    PortsTableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}