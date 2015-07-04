package me.khmdev.MDS;



import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class init extends JavaPlugin{
	public static base base;
	public void onEnable() {
		if (!hasPluging("APIAuxiliar")) {
			getLogger().severe(
					getName()
							+ " se desactivo debido ha que no encontro la API");
			setEnabled(false);
			return;
		}
		
		base=new base(this);
	}
	private static boolean hasPluging(String s) {
		try {
			return Bukkit.getPluginManager().getPlugin(s).isEnabled();
		} catch (Exception e) {

		}
		return false;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (base.onCommand(sender, cmd, label, args)) {
			return true;
		}
		return false;
	}
	@Override
	public void onDisable(){
		if(base!=null){base.onDisable();}
	}
}
