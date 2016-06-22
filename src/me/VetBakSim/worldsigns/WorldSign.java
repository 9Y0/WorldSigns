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

import org.bukkit.Material;
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
		cancel();
		this.sign.setType(Material.AIR);
	}

	public World getWorldToSendTo() {
		return this.worldToSendTo;
	}

	public Sign getSign() {
		return this.sign;
	}

	public void run() {
		this.sign.setLine(0, "§6§l[WorldSign]");
		this.sign.setLine(1, "§8World: §7" + this.worldToSendTo.getName());
		this.sign.setLine(3, "§3Players: §b" + this.worldToSendTo.getPlayers().size());

		this.sign.update();
	}
}
