package ru.nozdratenko.sdpo.Core.FileSystem;

import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FileBase {

    protected String name = "unknown";
    protected String path = "";
    private static final Map<String, String> dirBase = Map.of("dll", "native", "exe", "versions");

    public FileBase(String path) {
        this.name = getName(path);
        this.path = getBase(path);
    }

    public String read() throws IOException {
        File file = getFile();
        if (!file.exists() || !file.isFile()) {
            return "";
        }

        SdpoLog.debug("Read file " + getFile().getAbsolutePath());

        InputStream inputStream = new FileInputStream(file);
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public void create() throws IOException {
        File file = getFile();
        file.getParentFile().mkdirs();
        file.createNewFile();
        SdpoLog.debug("Create file:" + file.getAbsolutePath());
    }

    public File getFile() {
        File file = new File(globalPath());
        return file;
    }

    public void writeFile(String str) throws IOException {
        SdpoLog.debug("Write file:" + getFile().getAbsolutePath());
        Writer fstream = null;
        BufferedWriter out = null;
        fstream = new OutputStreamWriter(new FileOutputStream(getFile()), StandardCharsets.UTF_8);
        fstream.write(str);
        fstream.close();
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

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            result = result.substring(1);
        }

        return result;
    }

    public static String getMainFolderUrl() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
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

    public static String exportLibrary(String resourceName) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String[] split = resourceName.split("\\.");
        String extension = split[split.length - 1];
        if (dirBase.containsKey(extension)) {
            try {
                stream = FileBase.class.getClassLoader().getResourceAsStream(resourceName);
                if (stream == null) {
                    SdpoLog.error("Cannot get resource \"" + dirBase.get(extension) + "\" from Jar file.");
                    return null;
                }

                int readBytes;
                byte[] buffer = new byte[4096];
                String out = concatPath(getMainFolderUrl(), dirBase.get(extension), resourceName);
                new File(out).getParentFile().mkdirs();
                resStreamOut = new FileOutputStream(out);
                while ((readBytes = stream.read(buffer)) > 0) {
                    resStreamOut.write(buffer, 0, readBytes);
                }

                stream.close();
                resStreamOut.close();

                return out;
            } catch (Exception ex) {
                SdpoLog.error(ex);
            }
        }

        return null;
    }

    public String getName() {
        return this.name;
    }
}
