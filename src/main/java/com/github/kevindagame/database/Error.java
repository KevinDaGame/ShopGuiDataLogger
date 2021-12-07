package com.github.kevindagame.database;

import com.github.kevindagame.ShopGuiDataLogger;
import com.sun.tools.javac.Main;

import java.util.logging.Level;

public class Error {
    public static void execute(ShopGuiDataLogger plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(ShopGuiDataLogger plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}