package ru.nozdratenko.sdpo.task.Alcometer;

import jssc.SerialPortException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.exception.AlcometerException;
import ru.nozdratenko.sdpo.helper.AlcometerHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;
import ru.nozdratenko.sdpo.util.StatusType;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Component
public class AlcometerResultTask implements Runnable {
    public String result = "0";
    public JSONObject error;
    private boolean flow = false;
    public StatusType currentStatus = StatusType.FREE;
    private volatile boolean stopFlag = false;

    @Autowired
    private AlcometerHelper alcometerHelper;

    @Override
    public void run() {
        SdpoLog.info("Alcometer run task: " + this.currentStatus.toString());
        while (stopFlag) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}

            if (this.currentStatus == StatusType.FREE) {
                continue;
            }

            if (AlcometerHelper.PORT == null) {
                this.alcometerHelper.setComPort();
                this.currentStatus = StatusType.FREE;
            }

            if (this.currentStatus == StatusType.STOP) {
                try {
                    this.alcometerHelper.open();
                    this.alcometerHelper.stop();
                } catch (UnsupportedEncodingException | SerialPortException e) {
                    SdpoLog.error(e);
                } catch (AlcometerException ignored) { }

                this.currentStatus = StatusType.FREE;

            } else if (this.currentStatus == StatusType.REQUEST) {
                try {
                    this.alcometerHelper.open();
                    this.alcometerHelper.start();
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

                this.currentStatus = StatusType.WAIT;

            } else if (this.currentStatus == StatusType.WAIT
                    || this.currentStatus == StatusType.READY
                    || this.currentStatus == StatusType.ANALYSE ) {
                try {
                    String result = this.alcometerHelper.result();
                    if (Objects.equals(result, "STATUS_READY")) {
                        this.currentStatus = StatusType.READY;
                        continue;
                    }
                    if (Objects.equals(result, "ANALYSE")) {
                        this.currentStatus = StatusType.ANALYSE;
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
                                this.alcometerHelper.start();
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
            } else if (this.currentStatus == StatusType.ERROR) {
                this.alcometerHelper.setComPort();
                this.currentStatus = StatusType.FREE;
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

    public void stop() {
        this.stopFlag = true;
        Thread.currentThread().interrupt();
    }
}
