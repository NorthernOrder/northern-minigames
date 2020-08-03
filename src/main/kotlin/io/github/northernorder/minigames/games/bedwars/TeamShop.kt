package io.github.northernorder.minigames.games.bedwars

import io.github.northernorder.minigames.utils.ItemBuilder
import io.github.northernorder.minigames.utils.MCPlayer
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.pow

class TeamShop(private val team: Team) : Shop("Upgrade Shop") {
    companion object Static : ShopStatic {
        private val SOLD_OUT_PROTECTION = ItemBuilder(
            Material.BARRIER
        ).name("Protection (Sold out)").lore("Protection IV: Active").build()
        private val SOLD_OUT_FORGE = ItemBuilder(Material.BARRIER)
            .name("Forge (Sold out)").lore("200% Generation: Active").build()
        private val SOLD_OUT_SHARPNESS = ItemBuilder(Material.BARRIER)
            .name("Sharpness (Sold out)").lore("Sharpness I: Active").build()
//        private val SOLD_OUT_HEAL = ItemBuilder(Material.BARRIER)
//            .name("Healing Pool (Sold out)").build()
//        private val SOLD_OUT_TRAP = ItemBuilder(Material.BARRIER)
//            .name("Mining Fatigue Trap (Sold out)").build()

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
            add(Material.IRON_SWORD).name("Sharpness (Permanent)").styledLore(
                ComponentBuilder("Sharpness I").create()
            ).cost(Money.DIAMOND, 4).done()
            add(Material.DIAMOND_CHESTPLATE).name("Protection (Permanent)").styledLore(
                ComponentBuilder("Tier 1: Protection I, ").append("2 Diamonds").color(ChatColor.AQUA).create(),
                ComponentBuilder("Tier 2: Protection II, ").append("4 Diamonds").color(ChatColor.AQUA).create(),
                ComponentBuilder("Tier 3: Protection III, ").append("6 Diamonds").color(ChatColor.AQUA).create(),
                ComponentBuilder("Tier 4: Protection IV, ").append("8 Diamonds").color(ChatColor.AQUA).create()
            ).done()
            add(Material.ANVIL).name("Forge").styledLore(
                ComponentBuilder("Tier 1: 50% Generation, ").append("2 Diamonds").color(ChatColor.AQUA).create(),
                ComponentBuilder("Tier 2: 100% Generation, ").append("4 Diamonds").color(ChatColor.AQUA).create(),
                ComponentBuilder("Tier 3: Emeralds & 150% Generation, ").append("8 Diamonds").color(ChatColor.AQUA)
                    .create(),
                ComponentBuilder("Tier 4: 200% Generation, ").append("16 Diamonds").color(ChatColor.AQUA).create()
            ).done()
//            add(Material.BEACON).name("Healing Pool").styledLore(
//                ComponentBuilder("Health regeneration inside own island").create()
//            ).cost(Money.DIAMOND, 3).done()
//            add(Material.FEATHER).name("Mining Fatigue Trap").styledLore(
//                ComponentBuilder("Gives an enemy mining fatigue for 10 seconds").create()
//            ).cost(Money.DIAMOND, 1).done()
        }
    }

    init {
        inv.contents = items.toTypedArray()
    }

    override fun buy(purchase: ItemStack, mcPlayer: MCPlayer) {
        val type = purchase.type

        val buyEffect: () -> Unit
        var itemCost = costs[type]
        val upgrade = Upgrade.fromMaterial(type) ?: return

        when (upgrade) {
            Upgrade.SHARPNESS -> buyEffect = { team.upgrades.sharpness = true }
            Upgrade.PROTECTION -> {
                buyEffect = {
                    team.upgrades.protection++
                    team.players.forEach { player -> player.updateArmorSlots() }
                }
                itemCost = Cost(Money.DIAMOND, (team.upgrades.protection + 1) * 2)
            }
            Upgrade.FORGE -> {
                buyEffect = { team.upgrades.forge++ }
                itemCost = Cost(Money.DIAMOND, 2.0.pow((team.upgrades.forge + 1).toDouble()).toInt())
            }
            Upgrade.HEAL -> buyEffect = { team.upgrades.heal = true }
            Upgrade.TRAP -> buyEffect = { team.upgrades.trap = true }
        }

        buyItem(itemCost!!, mcPlayer, buyEffect)
    }

    fun reset() {
        inv.contents = items.toTypedArray()
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
            Material.DIAMOND_CHESTPLATE -> updateProtection(itemStack, index)
            Material.ANVIL -> updateForge(itemStack, index)
            Material.IRON_SWORD -> if (team.upgrades.sharpness) {
                inv.setItem(index, SOLD_OUT_SHARPNESS)
            }
//            Material.BEACON -> if (team.upgrades.heal) {
//                inv.setItem(index, SOLD_OUT_HEAL)
//            }
//            Material.FEATHER -> if (team.upgrades.trap) {
//                inv.setItem(index, SOLD_OUT_TRAP)
//            }
            else -> {
                // nothing else should get updated
            }
        }
    }

    private fun updateProtection(itemStack: ItemStack, index: Int) {
        val protection = team.upgrades.protection
        if (protection == 4) {
            inv.setItem(index, SOLD_OUT_PROTECTION)
            return
        }
        if (itemStack.amount != protection + 1) {
            itemStack.add()
        }
    }

    private fun updateForge(itemStack: ItemStack, index: Int) {
        val forge = team.upgrades.forge
        if (forge == 4) {
            inv.setItem(index, SOLD_OUT_FORGE)
            return
        }
        if (itemStack.amount != forge + 1) {
            itemStack.add()
        }
    }
}