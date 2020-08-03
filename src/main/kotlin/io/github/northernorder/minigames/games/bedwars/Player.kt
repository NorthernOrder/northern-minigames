package io.github.northernorder.minigames.games.bedwars

import io.github.northernorder.minigames.common.BasePlayer
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.LeatherArmorMeta
import java.util.*
import org.bukkit.entity.Player as MCPlayer

class Player(mcPlayer: MCPlayer, var team: Team, val spawn: Location) : BasePlayer(mcPlayer) {
    val shop = PlayerShop(this, team.color)
    var kills = 0
    var bedsBroken = 0
    var finalKills = 0
    var armor = 0
    var dead = false

    fun updateArmorSlots() {
        val inventory: PlayerInventory = mcPlayer.inventory
        val armorColor: Color = this.team.color.color

        val helmet = ItemStack(Material.LEATHER_HELMET)
        val helmetMeta = helmet.itemMeta as LeatherArmorMeta
        helmetMeta.setColor(armorColor)
        if (team.upgrades.protection > 0) {
            helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, team.upgrades.protection, false)
        }
        helmetMeta.isUnbreakable = true
        helmet.itemMeta = helmetMeta

        val chestplate = ItemStack(Material.LEATHER_CHESTPLATE)
        val chestplateMeta = chestplate.itemMeta as LeatherArmorMeta
        chestplateMeta.setColor(armorColor)
        if (team.upgrades.protection > 0) {
            chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, team.upgrades.protection, false)
        }
        chestplateMeta.isUnbreakable = true
        chestplate.itemMeta = chestplateMeta

        val leggingsMat: Material
        val bootsMat: Material
        when (armor) {
            0 -> {
                leggingsMat = Material.LEATHER_LEGGINGS
                bootsMat = Material.LEATHER_BOOTS
            }
            1 -> {
                leggingsMat = Material.CHAINMAIL_LEGGINGS
                bootsMat = Material.CHAINMAIL_BOOTS
            }
            2 -> {
                leggingsMat = Material.IRON_LEGGINGS
                bootsMat = Material.IRON_BOOTS
            }
            3 -> {
                leggingsMat = Material.DIAMOND_LEGGINGS
                bootsMat = Material.DIAMOND_BOOTS
            }
            else -> {
                leggingsMat = Material.AIR
                bootsMat = Material.AIR
            }
        }

        val leggings = ItemStack(leggingsMat)
        val leggingsMeta = leggings.itemMeta
        if (armor == 0) {
            (leggingsMeta as LeatherArmorMeta).setColor(armorColor)
        }
        if (team.upgrades.protection > 0) {
            leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, team.upgrades.protection, false)
        }
        leggingsMeta.isUnbreakable = true
        leggings.itemMeta = leggingsMeta

        val boots = ItemStack(bootsMat)
        val bootsMeta = boots.itemMeta
        if (armor == 0) {
            (bootsMeta as LeatherArmorMeta).setColor(armorColor)
        }
        if (team.upgrades.protection > 0) {
            bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, team.upgrades.protection, false)
        }
        bootsMeta.isUnbreakable = true
        boots.itemMeta = bootsMeta

        val armorSet = arrayOf(boots, leggings, chestplate, helmet)
        inventory.setArmorContents(armorSet)
    }

    fun giveDefaultInventory() {
        val inventory: PlayerInventory = mcPlayer.inventory
        updateArmorSlots()
        val sword = ItemStack(Material.WOODEN_SWORD)
        val swordMeta = sword.itemMeta
        swordMeta.isUnbreakable = true
        if (team.upgrades.sharpness) {
            swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false)
        }
        swordMeta.addAttributeModifier(
            Attribute.GENERIC_ATTACK_SPEED,
            AttributeModifier(
                UUID.randomUUID(),
                "generic.attackSpeed",
                1024.0,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
            )
        )
        sword.itemMeta = swordMeta
        inventory.addItem(sword)
    }
}