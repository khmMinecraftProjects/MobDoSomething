package me.khmdev.MDS;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import me.khmdev.APIBase.API;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ListenMob implements Listener, Runnable {
	private static List<UUID> freeze = new LinkedList<>();
	public ListenMob(JavaPlugin pl) {
		
		pl.getServer().getScheduler().runTaskTimer(pl, this, 0,20000000);

	}

	@EventHandler
	public void listenMob(PlayerInteractEntityEvent e) {
		MetadataValue data = API.getMetadata(e.getRightClicked(), "IdMDS");

		if (data != null) {
			MobDo m = base.getMob(data.asString());
			if (m != null) {
				m.initTutorial(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void listenMob(EntityDamageByEntityEvent e) {
		MetadataValue data = API.getMetadata(e.getEntity(), "IdMDS");
		if (data != null) {
			if(!(e instanceof Player && ((Player)e).hasPermission("mds.attack"))){
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void listenMob(EntityTargetEvent e) {
		MetadataValue data = API.getMetadata(e.getEntity(), "IdMDS");
		if (data != null) {
			e.setCancelled(true);
		}
	}

	public static void add(Entity e){
		freeze.add(e.getUniqueId());
		if(e instanceof LivingEntity){
			((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
					Integer.MAX_VALUE, 7));
			((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.JUMP,
					Integer.MAX_VALUE, -7));
		}
	}

	@Override
	public void run() {
		for (World world : Bukkit.getWorlds()) {
			for (LivingEntity ent : world.getLivingEntities()) {
				UUID id = ent.getUniqueId();
				if (freeze.contains(id)){
					((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
							Integer.MAX_VALUE, 7));
					((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.JUMP,
							Integer.MAX_VALUE, -7));
				}
			}
		}
	}

}
