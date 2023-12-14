package de.nekosarekawaii.vandalism.feature.script.parse.command;

public interface ScriptCommandExecution {

    void execute(final String scriptName, final int lineNumber, final String code, final boolean execute) throws Exception;

}