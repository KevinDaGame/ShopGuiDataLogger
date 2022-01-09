package com.github.kevindagame;

import com.github.kevindagame.Commands.Command;
import com.github.kevindagame.database.Database;
import com.github.kevindagame.database.SQLite;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ShopGuiDataLogger extends JavaPlugin {
    private Database db;
    private QueryHandler queryHandler;
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new TransactionEvent(this), this);
        getCommand("sgdl").setExecutor(new Command(this));


        this.db = new SQLite(this);
        this.db.load();
        File queryFile = new File(getDataFolder(), "queries.yml");
        if (!queryFile.exists()) saveResource(queryFile.getName(), false);
        FileConfiguration queries = YamlConfiguration.loadConfiguration(queryFile);
        queryHandler = new QueryHandler(queries, db, this);


    }

    @Override
    public void onDisable() {

    }

    public Database getDB() {
        return db;
    }

    public QueryHandler getQueryHandler() {
        return queryHandler;
    }
}
