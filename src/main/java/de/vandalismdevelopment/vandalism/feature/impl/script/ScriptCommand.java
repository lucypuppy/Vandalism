package de.vandalismdevelopment.vandalism.feature.impl.script;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.util.Pair;

public enum ScriptCommand {

    RUN((script, lineNumber, code) -> Vandalism.getInstance().getCommandRegistry().commandDispatch(ScriptParser.applyCodeReplacements(code))),
    WAIT((script, lineNumber, code) -> {
        final String[] args = code.split(" ");
        if (args.length < 1) return;
        final long delay = Long.parseLong(args[0].trim());
        Thread.sleep(delay);
        if (args.length > 1) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCode = ScriptParser.parseCodeFromScriptLine(script,
                    ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), lineNumber
            );
            if (parsedCode != null) parsedCode.getLeft().execute(script, lineNumber, parsedCode.getRight().getRight());
        }
    }),
    TIMES((script, lineNumber, code) -> {
        final String[] args = code.split(" ");
        if (args.length < 2) return;
        final int times = Integer.parseInt(args[0].trim());
        for (int i = 0; i < times; i++) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCode = ScriptParser.parseCodeFromScriptLine(script,
                    ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), lineNumber
            );
            if (parsedCode == null) continue;
            parsedCode.getLeft().execute(script, lineNumber, parsedCode.getRight().getRight());
        }
    });

    private final ScriptExecution scriptExecution;

    ScriptCommand(final ScriptExecution scriptExecution) {
        this.scriptExecution = scriptExecution;
    }

    public void execute(final Script script, final int lineNumber, final String code) throws Exception {
        this.scriptExecution.execute(script, lineNumber, code);
    }

}
