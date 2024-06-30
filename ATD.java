package net.krypton.atd;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;

public class ATD {
    private void downloadFile(URL url) throws Exception {
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream())) {
            String fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (Exception e) {
            throw e;
        }
    }

    private static void printProgress(int currentTask, int totalTasks) {
        int percent = (int) ((double) currentTask / totalTasks * 100);
        String progress = "[" + String.join("", Collections.nCopies(percent, "=")) 
                       + (percent < 100 ? ">" : "]")
                       + String.join("", Collections.nCopies(100 - percent, " "))
                       + "] " + percent + "%";
        System.out.print("\r" + progress);
    }

    public void downloadFiles() throws Exception {
        if (new File("isDone").exists()) {
            return;
        }
        int totalTasks = 0;
        BufferedReader reader = null;
        String[] failedMods = new String[0];
        int failedModsIndex = 0;
        int currentTask = 0;
        try {
            String line;
            reader = new BufferedReader(new FileReader("urls.txt"));
            while ((line = reader.readLine()) != null) {
                totalTasks++;
            }
            reader.close();
            reader = new BufferedReader(new FileReader("urls.txt"));
            printProgress(0, totalTasks);
            while ((line = reader.readLine()) != null) {
                URI uri = new URI(line.trim());
                URL url = uri.toURL();
                try {
                    System.out.println("\n正在下载: " + url.getPath().substring(url.getPath().lastIndexOf('/') + 1));
                    downloadFile(url);
                    System.out.println("下载完成: " + url.getPath().substring(url.getPath().lastIndexOf('/') + 1));
                } catch (Exception e) {
                    System.err.println("下载失败: " + url + " \n\t 错误原因: " + e.getMessage());
                    if (failedMods.length == failedModsIndex) {
                        String[] temp = new String[failedModsIndex + 1];
                        System.arraycopy(failedMods, 0, temp, 0, failedModsIndex);
                        failedMods = temp;
                    }
                    failedMods[failedModsIndex++] = url.toString();
                }finally {
                    currentTask++;
                    printProgress(currentTask, totalTasks);
                    if (currentTask == totalTasks) {
                        System.out.println("\n\n-------------------------------------\n");
                    }
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        if (failedModsIndex > 0) {
            System.out.println("下载过程中存在错误，以下是下载失败的链接\n如您不是整合包作者，请联系作者更新下载列表链接。\n或您可自己尝试下载对应的资源。\n\n-------------------------------------\n");
            for (String mod : failedMods) {
                System.out.println(mod);
            }
            System.out.println("\n----------------- END --------------------\n");
        } else {
            File isDone = new File("isDone");
            isDone.createNewFile();
            isDone.setLastModified(System.currentTimeMillis());
            System.out.println("下载完成，请重启MC...");
        }

        System.out.println("按两次换行键继续...");
        new java.util.Scanner(System.in).nextLine();
        new java.util.Scanner(System.in).nextLine();

        if (failedModsIndex > 0) {
            throw new Exception("下载过程中存在错误，请在排查urls.txt后重启");
        } else {
            throw new Exception("下载完成，所以我帮你关闭了客户端，重启就好");
        }
    }
    public static void main(String[] args) throws Exception {
        new ATD().downloadFiles();
    }
}