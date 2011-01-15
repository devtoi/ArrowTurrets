import java.io.File;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;


public class ArrowTurrets extends JavaPlugin{

	private String name;
	private String version;
	private final ATPListener playerListener = new ATPListener(this);
	
	public ArrowTurrets(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File plugin, ClassLoader cLoader) {
	    super(pluginLoader, instance, desc, plugin, cLoader);
	
	    name = "Arrow turrets";
	    version = "v1.1.0 (Kleynach)";
	    
	    playerListener.loadConfig();
	    playerListener.loadTurrets();
	}
	
	public void onEnable()
	{
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);

		System.out.println(name + " " + version + " initialized!");
	}
	
	public void onDisable()
	{
		System.out.println(name + " " + version + " disabled.");
	}
}
