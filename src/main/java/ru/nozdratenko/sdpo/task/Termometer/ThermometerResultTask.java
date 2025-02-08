package ru.nozdratenko.sdpo.task.Termometer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nozdratenko.sdpo.helper.ThermometerHelper;

@Component
public class ThermometerResultTask implements Runnable {
    public double result = 0;
    private final ThermometerHelper thermometerHelper;

    @Autowired
    public ThermometerResultTask(ThermometerHelper thermometerHelper) {
        this.thermometerHelper = thermometerHelper;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.result = this.thermometerHelper.getTemp();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //
            }
        }
    }

    public boolean exist() {
        if (this.result > 0) {
            return true;
        }

        return false;
    }

    public void clear() {
        this.result = 0;
    }
}
