package me.VetBakSim.worldsigns;

import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldSign extends BukkitRunnable {

	private Sign sign;
	private World worldToSendTo;

	public WorldSign(Sign sign, World worldToSendTo) {
		this.sign = sign;
		this.worldToSendTo = worldToSendTo;
	}

	public void remove() {
		SignManager.getInstance().getSigns().remove(this.sign.getLocation());
		this.cancel();
		if (this.sign != null)
			this.sign.setType(Material.AIR);
	}

	public World getWorldToSendTo() {
		return worldToSendTo;
	}

	public Sign getSign() {
		return sign;
	}

	@Override
	public void run() {
		this.sign.setLine(0, "§6§l[WorldSign]");
		this.sign.setLine(1, "§8World: §7" + this.worldToSendTo.getName());
		this.sign.setLine(3, "§3Players: §b" + this.worldToSendTo.getPlayers().size());

		this.sign.update();
	}

}
