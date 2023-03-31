package ru.nozdratenko.sdpo.task;

import ru.nozdratenko.sdpo.helper.ThermometerHelper;

public class ThermometerResultTask extends Thread {
    public double result = 0;

    @Override
    public void run() {
        while (true) {
            try {
                this.result = ThermometerHelper.getTemp();
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
