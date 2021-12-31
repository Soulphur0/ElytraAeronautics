package com.github.Soulphur0.config;

import java.io.*;

public class ConfigFileReader {

    static public EanConfigFile getConfigFile(){
        EanConfigFile output = new EanConfigFile();
        try{
            FileInputStream fileIn = new FileInputStream("config/ElytraAeronautics.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            output = (EanConfigFile) in.readObject();
            in.close();
            fileIn.close();
            return output;
        } catch (IOException e){
            System.out.println("A problem has occurred when loading the config data.");
            e.printStackTrace();
            return output;
        } catch (ClassNotFoundException f){
            System.out.println("CloudLayer class not found.");
            f.printStackTrace();
            return output;
        } catch (NullPointerException g){
            System.out.println("There were no files found to read.");
            g.printStackTrace();
            return output;
        }
    }
}
