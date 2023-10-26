package de.vandalismdevelopment.vandalism.feature.impl.script;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Pair;

public enum ScriptCommand {

    RUN((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args[0].equals("script") && args.length > 2 && args[1].equals("execute") && args[2].equals(scriptName)) {
            throw new RuntimeException("This script can't run itself because this would cause a stack overflow");
        }
        if (execute) {
            Vandalism.getInstance().getCommandRegistry().execute(ScriptParser.applyCodeReplacements(code));
        } else {
            final ParseResults<CommandSource> parse = Vandalism.getInstance().getCommandRegistry().parse(code);
            if (parse.getReader().canRead()) {
                if (parse.getExceptions().size() == 1) {
                    throw parse.getExceptions().values().iterator().next();
                } else if (parse.getContext().getRange().isEmpty()) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader());
                } else {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
                }
            } else if (parse.getContext().getCommand() == null) {
                throw new RuntimeException("The command needs at least one argument");
            }
        }
    }),
    WAIT((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("Wait command needs at least one argument");
        final String
                commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1),
                command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        final long delay;
        try {
            delay = Long.parseLong(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid long value '" + args[0] + "' for delay argument after wait command");
        }
        if (delay < 1) {
            throw new RuntimeException("Delay '" + args[0] + "' after wait command must be greater than 0");
        }
        if (execute) Thread.sleep(delay);
        if (args.length > 1) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(
                    scriptName,
                    commandWithCode,
                    lineNumber,
                    execute
            );
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after wait command '" + command + "'");
        }
    }),
    TIMES((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("Times command needs at least one argument");
        final String
                commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1),
                command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        int amount;
        try {
            amount = Integer.parseInt(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid integer value '" + args[0] + "' for amount argument after times command");
        }
        if (amount < 1) {
            throw new RuntimeException("Amount '" + args[0] + "' after times command must be greater than 0");
        }
        if (args.length < 2) {
            throw new RuntimeException("Times command needs at least two arguments '" + args[0] + "'");
        }
        for (int i = 0; i < amount; i++) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(
                    scriptName,
                    commandWithCode,
                    lineNumber,
                    execute
            );
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after times command '" + command + "'");
        }
    });

    private final ScriptCommandExecution scriptCommandExecution;

    ScriptCommand(final ScriptCommandExecution scriptCommandExecution) {
        this.scriptCommandExecution = scriptCommandExecution;
    }

    public void check(final String scriptName, final int lineNumber, final String code) throws Exception {
        this.scriptCommandExecution.execute(scriptName, lineNumber, code, false);
    }

    public void execute(final String scriptName, final int lineNumber, final String code) throws Exception {
        this.scriptCommandExecution.execute(scriptName, lineNumber, code, true);
    }

}
