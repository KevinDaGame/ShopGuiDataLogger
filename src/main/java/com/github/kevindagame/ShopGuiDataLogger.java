package com.github.kevindagame;

import com.github.kevindagame.Commands.Command;
import com.github.kevindagame.database.Database;
import com.github.kevindagame.database.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopGuiDataLogger extends JavaPlugin {
    private Database db;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new TransactionEvent(this), this);
        getCommand("sgdl").setExecutor(new Command(this));
        this.db = new SQLite(this);
        this.db.load();


    }

    @Override
    public void onDisable() {

    }

    public Database getDB() {
        return db;
    }
}
