package de.vandalismdevelopment.vandalism.gui.imgui;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.*;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.account.AccountManagerImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.namehistory.NameHistoryImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.nbteditor.NbtEditortImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.port.PortScannerImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script.ScriptsImGuiMenu;

import java.util.ArrayList;
import java.util.List;

public class ImGuiMenuRegistry {

    private final List<ImGuiMenu> imGuiMenus;

    public ImGuiMenuRegistry() {
        this.imGuiMenus = new ArrayList<>();
        this.registerImGuiMenus(
                new DemoImGuiMenu(),
                new ConfigImGuiMenu(),
                new ModulesImGuiMenu(),
                //TODO: Finish this -> new MacrosImGuiMenu(),
                new ScriptsImGuiMenu(),
                new CustomHUDImGuiMenu(),
                new AccountManagerImGuiMenu(),
                new ServerPingerImGuiMenu(),
                new PortScannerImGuiMenu(),
                new ServerAddressResolverImGuiMenu(),
                new NameHistoryImGuiMenu(),
                new BugScraperImGuiMenu(),
                new NbtEditortImGuiMenu()
        );
    }

    private void registerImGuiMenus(final ImGuiMenu... imGuiMenus) {
        Vandalism.getInstance().getLogger().info("Registering imgui menus...");
        for (final ImGuiMenu imGuiMenu : imGuiMenus) {
            if (!this.imGuiMenus.contains(imGuiMenu)) {
                this.imGuiMenus.add(imGuiMenu);
                Vandalism.getInstance().getLogger().info("ImGui Menu '" + imGuiMenu + "' has been registered.");
            } else {
                Vandalism.getInstance().getLogger().error("Duplicated imgui menu found: " + imGuiMenu);
            }
        }
        final int imGuiMenuListSize = this.imGuiMenus.size();
        if (imGuiMenuListSize < 1) Vandalism.getInstance().getLogger().info("No imgui menus found!");
        else Vandalism.getInstance().getLogger().info("Registered " + imGuiMenuListSize + " imGui menus.");
    }

    public List<ImGuiMenu> getImGuiMenus() {
        return this.imGuiMenus;
    }

    public <M extends ImGuiMenu> M getImGuiMenuByClass(final Class<M> clazz) {
        for (final ImGuiMenu imGuiMenu : this.imGuiMenus) {
            if (imGuiMenu.getClass().equals(clazz)) {
                return (M) imGuiMenu;
            }
        }
        return null;
    }

}
