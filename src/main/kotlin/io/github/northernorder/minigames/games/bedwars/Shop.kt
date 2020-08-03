package io.github.northernorder.minigames.games.bedwars

import io.github.northernorder.minigames.utils.MCPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class Shop(name: String) {
    val inv: Inventory = Bukkit.createInventory(null, 54, name)

    abstract fun buy(purchase: ItemStack, mcPlayer: MCPlayer)

    internal fun buyItem(cost: Cost, mcPlayer: MCPlayer, callback: () -> Unit) {
        val moneyItem = when (cost.type) {
            Money.IRON -> Material.IRON_INGOT
            Money.GOLD -> Material.GOLD_INGOT
            Money.EMERALD -> Material.EMERALD
            Money.DIAMOND -> Material.DIAMOND
        }

        val playerInventory = mcPlayer.inventory
        val moneyItemSlots = playerInventory.all(moneyItem)
        var inInventory = 0

        for (stack in moneyItemSlots.values) {
            inInventory += stack.amount
        }

        var toPay: Int = cost.cost
        if (toPay > inInventory) {
            mcPlayer.sendMessage(String.format("Not enough money. %s more needed", (inInventory - toPay) * -1))
            return
        }

        for ((slot, item) in moneyItemSlots) {
            val amount = item.amount
            var allPaid = false

            when {
                toPay - amount == 0 -> {
                    mcPlayer.sendMessage(String.format("Exact stack, %s", toPay))
                    playerInventory.clear(slot)
                    allPaid = true
                }
                toPay - amount > 0 -> {
                    mcPlayer.sendMessage(String.format("Not enough yet, %s", amount))
                    toPay -= amount
                    playerInventory.clear(slot)
                }
                else -> {
                    mcPlayer.sendMessage(String.format("More than enough, %s", amount - toPay))
                    playerInventory.setItem(slot, item.asQuantity(amount - toPay))
                    allPaid = true
                }
            }

            if (allPaid) break
        }
        callback()
        updateShop()
    }

    internal abstract fun updateShop()
}