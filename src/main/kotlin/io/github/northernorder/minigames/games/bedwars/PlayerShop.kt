package io.github.northernorder.minigames.games.bedwars

import io.github.northernorder.minigames.utils.ItemBuilder
import io.github.northernorder.minigames.utils.MCPlayer
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import java.util.*

class PlayerShop(val player: Player, color: Color) : Shop("Item Shop") {
    companion object Static : ShopStatic {
        private val SOLD_OUT_CHAIN_ARMOR = ItemBuilder(
            Material.BARRIER
        ).name("Chain Armor (Permanent) (Sold out)").build()
        private val SOLD_OUT_IRON_ARMOR = ItemBuilder(
            Material.BARRIER
        ).name("Iron Armor (Permanent) (Sold out)").build()
        private val SOLD_OUT_DIAMOND_ARMOR = ItemBuilder(
            Material.BARRIER
        ).name("Diamond Armor (Permanent) (Sold out)").build()
        private val armors = listOf(
            Material.CHAINMAIL_BOOTS,
            Material.IRON_BOOTS,
            Material.DIAMOND_BOOTS
        )
        private val tools = listOf(
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.DIAMOND_SWORD,
            Material.SHEARS,
            Material.BOW,
            Material.DIAMOND_PICKAXE
        )
        private val blocks = listOf(Material.END_STONE, Material.OBSIDIAN)

        private val items = mutableListOf<ItemStack>()
        private val costs = EnumMap<Material, Cost>(Material::class.java)

        private fun add(material: Material): ShopItemBuilder {
            return ShopItemBuilder(material, this)
        }

        override fun done(item: ItemStack) {
            items.add(item)
        }

        override fun setCost(cost: Cost, material: Material) {
            costs[material] = cost
        }

        init {
            Color.values().forEach { color -> setCost(Cost(Money.IRON, 16), color.wool) }
            add(Material.END_STONE).cost(Money.IRON, 24).stack(12).done()
            add(Material.OBSIDIAN).cost(Money.EMERALD, 4).stack(4).done()
            add(Material.STONE_SWORD).cost(Money.IRON, 10).done()
            add(Material.IRON_SWORD).cost(Money.GOLD, 7).done()
            add(Material.DIAMOND_SWORD).cost(Money.EMERALD, 4).done()
            add(Material.CHAINMAIL_BOOTS).name("Chain Armor (Permanent)").cost(Money.IRON, 40).done()
            add(Material.IRON_BOOTS).name("Iron Armor (Permanent)").cost(Money.GOLD, 12).done()
            add(Material.DIAMOND_BOOTS).name("Diamond Armor (Permanent)").cost(Money.EMERALD, 6).done()
            // add(Material.FIRE_CHARGE).name("Fireball").cost(Money.Iron, 40).done();
            add(Material.GOLDEN_APPLE).cost(Money.GOLD, 3).done()
            add(Material.ENDER_PEARL).cost(Money.EMERALD, 4).done()
            add(Material.BOW).cost(Money.GOLD, 12).done()
            add(Material.ARROW).cost(Money.GOLD, 2).stack(8).done()
            add(Material.SHEARS).cost(Money.IRON, 20).done()
            add(Material.DIAMOND_PICKAXE).cost(Money.EMERALD, 2).done()
            // add(Material.TNT).cost(Money.Gold, 8).done();
        }
    }

    init {
        val shopItems = mutableListOf<ItemStack>()
        shopItems.add(
            ItemBuilder(color.wool).stack(16).styledLore(
                ComponentBuilder("Cost:").bold(true).create(),
                ComponentBuilder("4 Iron").color(ChatColor.WHITE).create()
            ).build()
        )
        shopItems.addAll(items)
        inv.contents = shopItems.toTypedArray()
    }

    override fun buy(purchase: ItemStack, mcPlayer: MCPlayer) {
        if (purchase.type == Material.BLACK_STAINED_GLASS_PANE || purchase.type == Material.BARRIER) return
        val meta = purchase.itemMeta

        meta.lore = ArrayList()
        purchase.itemMeta = meta

        val itemCost: Cost = costs[purchase.type]!!
        val type = purchase.type

        if (armors.stream().anyMatch { armor: Material -> armor == type }) {
            buyArmor(itemCost, type)
            return
        }

        if (tools.stream().anyMatch { tool: Material -> tool == type }) {
            buyTool(itemCost, purchase)
            return
        }

        if (type == player.team.color.wool || blocks.stream().anyMatch { block: Material -> block == type }) {
            buyBlock(itemCost, purchase)
            return
        }

        buyItem(itemCost, mcPlayer) { mcPlayer.inventory.addItem(purchase) }
    }

    private fun buyArmor(cost: Cost, type: Material) {
        val armor = armors.indexOf(type)
        if (armor != -1) {
            buyItem(cost, player.mcPlayer) {
                player.armor = armor + 1
                player.updateArmorSlots()
            }
        }
    }

    private fun buyTool(cost: Cost, purchase: ItemStack) {
        val type = purchase.type
        val meta = purchase.itemMeta
        meta.isUnbreakable = true

        if ((type == Material.STONE_SWORD || type == Material.IRON_SWORD || type == Material.DIAMOND_SWORD) && player.team.upgrades.sharpness) {
            meta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false)
        }

        purchase.itemMeta = meta
        buyItem(cost, player.mcPlayer) { player.mcPlayer.inventory.addItem(purchase) }
    }

    private fun buyBlock(cost: Cost, purchase: ItemStack) {
        var itemCost = cost
        val meta = purchase.itemMeta
        if (purchase.type == player.team.color.wool) {
            itemCost = Cost(Money.IRON, 4)
        }

        purchase.itemMeta = meta

        buyItem(itemCost, player.mcPlayer) { player.mcPlayer.inventory.addItem(purchase) }
    }

    override fun updateShop() {
        val itemStacks = inv.storageContents
        for (i in itemStacks.indices) {
            val itemStack = itemStacks[i] ?: continue
            updateItem(itemStack, i)
        }
    }

    private fun updateItem(itemStack: ItemStack, index: Int) {
        when (itemStack.type) {
            Material.CHAINMAIL_BOOTS -> if (player.armor >= 1) {
                inv.setItem(index, SOLD_OUT_CHAIN_ARMOR)
            }
            Material.IRON_BOOTS -> if (player.armor >= 2) {
                inv.setItem(index, SOLD_OUT_IRON_ARMOR)
            }
            Material.DIAMOND_BOOTS -> if (player.armor == 3) {
                inv.setItem(index, SOLD_OUT_DIAMOND_ARMOR)
            }
            else -> {
                // nothing else should get updated
            }
        }
    }
}