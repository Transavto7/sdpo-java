package ru.nozdratenko.sdpo.util;

public enum StatusType {
    REQUEST(false),
    WAIT(false),
    STOP(false),
    RESULT(true),
    ERROR(true),
    FREE(true);

    public boolean skip = false;
    private StatusType(boolean skip) {
        this.skip = skip;
    }
}
