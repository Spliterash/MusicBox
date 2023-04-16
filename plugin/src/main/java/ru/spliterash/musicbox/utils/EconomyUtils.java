package ru.spliterash.musicbox.utils;

import lombok.experimental.UtilityClass;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.spliterash.musicbox.Lang;
import ru.spliterash.musicbox.MusicBox;

@UtilityClass
public class EconomyUtils {
    private final Economy eco;

    static {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
            eco = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
        else
            eco = null;
    }

    public boolean isEnable() {
        return MusicBox.getInstance().getConfigObject().getEconomy().isEnable();
    }

    public double getDiscPrice() {
        return MusicBox.getInstance().getConfigObject().getEconomy().getPrice();
    }

    public double getBalance(Player player) {
        return eco.getBalance(player);
    }

    public boolean hasMoney(Player player, double money) {
        double currentMoney = getBalance(player);
        return (currentMoney - money) >= 0;
    }

    public boolean buyNoMessage(Player player, double price) {
        return eco.withdrawPlayer(player, price).transactionSuccess();
    }


    public boolean canBuy(Player player, double price) {
        double currentMoney = getBalance(player);
        double moneyLeft = currentMoney - price;
        if (moneyLeft >= 0) {
            return true;
        } else {
            moneyLeft *= -1;
            player.sendMessage(Lang.NO_HAS_MONEY.toString("{amount}", String.valueOf(moneyLeft)));
            return false;
        }
    }
}
