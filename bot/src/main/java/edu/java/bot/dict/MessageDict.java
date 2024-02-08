package edu.java.bot.dict;

public enum MessageDict {
    SUCCESSFUL_SIGN_UP("_Регистрация прошла успешно!_"),
    BAD_INPUT_NO_TEXT("_В сообщении нет теста.\nЯ взаимодействую только с текстом >:[_"),
    BAD_INPUT_UNRECOGNIZED_COMMAND("_Не смог распознать команду: `%s`_"),
    INTERNAL_SERVER_ERROR("*О нет, похоже что я сломался(*");

    public final String msg;

    MessageDict(String msg) {
        this.msg = msg;
    }
}
