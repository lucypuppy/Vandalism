package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.ScriptParser;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.command.ScriptCommand;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.IScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.ScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.BooleanScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.CategoryScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.StringScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.bool.ExperimentalScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.string.AuthorScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.string.DescriptionScriptInfo;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ConfigImGuiMenu extends ImGuiMenu {

    public ConfigImGuiMenu() {
        super("Config");
    }

    @Override
    public void render() {
        if (ImGui.begin("Config", ImGuiWindowFlags.NoCollapse)) {
            if (ImGui.beginTabBar("configTabBar")) {
                if (ImGui.beginTabItem("Main##mainconfigtabitem")) {
                    final MainConfig mainConfigValues = Vandalism.getInstance().getConfigManager().getMainConfig();
                    if (ImGui.button("Reset Main Config##mainconfigresetbutton")) {
                        for (final Value<?> value : mainConfigValues.getValues()) {
                            value.resetValue();
                        }
                    }
                    ImGui.separator();
                    mainConfigValues.renderValues();
                    ImGui.endTabItem();
                }
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
            if (ImGui.checkbox("Enabled" + moduleIdent, module.isEnabled())) {
                module.toggle();
            }
            ImGui.sameLine();
            final ImBoolean showInModuleList = new ImBoolean(module.isShowInModuleList());
            if (ImGui.checkbox("Show in Enabled Module List" + moduleIdent, showInModuleList)) {
                module.setShowInModuleList(showInModuleList.get());
            }
            ImGui.sameLine();
            if (ImGui.button("Reset Config" + moduleIdent)) {
                for (final Value<?> value : values) {
                    value.resetValue();
                }
            }
            final File toggleScriptFile = new File(Vandalism.getInstance().getScriptRegistry().getDirectory(), "Toggle-" + module.getName() + "-Module" + ScriptParser.SCRIPT_FILE_EXTENSION);
            if (!toggleScriptFile.exists()) {
                ImGui.sameLine();
                if (ImGui.button("Generate Toggle Script" + moduleIdent)) {
                    try {
                        final PrintWriter printerWriter = new PrintWriter(toggleScriptFile);
                        for (final ScriptInfo scriptInfo : ScriptInfo.values()) {
                            final StringBuilder codeBuilder = new StringBuilder();
                            codeBuilder.append(ScriptParser.INFO_CHAR).append(scriptInfo.getTag()).append(' ');
                            final IScriptInfo<?> iScriptInfo = scriptInfo.get();
                            if (iScriptInfo instanceof final StringScriptInfo stringScriptInfo) {
                                if (stringScriptInfo instanceof AuthorScriptInfo) {
                                    codeBuilder.append(Vandalism.getInstance().getAuthor());
                                } else if (stringScriptInfo instanceof DescriptionScriptInfo) {
                                    codeBuilder.append("Toggles the ").append(module.getName()).append(" module.");
                                } else {
                                    codeBuilder.append(stringScriptInfo.defaultValue());
                                }
                            } else if (iScriptInfo instanceof CategoryScriptInfo) {
                                codeBuilder.append(module.getCategory().normalName());
                            } else if (iScriptInfo instanceof final BooleanScriptInfo booleanScriptInfo) {
                                if (booleanScriptInfo instanceof ExperimentalScriptInfo) {
                                    codeBuilder.append(module.isExperimental());
                                } else codeBuilder.append(booleanScriptInfo.defaultValue());
                            }
                            printerWriter.println(codeBuilder);
                        }
                        printerWriter.println();
                        printerWriter.println(ScriptParser.CODE_CHAR + ScriptCommand.RUN.name().toLowerCase() + " toggle " + module.getName());
                        printerWriter.close();
                        Vandalism.getInstance().getScriptRegistry().loadScriptFromFile(toggleScriptFile);
                    } catch (final Exception exception) {
                        Vandalism.getInstance().getLogger().error("Failed to generate toggle script for module: " + module.getName(), exception);
                    }
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
