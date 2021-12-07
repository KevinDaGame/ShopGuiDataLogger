package com.github.kevindagame.database;

import com.github.kevindagame.ShopGuiDataLogger;
import com.sun.tools.javac.Main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLite extends Database{
    String dbname;
    public SQLite(ShopGuiDataLogger instance){
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "shop_sales"); // Set the table name here e.g player_kills
    }

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS shop_sales (" +
            "`player` varchar(32) NOT NULL," +
            "`item_name` varchar(32) NOT NULL," +
            "`amount` int(5) NOT NULL," +
            "`is_buy` int(1) NOT NULL," +
            "`transaction_time` DATETIME NOT NULL," +
            "`id` INTEGER NOT NULL PRIMARY KEY" +
            ");";


    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}