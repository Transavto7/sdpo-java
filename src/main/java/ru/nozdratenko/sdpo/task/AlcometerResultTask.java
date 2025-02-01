package ru.nozdratenko.sdpo.task;

import jssc.SerialPortException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class AlcometerResultTask extends Thread {
    public String result = "0";
    public JSONObject error;
    private boolean flow = false;
    public static StatusType currentStatus = StatusType.FREE;

    @Override
    public void run() {
        SdpoLog.info("Alcometer run task: " + currentStatus.toString());
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}

            if (currentStatus == StatusType.FREE) {
                continue;
            }

            if (AlcometerHelper.PORT == null) {
                AlcometerHelper.setComPort();
                currentStatus = StatusType.FREE;
            }

            if (currentStatus == StatusType.STOP) {
                try {
                    AlcometerHelper.open();
                    AlcometerHelper.stop();
                } catch (UnsupportedEncodingException | SerialPortException e) {
                    SdpoLog.error(e);
                } catch (AlcometerException ignored) { }

                currentStatus = StatusType.FREE;

            } else if (currentStatus == StatusType.REQUEST) {
                try {
                    AlcometerHelper.open();
                    AlcometerHelper.start();
                } catch (SerialPortException e) {
                    setError("Ошибка открытия порта Алкометра");
                    continue;
                } catch (UnsupportedEncodingException e) {
                    setError("Ошибка закрытия алкометра");
                    continue;
                } catch (AlcometerException e) {
                    setError(e.message);
                    continue;
                }

                currentStatus = StatusType.WAIT;

            } else if (currentStatus == StatusType.WAIT
                    || currentStatus == StatusType.READY
                    || currentStatus == StatusType.ANALYSE ) {
                try {
                    String result = AlcometerHelper.result();
                    if (Objects.equals(result, "STATUS_READY")) {
                        currentStatus = StatusType.READY;
                        continue;
                    }
                    if (Objects.equals(result, "ANALYSE")) {
                        currentStatus = StatusType.ANALYSE;
                        continue;
                    }
                    if (result == null) {
                        continue;
                    }

                    setResult(result);
                } catch (SerialPortException e) {
                    setError("Ошибка открытия порта Алкометра");
                } catch (AlcometerException e) {
                    if (e.restart) {
                        if (flow)  {
                            try {
                                AlcometerHelper.start();
                            } catch (SerialPortException ex) {
                                setError("Ошибка открытия порта Алкометра");
                                continue;
                            } catch (UnsupportedEncodingException ex) {
                                setError("Ошибка закрытия алкометра");
                                continue;
                            } catch (AlcometerException ex) {
                                setError(ex.message);
                                continue;
                            }
                        }
                        continue;
                    }

                    flow = true;
                    setError(e.getResponse());
                }
            } else if (currentStatus == StatusType.ERROR) {
                AlcometerHelper.setComPort();
                currentStatus = StatusType.FREE;
            }
        }
    }

    public void setResult(String result) {
        this.result = result;
        currentStatus = StatusType.RESULT;
    }

    public void setError(JSONObject message) {
        this.error = message;
        currentStatus = StatusType.ERROR;
    }

    public void setError(String message) {
        JSONObject json = new JSONObject();
        json.put("message", message);
        this.setError(json);
    }

    public void close() {
        currentStatus = StatusType.STOP;
    }
}
