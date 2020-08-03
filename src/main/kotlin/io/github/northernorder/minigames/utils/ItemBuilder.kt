package io.github.northernorder.minigames.utils

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemBuilder(material: Material) {
    private var item = ItemStack(material)
    private var meta = item.itemMeta
    private val lore: MutableList<Array<BaseComponent>> = mutableListOf()

    fun build(): ItemStack {
        item.itemMeta = meta
        return item
    }

    fun name(name: String): ItemBuilder {
        meta.setDisplayName(name)
        return this
    }

    fun stack(size: Int): ItemBuilder {
        item.add(if (size - 1 == 0) 1 else size - 1)
        return this
    }

    fun lore(text: String): ItemBuilder {
        lore.add(ComponentBuilder(text).create())
        return this
    }

    fun lore(vararg loreTexts: String): ItemBuilder {
        for (text in loreTexts) {
            lore.add(ComponentBuilder(text).create())
        }
        return this
    }

    fun styledLore(component: Array<BaseComponent>): ItemBuilder {
        lore.add(component)
        return this
    }

    fun styledLore(vararg components: Array<BaseComponent>): ItemBuilder {
        lore.addAll(components)
        return this
    }
}