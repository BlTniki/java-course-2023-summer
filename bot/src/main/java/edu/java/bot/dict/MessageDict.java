package edu.java.bot.dict;

public enum MessageDict {
    SUCCESSFUL_SIGN_UP("_Регистрация прошла успешно!_"),
    USER_ALREADY_SIGN_UP("_Ты уже зарегистрирован! Добро пожаловать обратно!_"),
    USER_NOT_EXIST("_Не смог тебя найти в базе... Ты точно зареган?_"),
    HELP_COMMANDS_BUILDER("```command\n/%s %s\n```_%s_\n"),
    SUCCESSFUL_TRACK("_Начинаю следить за новым url_"),
    SUCCESSFUL_UNTRACK("_Прекращаю следить за этим url_"),
    URL_ALREADY_EXIST("_Этот url уже отслеживается_"),
    ALIAS_ALREADY_EXIST("_Этот alias уже занят для другого url_"),
    LINK_NOT_FOUND("_Не смог найти url с таким alias_"),
    BAD_INPUT_NO_TEXT("_В сообщении нет теста.\nЯ взаимодействую только с текстом >:[_"),
    BAD_INPUT_UNRECOGNIZED_COMMAND("_Не смог распознать команду:_ `%s`"),
    BAD_INPUT_WRONG_COMMAND_ARGUMENTS(
        "_Неверные аргументы команды. Использование:_\n```command\n/%s %s```"
    ),
    INTERNAL_SERVER_ERROR("*О нет, похоже что я сломался(*");

    public final String msg;

    MessageDict(String msg) {
        this.msg = msg;
    }
}
