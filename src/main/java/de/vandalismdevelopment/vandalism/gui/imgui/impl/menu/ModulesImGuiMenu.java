package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class ModulesImGuiMenu extends ImGuiMenu {

    public ModulesImGuiMenu() {
        super("Modules");
    }

    @Override
    public void render() {
        if (ImGui.begin("Modules", ImGuiWindowFlags.NoCollapse)) {
            if (ImGui.beginTabBar("modulesTabBar##modulestabbar")) {
                final FeatureList<Module> modules = Vandalism.getInstance().getModuleRegistry().getModules();
                if (!modules.isEmpty()) {
                    for (final FeatureCategory featureCategory : FeatureCategory.values()) {
                        final FeatureList<Module> modulesByCategory = modules.get(featureCategory);
                        if (modulesByCategory.isEmpty()) continue;
                        if (ImGui.beginTabItem(featureCategory.normalName() + " Modules##modulesfeaturecategory")) {
                            //TODO: Make performance improvement to this code with something like caching.
                            final List<Module> enabledModules = new ArrayList<>(), disabledModules = new ArrayList<>();
                            for (final Module module : modulesByCategory) {
                                if (module.isEnabled()) enabledModules.add(module);
                                else disabledModules.add(module);
                            }
                            if (!enabledModules.isEmpty()) {
                                ImGui.newLine();
                                ImGui.text("Enabled Modules (" + enabledModules.size() + ")");
                                for (final Module module : enabledModules) {
                                    this.renderModule(module);
                                }
                            }
                            if (!disabledModules.isEmpty()) {
                                ImGui.newLine();
                                ImGui.text("Disabled Modules (" + disabledModules.size() + ")");
                                for (final Module module : disabledModules) {
                                    this.renderModule(module);
                                }
                            }
                            ImGui.endTabItem();
                        }
                    }
                }
                ImGui.endTabBar();
            }
            ImGui.end();
        }
    }

    private void renderModule(final Module module) {
        if (ImGui.collapsingHeader(module.getName() + "##modules" + module.getCategory().normalName() + "module")) {
            final String moduleIdent = "##" + module.getName() + module.getCategory().normalName() + "module";
            final List<Value<?>> values = module.getValues();
            if (module.isExperimental()) {
                ImGui.textColored(0.8f, 0.1f, 0.1f, 1f, "Warning this is a experimental module which can have issues!");
            }
            if (ImGui.button("Reset Config" + moduleIdent)) {
                for (final Value<?> value : values) {
                    value.resetValue();
                }
            }
            if (!values.isEmpty()) {
                ImGui.separator();
                ImGui.newLine();
            }
            module.renderValues();
            ImGui.newLine();
        }
    }

}
