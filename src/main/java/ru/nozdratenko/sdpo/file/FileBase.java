package ru.nozdratenko.sdpo.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileBase {

    protected String name = "unknown";
    protected String path = "";

    public FileBase(String path) {
        this.name = getName(path);
        this.path = getBase(path);
    }

    public void create() throws IOException {
        File file = getFile();
        file.getParentFile().mkdirs();
        file.createNewFile();
    }

    public File getFile() {
        File file = new File(globalPath());
        return file;
    }

    public void writeFile(String str) throws IOException {
        FileWriter fooWriter = new FileWriter(getFile(), false);
        fooWriter.write(str);
        fooWriter.close();
    }

    public String globalPath() {
        return concatPath(getMainFolderUrl(), path, name);
    }

    public static String concatPath(String... args) {
        String result = "";

        for (String path : args) {
            if (path.length() > 0) {
                if (path.charAt(0) == '/' || String.valueOf(path.charAt(0)) == File.separator) {
                    path = path.substring(1);
                }

                if (path.charAt(path.length() - 1) == '/' ||
                        String.valueOf(path.charAt(path.length() - 1)) == File.separator) {
                    path = path.substring(0, path.length() - 1);
                }
            }

            result += File.separator + path;
        }

        return result.substring(1, result.length());
    }

    public static String getMainFolderUrl() {
        if(System.getProperty("os.name").toLowerCase().contains("win")) {
            return System.getenv("APPDATA") + File.separator + "sdpo";
        }

        return System.getProperty("user.home") + File.separator + "sdpo";
    }

    public static String getName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1];
    }

    public static String getBase(String path) {
        String[] split = path.split("/");
        return path.substring(0, path.length() - split[split.length - 1].length());
    }

    public String getName() {
        return this.name;
    }
}
