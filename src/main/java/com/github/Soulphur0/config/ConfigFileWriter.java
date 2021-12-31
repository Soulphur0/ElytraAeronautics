package com.github.Soulphur0.config;

import java.io.*;

public class ConfigFileWriter {

    static public void createConfigFile(EanConfigFile eanConfigFile){
        File configFile = new File("config/ElytraAeronautics.ser");
        if (!configFile.exists()){
            System.out.println("NEW CONFIG FILE CREATED");
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
