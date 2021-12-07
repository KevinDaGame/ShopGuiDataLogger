package com.github.kevindagame;

import net.brcdev.shopgui.event.ShopPostTransactionEvent;
import net.brcdev.shopgui.event.ShopPreTransactionEvent;
import net.brcdev.shopgui.shop.ShopManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TransactionEvent implements Listener {
    private ShopGuiDataLogger main;

    public TransactionEvent(ShopGuiDataLogger main) {
        this.main = main;
    }

    @EventHandler
    public void onShopTrans(ShopPostTransactionEvent event) {
        main.getDB().addTransaction(event.getResult());
    }
}
