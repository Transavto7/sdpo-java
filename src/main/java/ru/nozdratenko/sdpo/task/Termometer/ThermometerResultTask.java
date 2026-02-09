package ru.nozdratenko.sdpo.task.Termometer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.helper.ThermometerHelper;

@Component
public class ThermometerResultTask implements Runnable {
    public double result = 0;
    private boolean isTestMode = false;
    private final ThermometerHelper thermometerHelper;

    @Autowired
    public ThermometerResultTask(ThermometerHelper thermometerHelper) {
        this.thermometerHelper = thermometerHelper;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Не обновляем result если установлено тестовое значение
                if (!isTestMode) {
                    this.result = this.thermometerHelper.getTemp();
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //
            }
        }
    }

    public boolean exist() {
        return this.result > 0;
    }

    public void clear() {
        this.result = 0;
        this.isTestMode = false; // Снимаем тестовый режим
    }

    /**
     * Устанавливает тестовое значение термометра для режима разработки
     * @param temp температура в градусах Цельсия (например, 36.6, 37.5)
     */
    public void setTestValue(double temp) {
        this.isTestMode = true; // Включаем тестовый режим
        this.result = temp;
        ru.nozdratenko.sdpo.util.SdpoLog.info(String.format("Test thermometer value set: %.1f °C", temp));
    }
}
