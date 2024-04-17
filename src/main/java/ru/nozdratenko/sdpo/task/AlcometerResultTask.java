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
    public StatusType currentStatus = StatusType.FREE;

    @Override
    public void run() {
        SdpoLog.info("Alcometer run task: " + this.currentStatus.toString());
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}

            if (this.currentStatus == StatusType.FREE) {
                continue;
            }

            if (AlcometerHelper.PORT == null) {
                AlcometerHelper.setComPort();
                this.currentStatus = StatusType.FREE;
            }

            if (this.currentStatus == StatusType.STOP) {
                try {
                    AlcometerHelper.open();
                    AlcometerHelper.stop();
                } catch (UnsupportedEncodingException | SerialPortException e) {
                    SdpoLog.error(e);
                } catch (AlcometerException ignored) { }

                this.currentStatus = StatusType.FREE;

            } else if (this.currentStatus == StatusType.REQUEST) {
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

                this.currentStatus = StatusType.WAIT;

            } else if (this.currentStatus == StatusType.WAIT || this.currentStatus == StatusType.READY ) {
                try {
                    String result = AlcometerHelper.result();
                    if (Objects.equals(result, "STATUS_READY")) {
                        this.currentStatus = StatusType.READY;
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
            } else if (this.currentStatus == StatusType.ERROR) {
                AlcometerHelper.setComPort();
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
}
