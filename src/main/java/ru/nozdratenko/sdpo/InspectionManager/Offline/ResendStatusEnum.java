package ru.nozdratenko.sdpo.InspectionManager.Offline;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResendStatusEnum {

    SEND ("Отправлено"),
    UNSENT ("Не отправлено"),
    NO_CONFIRMATION ("Нет подтверждения");

    private final String title;

    @Override
    public String toString() {
        return this.getTitle();
    }
}
