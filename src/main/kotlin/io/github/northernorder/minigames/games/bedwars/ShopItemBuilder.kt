package io.github.northernorder.minigames.games.bedwars

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShopItemBuilder(material: Material, private val shop: ShopStatic) {
    private var item = ItemStack(material)
    private var meta = item.itemMeta
    private val lore: MutableList<Array<BaseComponent>> = mutableListOf()

    fun done() {
        meta.loreComponents = lore
        item.itemMeta = meta
        shop.done(item)
    }

    fun name(name: String): ShopItemBuilder {
        meta.setDisplayName(name)
        return this
    }

    fun stack(size: Int): ShopItemBuilder {
        item.add(if (size - 1 == 0) 1 else size - 1)
        return this
    }

    fun lore(text: String): ShopItemBuilder {
        lore.add(ComponentBuilder(text).create())
        return this
    }

    fun lore(vararg loreTexts: String): ShopItemBuilder {
        for (text in loreTexts) {
            lore.add(ComponentBuilder(text).create())
        }
        return this
    }

    fun styledLore(component: Array<BaseComponent>): ShopItemBuilder {
        lore.add(component)
        return this
    }

    fun styledLore(vararg components: Array<BaseComponent>): ShopItemBuilder {
        lore.addAll(components)
        return this
    }

    fun cost(
        type: Money,
        cost: Int
    ): ShopItemBuilder {
        var text = ""
        var color: ChatColor? = null
        when (type) {
            Money.IRON -> {
                text = "Iron"
                color = ChatColor.WHITE
            }
            Money.GOLD -> {
                text = "Gold"
                color = ChatColor.YELLOW
            }
            Money.EMERALD -> {
                text = "Emeralds"
                color = ChatColor.GREEN
            }
            Money.DIAMOND -> {
                text = "Diamonds"
                color = ChatColor.AQUA
            }
        }
        lore.add(ComponentBuilder("Cost:").bold(true).create())
        lore.add(ComponentBuilder(String.format("%s %s", cost, text)).color(color).create())
        shop.setCost(Cost(type, cost), item.type)
        return this
    }
}