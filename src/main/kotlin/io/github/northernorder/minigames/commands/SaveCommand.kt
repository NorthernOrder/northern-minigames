package io.github.northernorder.minigames.commands

import io.github.northernorder.minigames.common.Command
import io.github.northernorder.minigames.utils.MCPlayer
import org.bukkit.command.CommandSender

class SaveCommand : Command("save") {
    override fun command(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (isNotPlayer(sender)) return true
        val player = sender as MCPlayer

        val minigame = getCurrentMinigame(player) ?: return true

        if (!isMapSelected(player)) return true

        var name: String? = null

        if (args.isNotEmpty() && args[0] != "") {
            name = args[0]
        }

        val succeeded = minigame.saveMap(name)

        if (!succeeded) {
            player.sendMessage("Saving failed")
        } else {
            player.sendMessage("Saving succeeded")
        }

        return true
    }
}