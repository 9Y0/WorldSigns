package me.VetBakSim.worldsigns;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SignManager {

	private static SignManager instance = new SignManager();
	private Map<Location, WorldSign> signs;

	private SignManager() {
		this.signs = new HashMap<>();
	}

	public WorldSign addSign(Sign sign, World worldToSendTo) {
		WorldSign wsign = new WorldSign(sign, worldToSendTo);
		wsign.runTaskTimer(Main.getInstance(), 0L, 40L);
		this.signs.put(sign.getLocation(), wsign);
		return wsign;
	}

	public WorldSign getWorldSign(Sign sign) {
		Location loc = sign.getLocation();
		if (this.signs.containsKey(loc))
			return this.signs.get(loc);
		return null;
	}

	public void removeAllSigns() {
		for (WorldSign wsign : this.signs.values()) {
			wsign.remove();
		}
	}

	public void saveSigns() {
		int index = 0;
		FileConfiguration cfg = ConfigManager.getSigns().getConfig();

		for (WorldSign wsign : this.signs.values()) {
			ConfigurationSection s = cfg.createSection(index++ + "");
			ConfigurationSection sl = s.createSection("locatie");
			Location loc = wsign.getSign().getLocation();
			sl.set("x", loc.getX());
			sl.set("y", loc.getY());
			sl.set("z", loc.getZ());
			sl.set("wereld", loc.getWorld().getName());
			s.set("naarToeStuurWereld", wsign.getWorldToSendTo().getName());
		}

		ConfigManager.getSigns().save();
	}

	public void loadSigns() {
		FileConfiguration cfg = ConfigManager.getSigns().getConfig();
		for (String id : cfg.getKeys(false)) {
			ConfigurationSection s = cfg.getConfigurationSection(id);
			ConfigurationSection sl = s.getConfigurationSection("locatie");

			World world = Bukkit.getServer().getWorld(sl.getString("wereld"));
			World worldToSendTo = Bukkit.getServer().getWorld(s.getString("naarToeStuurWereld"));
			if (world == null) {
				System.err.println(sl.getString("wereld") + " is een niet bestaande wereld!");
				continue;
			} else if (worldToSendTo == null) {
				System.err.println(s.getString("naarToeStuurWereld") + " is een niet betsaande wereld!");
				continue;
			}

			Location loc = new Location(worldToSendTo, sl.getDouble("x"), sl.getDouble("y"), sl.getDouble("z"));
			Block b = loc.getBlock();
			if (b == null || (b.getType() != Material.SIGN && b.getType() != Material.WALL_SIGN
					&& b.getType() != Material.SIGN_POST)) {
				System.err.println("Een worldsign is verwijderd, vanwege een missende sign, op locatie:");
				System.err.println("x: " + sl.getInt("x"));
				System.err.println("y: " + sl.getInt("y"));
				System.err.println("z: " + sl.getInt("z"));
				System.err.println("wereld: " + sl.getString("wereld"));
				return;
			}

			Sign sign = (Sign) b.getState();

			this.addSign(sign, worldToSendTo);

			cfg.set(id, null);
		}

		ConfigManager.getSigns().save();
	}

	public static SignManager getInstance() {
		return instance;
	}

	public Map<Location, WorldSign> getSigns() {
		return signs;
	}

}
