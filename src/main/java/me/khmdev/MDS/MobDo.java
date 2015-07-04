package me.khmdev.MDS;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.khmdev.APIBase.API;

public class MobDo {
	private String id, name;
	private EntityType creature;
	private boolean freeze, inmortal;
	private List<String> actions = new LinkedList<>();
	private HashMap<String, Object> params;

	public MobDo(String i, String n, EntityType c, boolean f, boolean in,
			HashMap<String, Object> par, List<String> act) {
		id = i;
		name = n;
		creature = c;
		freeze = f;
		inmortal = in;
		params = par;
		actions = act;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public EntityType getCreature() {
		return creature;
	}

	public boolean isFreeze() {
		return freeze;
	}

	public boolean isInmortal() {
		return inmortal;
	}

	public void spawn(World w, int x, int y, int z) {
		Entity mob = w.spawnEntity(new Location(w, x, y, z), creature);

		if (mob instanceof LivingEntity) {
			LivingEntity live = (LivingEntity) mob;
			if (freeze) {
				live.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
						Integer.MAX_VALUE, Integer.MAX_VALUE), false);
			}
			live.setCustomName(name);
		}
		API.setMetadata(mob, "IdMDS", id);
		ListenMob.add(mob);
	}

	public void spawn(Location location) {
		Entity mob = location.getWorld().spawnEntity(location, creature);
		if (mob instanceof LivingEntity) {
			LivingEntity live = (LivingEntity) mob;

			live.setCustomName(name);
		}
		API.setMetadata(mob, "IdMDS", id);
		ListenMob.add(mob);

	}

	public void execute(Player player) {
		for (String as : actions) {
			Action a = base.getAction(as);
			if (a != null) {
				a.execute(this, player);
			}
		}
	}

	public void addAction(String a) {
		actions.add(a);
	}

	public void removeAction(String a) {
		actions.remove(a);
	}

	public List<String> getActions() {
		return actions;
	}

	public HashMap<String, Object> getParams() {
		return params;
	}
}
