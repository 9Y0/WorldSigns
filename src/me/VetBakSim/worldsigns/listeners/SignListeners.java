/**
 Copyright (c) 22 jun. 2016 Simcha van Collem

 The MIT License (MIT)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package me.VetBakSim.worldsigns.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.VetBakSim.worldsigns.SignManager;
import me.VetBakSim.worldsigns.WorldSign;

public class SignListeners implements Listener {
	private Map<Location, WorldSign> brokenOnce;

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (ChatColor.stripColor(e.getLine(0)).equalsIgnoreCase("[WorldSign]")) {
			Player p = e.getPlayer();
			if (!p.hasPermission("worldsigns.add")) {
				p.sendMessage(ChatColor.RED + "Je mag niet een WorldSing toevoegen!");
				return;
			}
			World worldToSendTo = Bukkit.getServer().getWorld(e.getLine(1));
			if (worldToSendTo == null) {
				p.sendMessage(ChatColor.RED + e.getLine(1) + " is niet een wereld in deze server!");
				return;
			}
			SignManager.getInstance().addSign((Sign) e.getBlock().getState(), worldToSendTo);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Block b = e.getClickedBlock();
		if ((b.getType() != Material.SIGN) && (b.getType() != Material.WALL_SIGN)
				&& (b.getType() != Material.SIGN_POST)) {
			return;
		}
		Player p = e.getPlayer();
		for (WorldSign wsign : SignManager.getInstance().getSigns().values()) {
			if (wsign.getSign().getLocation().equals(b.getLocation())) {
				World worldToSendTo = wsign.getWorldToSendTo();
				p.teleport(worldToSendTo.getSpawnLocation());
				p.sendMessage(ChatColor.GREEN + "Je wordt naar de wereld " + ChatColor.DARK_GREEN
						+ worldToSendTo.getName() + ChatColor.GREEN + " gestuurd!");
				break;
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (!p.hasPermission("worldsigns.remove")) {
			p.sendMessage(ChatColor.RED + "Jij mag deze sign niet weghalen!");
			return;
		}
		Block b = e.getBlock();
		if ((b.getType() != Material.SIGN) && (b.getType() != Material.WALL_SIGN)
				&& (b.getType() != Material.SIGN_POST)) {
			return;
		}
		if (!SignManager.getInstance().getSigns().containsKey(b.getLocation())) {
			return;
		}
		if (this.brokenOnce == null) {
			this.brokenOnce = new HashMap<>();
		}
		if (this.brokenOnce.containsKey(e.getBlock().getLocation())) {
			WorldSign wsign = (WorldSign) SignManager.getInstance().getSigns().get(e.getBlock().getLocation());
			wsign.remove();
		} else {
			WorldSign wsign = SignManager.getInstance().getSign((Sign) e.getBlock().getState());
			this.brokenOnce.put(wsign.getSign().getLocation(), wsign);

			e.setCancelled(true);

			e.getPlayer().sendMessage(ChatColor.GOLD
					+ "Weet je zeker dat je deze worldsign wil weghalen? Zoja, sloop hem dan nog een keer!");
		}
	}
}
