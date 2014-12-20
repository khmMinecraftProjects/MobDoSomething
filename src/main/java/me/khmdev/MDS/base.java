package me.khmdev.MDS;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import me.khmdev.APIBase.API;
import me.khmdev.APIBase.Almacenes.Almacen;
import me.khmdev.APIBase.Almacenes.Central;
import me.khmdev.APIBase.Almacenes.Datos;
import me.khmdev.APIBase.Almacenes.local.ConfigFile;
import me.khmdev.HUB.Base;
import me.khmdev.HUB.Tutorial.Tutorial;

public class base implements Datos {
	private static init instance;
	private Central central;

	public base(init plug) {
		central = new Central(plug);
		central.insertar(this);
		instance = plug;
		ConfigFile conf = new ConfigFile(plug.getDataFolder(), "mobs");
		FileConfiguration section = conf.getConfig();
		load(section);
		Bukkit.getPluginManager().registerEvents(new ListenMob(plug), plug);
	}

	public static Plugin getInstance() {
		return instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("mds")) {
			if (args.length < 1) {
				sender.sendMessage("/mds <id>");
				return true;
			}
			if (sender instanceof Player) {
				MobDo m = mobs.get(args[0]);

				if (m == null) {
					return true;
				}
				m.spawn(((Player) sender).getLocation());
			}
			return true;
		}
		return false;
	}

	private static HashMap<String, MobDo> mobs = new HashMap<>();

	public static MobDo getMob(String s) {
		return mobs.get(s);
	}

	public void load(FileConfiguration cfg) {
		for (String s : cfg.getKeys(false)) {
			if (cfg.isConfigurationSection(s)) {
				addMob(cfg.getConfigurationSection(s));
			}
		}
	}

	private void addMob(ConfigurationSection section) {
		String id = section.getName(), name = section.isString("name") ? section
				.getString("name") : "", tutorial = section
				.isString("tutorial") ? section.getString("tutorial") : "", type = section
				.isString("type") ? section.getString("type") : "";
		boolean freeze = section.isBoolean("freeze") ? section
				.getBoolean("freeze") : true, inmortal = section
				.isBoolean("inmortal") ? section.getBoolean("inmortal") : true;
		Tutorial t = Base.getTutorial(tutorial);
		if (type == null || type == "") {
			return;
		}
		EntityType c = EntityType.valueOf(type);
		if (c == null || t == null) {
			return;
		}
		MobDo m = new MobDo(id, name, c, t, freeze, inmortal);
		mobs.put(id, m);
	}


	@Override
	public void cargar(Almacen nbt) {
		HashMap<UUID, String> map = new HashMap<>();
		Almacen alm = nbt.getAlmacen("Freeze");
		for (String s : alm.getKeys()) {
			UUID id = UUID.fromString(s);
			map.put(id, alm.getString(s));
		}

		for (World w : Bukkit.getServer().getWorlds()) {
			for (LivingEntity ent : w.getLivingEntities()) {
				if (map.containsKey(ent.getUniqueId())) {
					API.setMetadata(ent, "IdMDS", map.get(ent.getUniqueId()));
					ListenMob.add(ent);
					System.out.println(ent + " " + map.get(ent.getUniqueId()));
				}
			}
		}
		nbt.setAlmacen("Freeze", alm);
	}

	@Override
	public void guardar(Almacen nbt) {
		Almacen alm = nbt.getAlmacen("Freeze");
		alm.clear();
		for (World w : Bukkit.getServer().getWorlds()) {
			for (LivingEntity ent : w.getLivingEntities()) {
				MetadataValue data = API.getMetadata(ent, "IdMDS");
				if (data != null) {
					alm.setString(ent.getUniqueId().toString(), data.asString());
				}
			}

		}
		nbt.setAlmacen("Freeze", alm);
	}

	public void onDisable() {
		central.guardar();
	}

}
