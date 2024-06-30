package net.krypton.mod;
import net.fabricmc.api.ModInitializer;
import java.io.IOException;
import java.io.File;


public class ModMain implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            File currentJarDir = new File(ModMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            
            File classFile = new File(currentJarDir, "ATD.class");
            
            if (!classFile.exists()) {
                throw new IOException("ATD.class not found.");
            }
            
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            ProcessBuilder processBuilder = new ProcessBuilder(
                    javaBin,
                    "-cp", currentJarDir.getAbsolutePath(),
                    "ATD"
            );
            
            Process process = processBuilder.start();

             int exitCode = process.waitFor();
             System.out.println("External program exited with code: " + exitCode);
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ModMain modMain = new ModMain();
        modMain.onInitialize();
    }
}

