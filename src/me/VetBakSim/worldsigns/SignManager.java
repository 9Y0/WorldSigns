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

	public static SignManager getInstance() {
		return instance;
	}

	private SignManager() {
		this.signs = new HashMap<>();
	}

	public void addSign(Sign sign, World worldToSendTo) {
		WorldSign wsign = new WorldSign(sign, worldToSendTo);
		wsign.runTaskTimer(Main.getPlugin(Main.class), 0L, 40L);
		this.signs.put(sign.getLocation(), wsign);
	}

	public WorldSign getSign(Sign sign) {
		Location loc = sign.getLocation();
		if (this.signs.containsKey(loc)) {
			return this.signs.get(loc);
		}
		return null;
	}

	public void removeAllSigns() {
		for (WorldSign sign : this.signs.values()) {
			sign.remove();
		}
	}

	public void saveSigns() {
		int index = 0;
		for (WorldSign sign : this.signs.values()) {
			FileConfiguration cfg = ConfigManager.getSigns().getConfig();

			ConfigurationSection s = cfg.createSection(index++ + "");
			ConfigurationSection ls = s.createSection("locatie");
			Location loc = sign.getSign().getLocation();
			ls.set("x", Integer.valueOf((int) loc.getX()));
			ls.set("y", Integer.valueOf((int) loc.getY()));
			ls.set("z", Integer.valueOf((int) loc.getZ()));
			ls.set("wereld", loc.getWorld().getName());
			s.set("naarToeStuurWereld", sign.getWorldToSendTo().getName());
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
				System.err.println(sl.getString("wereld") + " is een niet bestaande werled!");
			} else if (worldToSendTo == null) {
				System.err.println(sl.getString("naarToeStuurWereld") + " is een niet bestaande wereld!");
			} else {
				Location loc = new Location(world, sl.getDouble("x"), sl.getDouble("y"), sl.getDouble("z"));
				Block b = loc.getBlock();
				if ((b == null) || ((b.getType() != Material.SIGN) && (b.getType() != Material.WALL_SIGN)
						&& (b.getType() != Material.SIGN_POST))) {
					System.err.println("Een  worldsign is verwijderd, vanwege een missende sign, op locatie:");
					System.err.println("x:" + sl.getInt("x"));
					System.err.println("y:" + sl.getInt("y"));
					System.err.println("z:" + sl.getInt("z"));
					System.err.println("wereld:" + sl.getString("wereld"));
				} else {
					Sign sign = (Sign) b.getState();

					WorldSign wsign = new WorldSign(sign, worldToSendTo);
					wsign.runTaskTimer(Main.getPlugin(Main.class), 0L, 40L);
					this.signs.put(loc, wsign);

					cfg.set(id, null);
				}
			}
		}
		ConfigManager.getSigns().save();
	}

	public Map<Location, WorldSign> getSigns() {
		return this.signs;
	}
}
