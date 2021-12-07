package com.github.kevindagame;
import com.github.kevindagame.database.Database;
import com.github.kevindagame.database.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ShopGuiDataLogger extends JavaPlugin {
    private Database db;
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new TransactionEvent(this), this);
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) saveResource(configFile.getName(), false);
        this.db = new SQLite(this);
        this.db.load();


    }
    @Override
    public void onDisable(){

    }

    public Database getDB() {
        return db;
    }
}
