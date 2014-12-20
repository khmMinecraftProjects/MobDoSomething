package me.khmdev.MDS;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.khmdev.APIBase.API;
import me.khmdev.HUB.Base;
import me.khmdev.HUB.Tutorial.Tutorial;
import me.khmdev.HUB.Tutorial.TutorialAction;

public class MobDo {
	private String id, name;
	private Tutorial tutorial;
	private EntityType creature;
	private boolean freeze, inmortal;

	public MobDo(String i, String n, EntityType c, Tutorial t, boolean f,
			boolean in) {
		id = i;
		name = n;
		creature = c;
		tutorial = t;
		freeze = f;
		inmortal = in;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Tutorial getTutorial() {
		return tutorial;
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

	public void initTutorial(Player player) {
		if(tutorial.esTuto(player.getName())){
			player.sendMessage(
					ChatColor.translateAlternateColorCodes('&',
							"&CYa has hecho el tutorial"));
		}else{
			Base.run(new TutorialAction(tutorial,player));
		}
	}
}
