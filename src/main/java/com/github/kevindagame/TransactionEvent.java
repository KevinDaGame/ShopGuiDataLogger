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
        if(event.getResult().getShopAction() == ShopManager.ShopAction.SELL || event.getResult().getShopAction() == ShopManager.ShopAction.SELL_ALL){
            main.getDB().addTransaction(event.getResult());
        }
    }
}
