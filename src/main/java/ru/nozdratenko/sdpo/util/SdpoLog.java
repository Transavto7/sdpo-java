package ru.nozdratenko.sdpo.util;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import ru.nozdratenko.sdpo.exception.SdpoException;
import ru.nozdratenko.sdpo.file.FileBase;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SdpoLog extends FileAppender {
    private static final Logger logger = Logger.getLogger("mainLog");

    public static void error(Object error) {
        if (error instanceof SdpoException) {
            SdpoException sdpoException = (SdpoException) error;
            logger.error("[SDPO] " + sdpoException.getMessage());
            return;
        }

        if (error instanceof Throwable) {
            Throwable exception = (Throwable) error;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            logger.error(sw.toString());
            return;
        }

        logger.error(error);
    }

    public static void info(Object info) {
        logger.info(info);
    }

    public static void nativeLog(String string) {
        logger.info("[native] " + string);
    }

    public static void debug(Object debug) {
        logger.debug(debug);
    }

    public static void warning(Object warning) {
        logger.warn(warning);
    }

    @Override
    public void setFile(String fileName) {

        if (fileName.contains("%timestamp")) {
            Date d = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            fileName = fileName.replaceAll("%timestamp", format.format(d));
        }

        if(fileName.contains("%log_path")) {
            fileName = fileName.replaceAll("%log_path",
                    FileBase.concatPath(FileBase.getMainFolderUrl(), "logs").replace(File.separator, "/"));
        }

        super.setFile(fileName);
    }

}
