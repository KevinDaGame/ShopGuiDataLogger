package com.github.kevindagame.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.github.kevindagame.ShopGuiDataLogger;
import com.sun.tools.javac.Main;
import net.brcdev.shopgui.shop.ShopManager;
import net.brcdev.shopgui.shop.ShopTransactionResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Database {
    ShopGuiDataLogger plugin;
    Connection connection;
    // The name of the table we created back in SQLite class.
    public String table = "shop_sales";
    public int tokens = 0;
    public Database(ShopGuiDataLogger instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE amount = 0");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public void addTransaction(ShopTransactionResult transaction){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            String uuid = transaction.getPlayer().getUniqueId().toString();
            String item = transaction.getShopItem().getItem().getType().toString();
            int itemCount = transaction.getAmount();
            ps = conn.prepareStatement("INSERT INTO shop_sales VALUES(\"" + item +"\", " + itemCount + ", date('now'))\n" +
                    "  ON CONFLICT(item_name,transaction_date) DO UPDATE SET amount=amount+"+ itemCount + ";");
//
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;

    }


    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}

