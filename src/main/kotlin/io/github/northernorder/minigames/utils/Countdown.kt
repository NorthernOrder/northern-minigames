package io.github.northernorder.minigames.utils

import com.destroystokyo.paper.Title
import io.github.northernorder.minigames.Minigames
import org.bukkit.Bukkit

class Countdown(var title: String, var callback: () -> Unit, private val playerList: List<MCPlayer>) : Runnable {
    var time = 5

    fun schedule() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.instance, this, 2)
    }

    override fun run() {
        if (time > 0) {
            playerList.forEach { p -> p.sendTitle(Title.builder().title(String.format("%s %s", title, time)).build()) }
            time--
            Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.instance, this, 20)
            return
        }
        callback()
        playerList.forEach { p -> p.hideTitle() }
    }
}