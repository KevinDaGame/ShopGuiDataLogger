package com.github.kevindagame.database;

import com.github.kevindagame.ShopGuiDataLogger;
import net.brcdev.shopgui.shop.ShopTransactionResult;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public abstract class Database {
    public String table = "shop_sales";
    ShopGuiDataLogger plugin;
    Connection connection;

    public Database(ShopGuiDataLogger instance) {
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE amount = 0");
            ResultSet rs = ps.executeQuery();
            close(ps, rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public void addTransaction(ShopTransactionResult transaction) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            String item = transaction.getShopItem().getItem().getType().toString();
            int itemCount = transaction.getAmount();
            double sellPrice = transaction.getPrice() / itemCount;
            ps = conn.prepareStatement("INSERT INTO shop_sales VALUES(\"" + item + "\", " + itemCount + ", date('now')\n" + ", " + sellPrice + ")" +
                    "  ON CONFLICT(item_name,transaction_date) DO UPDATE SET amount=amount+" + itemCount + ";");
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

    }

    public ResultSet executeQuery(String query) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public boolean dumpData() {
        Connection conn = null;
        PreparedStatement ps = null;
        File dumpFile = new File(plugin.getDataFolder(), "dump.xlsx");
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + ";");
            ResultSet rs = ps.executeQuery();


            XSSFWorkbook dumpBook = new XSSFWorkbook();
            XSSFSheet sheet = dumpBook.createSheet("all data");
            XSSFRow row = sheet.createRow(0);
            XSSFCell cell;
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String name = rsmd.getColumnName(i + 1);
                cell = row.createCell(i);
                cell.setCellValue(name);
            }
            int i = 1;
            while (rs.next()) {
                row = sheet.createRow(i);
                cell = row.createCell(0);
                cell.setCellValue(rs.getString("item_name"));
                cell = row.createCell(1);
                cell.setCellValue(rs.getString("amount"));
                cell = row.createCell(2);
                cell.setCellValue(rs.getString("transaction_date"));
                cell = row.createCell(3);
                cell.setCellValue(rs.getString("sell_price"));
                i++;
            }
            FileOutputStream out = new FileOutputStream(dumpFile);
            dumpBook.write(out);
            out.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
    @Deprecated
    public boolean report() {
        Connection conn = null;
        PreparedStatement ps = null;
        File dumpFile = new File(plugin.getDataFolder(), "report.xlsx");
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("select item_name, sum(amount) as items_sold, sum(sell_price * amount) as total_value from shop_sales \n" +
                    "where(transaction_date >= date(CURRENT_DATE, '-7 days'))\n" +
                    "GROUP BY item_name;");
            ResultSet rs = ps.executeQuery();


            XSSFWorkbook dumpBook = new XSSFWorkbook();
            XSSFSheet sheet = dumpBook.createSheet("report");
            XSSFRow row = sheet.createRow(0);
            XSSFCell cell;
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String name = rsmd.getColumnName(i + 1);
                cell = row.createCell(i);
                cell.setCellValue(name);
            }
            int i = 1;
            while (rs.next()) {
                row = sheet.createRow(i);
                cell = row.createCell(0);
                cell.setCellValue(rs.getString("item_name"));
                cell = row.createCell(1);
                cell.setCellValue(rs.getString("items_sold"));
                cell = row.createCell(2);
                cell.setCellValue(rs.getString("total_value"));
                i++;
            }
            FileOutputStream out = new FileOutputStream(dumpFile);
            dumpBook.write(out);
            out.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Deprecated
    public boolean itemReport(String item) {
        Connection conn = null;
        PreparedStatement ps = null;
        File dumpFile = new File(plugin.getDataFolder(), item + ".xlsx");
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("select transaction_date, sum(amount) as items_bought, sum(sell_price * amount) as total_value from shop_sales \n" +
                    "WHERE(item_name like '" + item + "')\n" +
                    "GROUP BY transaction_date;");
            ResultSet rs = ps.executeQuery();


            XSSFWorkbook dumpBook = new XSSFWorkbook();
            XSSFSheet sheet = dumpBook.createSheet(item);
            XSSFRow row = sheet.createRow(0);
            XSSFCell cell;
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String name = rsmd.getColumnName(i + 1);
                cell = row.createCell(i);
                cell.setCellValue(name);
            }
            int i = 1;
            while (rs.next()) {
                row = sheet.createRow(i);
                cell = row.createCell(0);
                cell.setCellValue(rs.getString("transaction_date"));
                cell = row.createCell(1);
                cell.setCellValue(rs.getString("items_bought"));
                cell = row.createCell(2);
                cell.setCellValue(rs.getString("total_value"));
                i++;
            }
            FileOutputStream out = new FileOutputStream(dumpFile);
            dumpBook.write(out);
            out.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

