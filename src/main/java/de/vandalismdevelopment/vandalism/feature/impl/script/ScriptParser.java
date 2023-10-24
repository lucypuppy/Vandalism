package de.vandalismdevelopment.vandalism.feature.impl.script;

import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.common.object.ObjectTypeChecker;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import net.minecraft.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScriptParser {

    public final static FeatureCategory EXAMPLE_SCRIPT_CATEGORY = FeatureCategory.MISC;

    public final static boolean EXAMPLE_SCRIPT_EXPERIMENTAL = false;

    public final static String SCRIPT_FILE_EXTENSION = "." + Vandalism.getInstance().getLowerCaseName() + "-script",
            EXAMPLE_SCRIPT_VERSION = "1.0.0",
            EXAMPLE_SCRIPT_AUTHOR = "Example Author",
            EXAMPLE_SCRIPT_DESCRIPTION = "Example Description",
            INFO_VERSION = "version",
            INFO_AUTHOR = "author",
            INFO_DESCRIPTION = "description",
            INFO_CATEGORY = "category",
            INFO_EXPERIMENTAL = "experimental";

    public final static String INFO_CHAR = "@", CODE_CHAR = "!", VARIABLE_CHAR = "%";

    public static Script parseScriptInfoFromFile(final File file) throws IOException {
        if (!file.exists() || !file.isFile() || file.length() < 1 || !file.getName().endsWith(SCRIPT_FILE_EXTENSION)) {
            return null;
        }
        final String name = StringUtils.replaceLast(file.getName(), SCRIPT_FILE_EXTENSION, "");
        String version = EXAMPLE_SCRIPT_VERSION, author = EXAMPLE_SCRIPT_AUTHOR, description = EXAMPLE_SCRIPT_DESCRIPTION;
        FeatureCategory category = EXAMPLE_SCRIPT_CATEGORY;
        boolean experimental = EXAMPLE_SCRIPT_EXPERIMENTAL;
        try (final Scanner scanner = new Scanner(file)) {
            for (int i = 0; i < 5; i++) {
                if (scanner.hasNextLine()) {
                    final String line = scanner.nextLine();
                    try {
                        if (!line.startsWith(INFO_CHAR)) {
                            Vandalism.getInstance().getLogger().error("Invalid script info line '" + line + "' (" + (i + 1) + ") in file '" + name + "'. " +
                                    "The line isn't starting with '" + INFO_CHAR + "'.");
                            continue;
                        }
                        final String info = line.substring(1).trim();
                        if (info.startsWith(INFO_VERSION)) version = info.substring(INFO_VERSION.length()).trim();
                        else if (info.startsWith(INFO_AUTHOR)) author = info.substring(INFO_AUTHOR.length()).trim();
                        else if (info.startsWith(INFO_DESCRIPTION)) {
                            description = info.substring(INFO_DESCRIPTION.length()).trim();
                        } else if (info.startsWith(INFO_CATEGORY)) {
                            final FeatureCategory newCategory = FeatureCategory.fromNormalName(info.substring(INFO_CATEGORY.length()).trim());
                            if (newCategory == null) {
                                Vandalism.getInstance().getLogger().error(
                                        "Invalid script category in line '" + line + "' (" + (i + 1) + ") in file '" + name + "'."
                                );
                            } else category = newCategory;
                        } else if (info.startsWith(INFO_EXPERIMENTAL)) {
                            final String experimentalString = line.substring(INFO_EXPERIMENTAL.length() + 2).trim();
                            if (ObjectTypeChecker.isBoolean(experimentalString)) {
                                experimental = Boolean.parseBoolean(experimentalString);
                            } else {
                                Vandalism.getInstance().getLogger().error(
                                        "Invalid script experimental in line '" + line + "' (" + (i + 1) + ") in file '" + name + "'."
                                );
                            }
                        } else {
                            Vandalism.getInstance().getLogger().error("Invalid script info line '" + line + "' (" + (i + 1) + ") in file '" + name + "'.");
                        }
                    } catch (final Exception e) {
                        Vandalism.getInstance().getLogger().error(
                                "Failed to parse script from file '" + name + "' due to an error which was caused by the line " + (i + 1) + ".", e
                        );
                        return null;
                    }
                }
            }
        }
        return new Script(file, name, version, author, description, category, experimental);
    }

    public static List<Pair<ScriptCommand, Pair<Integer, String>>> parseCodeFromScriptFile(final Script script) {
        final File file = script.getFile();
        if (file == null || !file.exists() || !file.isFile() || file.length() < 1) {
            return null;
        }
        int lineNumber = 0;
        try {
            final Scanner scanner = new Scanner(file);
            final List<Pair<ScriptCommand, Pair<Integer, String>>> code = new ArrayList<>();
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                lineNumber++;
                if (!line.startsWith("!")) continue;
                final Pair<ScriptCommand, Pair<Integer, String>> command = parseCodeFromScriptLine(script, line, lineNumber);
                if (command == null) {
                    Vandalism.getInstance().getLogger().warn(
                            "Skipping code '" + line + "' in script '" + script.getName() + "' at line " + lineNumber + "."
                    );
                    continue;
                }
                code.add(command);
            }
            scanner.close();
            return code;
        } catch (final Exception e) {
            Vandalism.getInstance().getLogger().error(
                    "Failed to parse script code line from file '" + script.getName() + "' due to an error which was caused by line " + lineNumber + ".",
                    e
            );
            return null;
        }
    }

    public static Pair<ScriptCommand, Pair<Integer, String>> parseCodeFromScriptLine(final Script script, String line, final int lineNumber) {
        try {
            if (line.startsWith(CODE_CHAR)) {
                if (line.startsWith(CODE_CHAR + CODE_CHAR)) line = line.replaceFirst(CODE_CHAR + CODE_CHAR, CODE_CHAR);
                boolean notFound = true;
                for (final ScriptCommand command : ScriptCommand.values()) {
                    final String commandStart = CODE_CHAR + command.name().toLowerCase();
                    if (line.startsWith(commandStart)) {
                        notFound = false;
                        final String[] split = line.substring(commandStart.length() - 1).trim().split(" ", 2);
                        if (split.length != 2) {
                            Vandalism.getInstance().getLogger().warn(
                                    "Skipping invalid script command '" + line + "' in script '" + script.getName() + "' at line " + lineNumber + "."
                            );
                            break;
                        }
                        return new Pair<>(command, new Pair<>(lineNumber, split[1]));
                    }
                }
                if (notFound) {
                    Vandalism.getInstance().getLogger().warn(
                            "Skipping unknown script command '" + line + "' in script '" + script.getName() + "' at line " + lineNumber + "."
                    );
                }
            }
        } catch (final Exception e) {
            Vandalism.getInstance().getLogger().error(
                    "Failed to parse code line from script '" + script.getName() + "' due to an error which was caused by the line " + lineNumber + ".", e
            );
        }
        return null;
    }

    public static String applyCodeReplacements(String code) {
        if (!code.contains(VARIABLE_CHAR)) return code;
        for (final ScriptVariable variable : ScriptVariable.values()) code = variable.replaceCode(code);
        return code;
    }

}
