package me.VetBakSim.worldsigns;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.VetBakSim.worldsigns.listeners.SignListeners;

public class Main extends JavaPlugin {

	private static Main instance;

	public static Main getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		SignManager.getInstance().loadSigns();

		Bukkit.getServer().getPluginManager().registerEvents(new SignListeners(), this);
	}

	@Override
	public void onDisable() {
		SignManager.getInstance().saveSigns();
	}

}
