package ru.nozdratenko.sdpo.task;

import jssc.SerialPortException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

import java.io.UnsupportedEncodingException;

public class AlcometerResultTask extends Thread {
    public String result = "0";
    public JSONObject error;
    public StatusType currentStatus = StatusType.FREE;

    @Override
    public void run() {
        SdpoLog.info("Alcometer run task: " + this.currentStatus.toString());
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //
            }

            if (this.currentStatus == StatusType.STOP) {
                try {
                    AlcometerHelper.stop();
                } catch (UnsupportedEncodingException | SerialPortException e) {
                    e.printStackTrace();
                    SdpoLog.error(e);
                }

                this.currentStatus = StatusType.FREE;
            }

            if (this.currentStatus == StatusType.REQUEST) {
                try {
                    AlcometerHelper.open();
                    AlcometerHelper.start();
                } catch (SerialPortException e) {
                    setError("Ошибка открытия порта Алкометра");
                    continue;
                } catch (UnsupportedEncodingException e) {
                    setError("Ошибка закрытия алкометра");
                    continue;
                }

                this.currentStatus = StatusType.WAIT;
            }

            if (this.currentStatus == StatusType.WAIT) {
                try {
                    String result = AlcometerHelper.result();
                    if (result == null) {
                        continue;
                    }
                    setResult(result);
                } catch (SerialPortException e) {
                    setError("Ошибка открытия порта Алкометра");
                } catch (AlcometerException e) {
                    setError(e.getResponse());
                }
            }
        }
    }

    public void setResult(String result) {
        this.result = result;
        this.currentStatus = StatusType.RESULT;
    }

    public void setError(JSONObject message) {
        this.error = message;
        this.currentStatus = StatusType.ERROR;
    }

    public void setError(String message) {
        JSONObject json = new JSONObject();
        json.put("message", message);
        this.setError(json);
    }

    public void close() {
        this.currentStatus = StatusType.STOP;
    }
}
