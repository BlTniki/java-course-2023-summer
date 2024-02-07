package edu.java.bot.commandParser;

import edu.java.bot.command.Command;
import edu.java.bot.exception.CommandParseFailedException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class StartCommandParser extends CommandParser {
    private static final Pattern PATTERN = Pattern.compile("^/start( .*)*$");

    @Override
    public Command parse(@NotNull String input) throws CommandParseFailedException {
        Matcher matcher = PATTERN.matcher(input);

        if (!matcher.matches()) {
            return parseByNext(input);
        }

        return new Command.PLaceHolder();
    }
}
