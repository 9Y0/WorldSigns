package me.VetBakSim.worldsigns;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {

	private static ConfigManager signs = new ConfigManager("signs");

	public static ConfigManager getSigns() {
		return signs;
	}

	private File file;
	private FileConfiguration config;

	private ConfigManager(String fileName) {
		if (!Main.getInstance().getDataFolder().exists())
			Main.getInstance().getDataFolder().mkdir();

		this.file = new File(Main.getInstance().getDataFolder(), fileName + ".yml");
		if (!this.file.exists()) {
			try {
				this.file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.config = YamlConfiguration.loadConfiguration(this.file);
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void save() {
		try {
			this.config.save(this.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
