package de.vandalismdevelopment.vandalism.feature.impl.script.parse.command;

public interface ScriptCommandExecution {

    void execute(final String scriptName, final int lineNumber, final String code, final boolean execute) throws Exception;

}