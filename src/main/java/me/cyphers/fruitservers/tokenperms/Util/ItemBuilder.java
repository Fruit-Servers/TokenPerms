package me.cyphers.fruitservers.tokenperms.Util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    /**
     * This final item to be built
     */
    private final ItemStack item;

    /**
     * Meta for the item. Stores Display Name and Lore
     */
    private ItemMeta itemMeta;

    /**
     * Lore for the item. An incremental list that gets added to by the builder
     */
    private final List<String> itemLore;

    /**
     * Creates the initial ItemStack
     * @param material the material for the ItemStack
     */
    public ItemBuilder(Material material) {

        this.item = new ItemStack(material, 1);
        this.itemMeta = item.getItemMeta();
        this.itemLore = new ArrayList<>();

    }

    /**
     * Creates the initial ItemStack from an existing ItemStack
     * @param itemStack the ItemStack to initialise the builder from
     */
    public ItemBuilder(ItemStack itemStack) {

        this.item = itemStack;
        this.itemMeta = itemStack.getItemMeta();
        if (itemMeta != null && itemMeta.getLore() != null) this.itemLore = itemMeta.getLore();
        else this.itemLore = new ArrayList<>();

    }

    /* Meta */

    /**
     * Overrides the ItemMeta with a new instance of meta. Intended to be used in conjunction with ItemMeta providers such as Player Heads
     * @param meta the meta to replace the existing meta with
     * @return the Builder instance
     */
    public ItemBuilder meta(ItemMeta meta) {
        this.itemMeta = meta;
        return this;
    }

    /*  Material and Amount  */
    /**
     * Add an amount to the ItemStack
     * @param amount the amount to be added
     * @return The Builder instance
     */
    public ItemBuilder amount(int amount) {

        this.item.setAmount(amount);
        return this;

    }

    /**
     * Add a material to the ItemStack
     * @param material the material to be added
     * @return The Builder instance
     */
    public ItemBuilder type(Material material) {

        this.item.setType(material);
        return this;

    }

    /*  Name and Lore  */
    /**
     * Add a name to the ItemStack. Uses '&' for minecraft colour formatting
     * @param name the name to be added
     * @return The Builder instance
     */
    public ItemBuilder name(String name) {

        this.itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;

    }

    /**
     * Add a single line of lore to the ItemStack. Uses '&' for minecraft colour formatting
     * @param lore the lore to be added
     * @return The Builder instance
     */
    public ItemBuilder lore(String lore) {

        this.itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));
        return this;

    }

    /**
     * Add multiple lines of lore to the ItemStack. Uses '&' for minecraft colour formatting
     * @param lore the lore to be added
     * @return The Builder instance
     */
    public ItemBuilder lore(Iterable<String> lore) {

        for (String loreLine : lore) {

            this.itemLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));

        }

        return this;

    }

    /*  Enchanting  */
    /**
     * Gives the item the enchanted look but hides the enchantment names
     * @return The Builder instance
     */
    public ItemBuilder enchant() {

        this.itemMeta.addEnchant(Enchantment.MENDING, 1, false);
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;

    }

    /**
     * Add an enchant to the ItemStack
     * @param enchantment the enchantment to be added
     * @param level the level of the enchant
     * @return The Builder instance
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {

        this.itemMeta.addEnchant(enchantment, level, true);
        return this;

    }

    /**
     * Sets the item to be unbreakable
     * @param showFlag whether to show that the item is unbreakable
     * @return The Builder instance
     */
    public ItemBuilder unbreakable(boolean showFlag) {

        this.itemMeta.setUnbreakable(true);
        if (showFlag) this.itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;

    }

    /*  Flags and Attributes  */

    /**
     * Adds an item flag to the item
     * @param flag the flag(s) to be added
     * @return the Builder instance
     */
    public ItemBuilder flag(ItemFlag... flag) {
        this.itemMeta.addItemFlags(flag);
        return this;
    }

    /**
     * Adds an attribute to the item
     * @param attribute the Attribute to add
     * @param modifier the modifier for the Attribute
     * @return the Builder instance
     */
    public ItemBuilder attribute(Attribute attribute, AttributeModifier modifier) {
        this.itemMeta.addAttributeModifier(attribute, modifier);
        return this;
    }

    /**
     * Builds the item
     * @return the item
     */
    public ItemStack build() {

        itemMeta.setLore(itemLore);
        item.setItemMeta(itemMeta);
        return item;

    }

}
