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
				p.sendMessage(ChatColor.RED + "Je mag niet een WorldSign toevoegen!");
				e.getBlock().breakNaturally();
				return;
			}

			World worldToSendTo = Bukkit.getServer().getWorld(e.getLine(1));
			if (worldToSendTo == null) {
				p.sendMessage(ChatColor.RED + e.getLine(1) + " is niet een wereld in deze server!");
				return;
			}

			if (!isInteger(e.getLine(2)) || Integer.parseInt(e.getLine(2)) < 1 || Integer.parseInt(e.getLine(2)) > 4) {
				p.sendMessage(ChatColor.RED
						+ "Usage:\nRegel 1: [WorldSign]\nRegel 2: Wereld naam\nRegel 3: Zone nummer(1t/m4)");
				e.getBlock().breakNaturally();
				return;
			}

			SignManager.getInstance().addSign((Sign) e.getBlock().getState(), worldToSendTo,
					Integer.parseInt(e.getLine(2)));
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		Block b = e.getClickedBlock();
		if (b.getType() != Material.SIGN && b.getType() != Material.SIGN_POST && b.getType() != Material.WALL_SIGN)
			return;

		Player p = e.getPlayer();
		for (WorldSign wsign : SignManager.getInstance().getSigns().values()) {
			if (wsign.getSign().getLocation().equals(b.getLocation())) {
				World worldToSendTo = wsign.getWorldToSendTo();
				if (!p.hasPermission("worldsigns.zone." + wsign.getZone())) {
					p.sendMessage(ChatColor.RED + "Je mag niet naar een wereld in deze zone gaan!");
					return;
				}
				p.teleport(worldToSendTo.getSpawnLocation());
				p.sendMessage(ChatColor.GREEN + "Je bent naar de wereld " + ChatColor.DARK_GREEN
						+ worldToSendTo.getName() + ChatColor.GREEN + " gestuurd!");
				break;
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (b.getType() != Material.SIGN && b.getType() != Material.SIGN_POST && b.getType() != Material.WALL_SIGN)
			return;
		if (!SignManager.getInstance().getSigns().containsKey(b.getLocation()))
			return;

		Player p = e.getPlayer();
		if (!p.hasPermission("worldsigns.remove")) {
			p.sendMessage(ChatColor.RED + "Jij mag deze worldsign niet weghalen!");
			e.setCancelled(true);
			return;
		}

		if (this.brokenOnce == null)
			this.brokenOnce = new HashMap<>();
		if (this.brokenOnce.containsKey(b.getLocation())) {
			WorldSign wsign = SignManager.getInstance().getWorldSign((Sign) b.getState());
			wsign.remove();
		} else {
			WorldSign wsign = SignManager.getInstance().getWorldSign((Sign) b.getState());
			this.brokenOnce.put(b.getLocation(), wsign);

			e.setCancelled(true);
			p.sendMessage(ChatColor.GOLD
					+ "Weet je zeker dat je deze worldsign wil weghalen? Zoja, sloop hem dan nog een keer!");
		}
	}

	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
