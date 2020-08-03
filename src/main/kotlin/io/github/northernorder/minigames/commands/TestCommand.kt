package io.github.northernorder.minigames.commands

import io.github.northernorder.minigames.common.Command
import io.github.northernorder.minigames.utils.MCPlayer
import org.bukkit.command.CommandSender

class TestCommand : Command("test") {
    override fun command(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (isNotPlayer(sender)) return true
        val player = sender as MCPlayer

        val minigame = getCurrentMinigame(player) ?: return true
        val testables = minigame.testables

        if (args.isEmpty() || !testables.keys.stream().anyMatch { testable -> testable == args[0] }) {
            player.sendMessage("Not a known testable")
            return true
        }

        val testable = testables[args[0]] ?: return true
        testable(player)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val minigame = getCurrentMinigame(null) ?: return super.onTabComplete(sender, command, alias, args)
        val testables = minigame.testables.keys.toMutableList()
        if (args.isEmpty() || !testables.stream().anyMatch { testable -> testable == args[0] }) {
            return testables
        }
        return mutableListOf()
    }
}