package com.template.util;

import java.io.FileWriter;

public class AppLogger {

    public static void log(String msg){
        try(FileWriter fw =
                    new FileWriter("data/app.log", true)) {

            fw.write(msg + "\n");

        } catch(Exception ignored){}
    }
}