package ru.nozdratenko.sdpo.task;

import jssc.SerialPortException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.UnsupportedEncodingException;

public class AlcometerResultTask extends Thread {
    public double result = 0;
    public JSONObject error;
    public Status currentStatus = Status.FREE;
    public enum Status {
            WAIT,
            RESULT,
            ERROR,
            FREE
    };

    @Override
    public void run() {
        SdpoLog.info("Alcometer run task: " + this.currentStatus.toString());
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //
            }

            SdpoLog.info("Current status: " + this.currentStatus.toString());

            if (this.currentStatus != Status.WAIT) {
                continue;
            }

            try {
                this.result = AlcometerHelper.getResult();
                this.currentStatus = Status.RESULT;
            } catch (SerialPortException | UnsupportedEncodingException e) {
                SdpoLog.error(e);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", "Неизвестная ошибка алкометра");
                this.error = jsonObject;
                this.currentStatus = Status.ERROR;
            } catch (AlcometerException e) {
                this.error = e.getResponse();
            }
        }
    }
}
