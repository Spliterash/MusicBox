package ru.spliterash.musicbox.utils;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UtilityClass
public class ItemUtils {
    private final Enchantment enchantment = XEnchantment.PROTECTION_EXPLOSIONS.getEnchant();

    public ItemStack glow(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(enchantment, 9999, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack createStack(XMaterial material, String name, @Nullable List<String> lore) {
        ItemStack stack = material.parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null)
            meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public boolean isGlow(ItemStack item) {
        return item.getEnchantments().containsKey(enchantment);
    }

    public ItemStack unGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.removeEnchant(enchantment);
        item.setItemMeta(meta);
        return item;
    }

    public List<XMaterial> getEndWith(String endWith) {
        return Arrays.stream(XMaterial.values())
                .filter(s -> s.name().endsWith(endWith))
                .filter(XMaterial::isSupported)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет два массива с предметами на сходство
     *
     * @param c1 Первый массив с предметами
     * @param c2 Второй массив с предметами
     * @return Одинаковые ли массивы
     */
    public boolean isSimilar(ItemStack[] c1, ItemStack[] c2) {
        if (c1 == null || c2 == null)
            return false;
        if (c1.length != c2.length)
            return false;
        for (int i = 0; i < c1.length; i++) {
            ItemStack i1 = c1[i];
            ItemStack i2 = c2[i];
            if (i1 == null && i2 == null)
                continue;
            if (i1 == i2)
                continue;
            if (i1 == null || i2 == null)
                return false;
            if (!i1.isSimilar(i2))
                continue;
            return false;
        }
        return true;
    }

    /**
     * Убирает пробелы в инвентаря, выставляя всё в 1 линию
     */
    public void groupInventory(Inventory inventory) {
        BukkitUtils.checkPrimary();
        int current = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null)
                continue;
            if (i > current) {
                inventory.setItem(current, stack);
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
            current++;
        }
    }

    /**
     * Считает сколько слотов занято
     */
    public int getFilledSlots(Inventory inventory) {
        int notEmpty = 0;
        for (ItemStack stack : inventory) {
            if (stack != null && !stack.getType().equals(Material.AIR)) {
                notEmpty++;
            }
        }
        return notEmpty;
    }

    /**
     * Сдвигает все ячейки инвентаря на N ячеек
     * Пример для 1
     * 0|1|2|3 -> 3|0|1|2 -> 2|3|0|1 -> 1|2|3|0
     * <p>
     * <p>
     * PS. Алгоритм стырил(зато честно)
     *
     * @param inventory Инвентарь который надо сдвинуть
     * @param shift     На сколько будет сдвинут инвентарь
     */
    public void shiftInventory(Inventory inventory, int shift) {
        int currentIndex, movedIndex;
        ItemStack buffer;
        int invLength = inventory.getSize();
        int greatest = greatestCommonDivisor(Math.abs(shift), invLength);
        for (int i = 0; i < greatest; i++) {
            buffer = inventory.getItem(i);
            currentIndex = i;
            if (shift > 0) {
                while (true) {
                    movedIndex = currentIndex + shift;
                    if (movedIndex >= invLength)
                        movedIndex = movedIndex - invLength;
                    if (movedIndex == i)
                        break;
                    inventory.setItem(currentIndex, inventory.getItem(movedIndex));
                    currentIndex = movedIndex;
                }
            } else if (shift < 0) {
                while (true) {
                    movedIndex = currentIndex + shift;
                    if (movedIndex < 0)
                        movedIndex = invLength + movedIndex;
                    if (movedIndex == i)
                        break;
                    inventory.setItem(currentIndex, inventory.getItem(movedIndex));
                    currentIndex = movedIndex;
                }
            }

            inventory.setItem(currentIndex, buffer);
        }
    }

    private int greatestCommonDivisor(int a, int b) {
        if (b == 0)
            return a;
        else
            return greatestCommonDivisor(b, a % b);
    }

    /**
     * Ищет первый попавшийся айтетем в инвентаре и возращает его индекс
     *
     * @param inv       Инвентарь
     * @param predicate Чекалка предметов
     */
    public int findItem(Inventory inv, Predicate<ItemStack> predicate) {
        for (ListIterator<ItemStack> iter = inv.iterator(); iter.hasNext(); ) {
            int index = iter.nextIndex();
            ItemStack stack = iter.next();
            if (predicate.test(stack))
                return index;
        }
        return -1;
    }
}
