package com.github.Soulphur0.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ConfigFileWriter {

    static public void createConfigFile(EanConfigFile eanConfigFile){
        File configFile = new File("config/ElytraAeronautics.ser");
        if (!configFile.exists()){
            try {
                FileOutputStream fileOut = new FileOutputStream("config/ElytraAeronautics.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(eanConfigFile);
                out.close();
                fileOut.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    static public void writeToFile(EanConfigFile eanConfigFile){
        try {
            FileOutputStream fileOut = new FileOutputStream("config/ElytraAeronautics.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(eanConfigFile);
            out.close();
            fileOut.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
