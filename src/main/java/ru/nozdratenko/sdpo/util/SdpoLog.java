package ru.nozdratenko.sdpo.util;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import ru.nozdratenko.sdpo.file.FileBase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SdpoLog extends FileAppender {
    private static final Logger logger = Logger.getLogger("mainLog");

    public static void error(Object error) {
        logger.error(error);
    }

    public static void info(Object info) {
        logger.info(info);
    }

    public static void debug(Object debug) {
        if(logger.isDebugEnabled())
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
