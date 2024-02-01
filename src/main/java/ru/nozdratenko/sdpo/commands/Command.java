package ru.nozdratenko.sdpo.commands;

import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.HashMap;
import java.util.Map;

public abstract class Command {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        new AlcometrScreenCommand().register(commands);
        new AlcometrCommand().register(commands);
        new PhotoCommand().register(commands);
        new VideoCommand().register(commands);
        new PhotoAndVideoCommand().register(commands);
    }

    protected void register(Map<String, Command> commands) {
        commands.put(this.getCommand(), this);
    }

    public static void execute(String key, String[] args) {
        SdpoLog.info("======[ Run command line ]======");
        Command command = commands.get(key);
        if (command == null) {
            SdpoLog.error("Command " + key + " not found");
        }

        command.run(args);
        System.exit(0);
    }

    abstract String getCommand();
    abstract void run(String[] args);
}
