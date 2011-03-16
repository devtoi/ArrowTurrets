package com.toi.arrowturrets;
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
	
	public ArrowTurrets()
	{
	
	    name = "Arrow turrets";
	    version = "v1.2.0 (Krathlak)";
	    
	    this.initCmds();
	    playerListener.loadConfig();
	    playerListener.getPerms().loadPermissions();
	    playerListener.getPerms().savePermissions();
	    playerListener.loadTurrets();
	}
	
	public void onEnable()
	{
	    getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
	    getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
	    getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);
		System.out.println(name + " " + version + " initialized!");
	}
	
	private void initCmds()
	{
		playerListener.getPerms().addCmd("/addt");
		playerListener.getPerms().addCmd("/delt");
		playerListener.getPerms().addCmd("/addts");
		playerListener.getPerms().addCmd("/delts");
		playerListener.getPerms().addCmd("/settn");
		playerListener.getPerms().addCmd("/addta");
		playerListener.getPerms().addCmd("/delta");
		playerListener.getPerms().addCmd("/addto");
		playerListener.getPerms().addCmd("/delto");
	}
	
	public void onDisable()
	{
		
	}

	public void onLoad()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
