package com.github.Soulphur0.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

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
            e.printStackTrace();
            return output;
        } catch (ClassNotFoundException f){
            f.printStackTrace();
            return output;
        } catch (NullPointerException g){
            g.printStackTrace();
            return output;
        }
    }
}
