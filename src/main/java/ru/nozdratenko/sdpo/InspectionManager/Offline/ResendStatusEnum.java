package ru.nozdratenko.sdpo.InspectionManager.Offline;

public enum ResendStatusEnum {

    SEND ("Отправлено"),
    UNSENT ("Не отправлено"),
    NO_CONFIRMATION ("Нет подтверждения");

    private String title;

    ResendStatusEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }
}
