package com.github.kevindagame;

import com.github.kevindagame.database.Database;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class QueryHandler {

    private final FileConfiguration file;
    private final Database database;
    private final ShopGuiDataLogger plugin;

    public QueryHandler(FileConfiguration file, Database database, ShopGuiDataLogger plugin) {
        this.file = file;
        this.database = database;
        this.plugin = plugin;
    }

    public String runQuery(String[] arguments) {
        String query = file.getString(arguments[0] + ".query");
        if (query == null) return "Sorry, No query with the name " + arguments[0] + " could be found";

        int argPlaceHolders = getPlaceholderOccurences(query);
        if (argPlaceHolders + 1 != arguments.length)
            return "You gave an incorrect amount of arguments! Expected: " + argPlaceHolders + ", recieved: " + (arguments.length - 1);
        query = replacePlaceHolders(query, arguments);
        ResultSet rs = database.executeQuery(query);
        if (rs == null) return "Sorry, there was no result";
        File outputFile = new File(plugin.getDataFolder(), arguments[0] + ".xlsx");
        XSSFWorkbook result = new XSSFWorkbook();
        XSSFSheet sheet = result.createSheet("result");
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell;
        ResultSetMetaData rsmd;
        try {
            rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                String colName = rsmd.getColumnName(i + 1);
                cell = row.createCell(i);
                cell.setCellValue(colName);
            }
            int i = 1;
            while (rs.next()) {
                row = sheet.createRow(i);
                for (int j = 0; j < columnCount; j++) {
                    cell = row.createCell(j);
                    cell.setCellValue(rs.getString(j + 1));
                }
                i++;
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            result.write(out);
            out.close();
            return "Succesfully created file " + arguments[0] + ".xlsx in the plugin's folder!";
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            return "An error has occured, please see the console for more details";
        }
    }

    private String replacePlaceHolders(String query, String[] arguments) {
        query =query.replace("%table%", database.table);
        for (int i = 1; i < arguments.length; i++) {
            query = query.replace("%param%", arguments[i]);
        }
        return query;
    }

    private int getPlaceholderOccurences(String query) {
        String word = "%param%";
        return StringUtils.countMatches(query, word);
    }
}