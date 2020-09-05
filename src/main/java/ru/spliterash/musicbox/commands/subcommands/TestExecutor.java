package ru.spliterash.musicbox.commands.subcommands;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import ru.spliterash.musicbox.utils.ItemUtils;

public class TestExecutor implements ru.spliterash.musicbox.commands.SubCommand {
    @Override
    public void execute(Player player, String[] args) {
        @NotNull Block b = player.getTargetBlock(null, 20);
        Chest chest = (Chest) b.getState();
        @NotNull Inventory inv = chest.getInventory();
        ItemUtils.shiftInventory(inv, 1);
    }
}
