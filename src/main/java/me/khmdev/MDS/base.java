package me.khmdev.MDS;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class base implements Datos {
	private static init instance;
	private Central central;
	private static HashMap<String, Action> actions = new HashMap<>();
	private static HashMap<String, MobDo> mobs = new HashMap<>();

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
					sender.sendMessage("No existe id " + args[0]);
					return true;
				}
				sender.sendMessage("Se ha spawneado el mob");
				m.spawn(((Player) sender).getLocation());
			}
			return true;
		}
		return false;
	}


	public static MobDo getMob(String s) {
		return mobs.get(s);
	}

	public void load(FileConfiguration cfg) {
		for (String s : cfg.getKeys(false)) {
			if (cfg.isConfigurationSection(s)) {
				addMobS(cfg.getConfigurationSection(s));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addMobS(ConfigurationSection section) {
		String id = section.getName(), name = "", type = "";
		List<String> act = new LinkedList<>();
		boolean freeze = true, inmortal = true;
		HashMap<String, Object> params = new HashMap<>();
		for (Entry<String, Object> data : section.getValues(false).entrySet()) {
			switch (data.getKey()) {
			case "name":
				if (data.getValue() instanceof String) {
					name = ChatColor.translateAlternateColorCodes('&',
							(String) data.getValue());
				}
				break;
			case "type":
				if (data.getValue() instanceof String) {
					type = (String) data.getValue();
				}
				break;
			case "freeze":
				if (data.getValue() instanceof Boolean) {
					freeze = (boolean) data.getValue();
				}
				break;
			case "inmortal":
				if (data.getValue() instanceof Boolean) {
					inmortal = (boolean) data.getValue();
				}
				break;
			case "action":
				if (data.getValue() instanceof List) {
					act = (List<String>) data.getValue();
				}else if(data.getValue() instanceof String){
					act.add((String) data.getValue());
				}
				break;
			default:
				params.put(data.getKey(), data.getValue());
				break;
			}
		}
		try {
			EntityType c = EntityType.valueOf(type);
			if (c == null) {
				return;
			}
			MobDo mob=new MobDo(id, name, c, freeze, inmortal, params,act);
			mobs.put(id, mob);
		} catch (Exception e) {
		}
		
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

	public static Action getAction(String as) {
		return actions.get(as);
	}

	public static Action addAction(String as, Action a) {
		return actions.put(as, a);
	}

	public static Action removeAction(String as) {
		return actions.remove(as);
	}
}
