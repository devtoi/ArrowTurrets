package com.toi.arrowturrets;

import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public class ArrowTurrets extends JavaPlugin
{
	private String name;
	private String version;
	private final ATPListener playerListener = new ATPListener(this);
	private static final Logger logger = Logger.getLogger("Minecraft");
	Plugin permPlugin = null;
	Boolean isGm = false;

	public ArrowTurrets()
	{

		
	}

	public void onEnable()
	{
		name = this.getDescription().getName();
		version = this.getDescription().getVersion();
		permPlugin = this.getServer().getPluginManager().getPlugin("GroupManager");
		if (permPlugin != null)
		{
			if (!this.getServer().getPluginManager().isPluginEnabled(permPlugin))
			{
				this.getServer().getPluginManager().enablePlugin(permPlugin);
			}
			logger.log(Level.INFO, "[ArrowTurrets] Found GroupManager. Using it for permissions");
			isGm = true;
		}
		else
		{
			permPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

			if (permPlugin != null)
			{
				if (!this.getServer().getPluginManager().isPluginEnabled(permPlugin))
				{
					this.getServer().getPluginManager().enablePlugin(permPlugin);
				}
				logger.log(Level.INFO, "[ArrowTurrets] Found Permissions. Using it for permissions");
			}
			else
			{
				logger.log(Level.INFO, "[ArrowTurrets] Permissions plugins not found, defaulting to OPS.txt");
			}
		}
		playerListener.loadConfig();
		playerListener.loadTurrets();
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);
		System.out.println(name + " " + version + " initialized!");
	}

	public void onDisable()
	{
	}

	@Override
	public void onLoad()
	{

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{

		if (sender instanceof Player)
		{
			Player player = (Player)sender;


			if (commandLabel.equalsIgnoreCase("delt") && hasPermission(player, "arrowturrets.delt"))
			{
				TargetBlock ab = new TargetBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					/*if (this.removeShooter(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ())))
					{
					player.sendMessage(atString() + "Removed a turret!");
					this.saveTurrets();
					}
					else
					player.sendMessage(atString() + "There is no turret here");*/
				}
				return true;
			}
			else if (commandLabel.equalsIgnoreCase("addt") && hasPermission(player, "arrowturrets.addt"))
			{
				TargetBlock ab = new TargetBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					Location tl = new Location(player.getWorld(), blk.getX(), blk.getY(), blk.getZ());
					if (!playerListener.turretExists(tl))
					{
						String tname = "";
						if (args.length != 0)
							tname = args[0];
						if (playerListener.useHash)
						{
							ArrayList<String> owners = new ArrayList<String>();
							owners.add(player.getName());
							ArrayList<String> accessors = new ArrayList<String>();
							playerListener.addTurret(tname, tl, null, owners, accessors);
						}
						else
							playerListener.turrets.add(new Turret(player.getName(), tl));
						player.sendMessage(playerListener.atString() + "Added a turret!");
						return true;
					}
					else
						player.sendMessage(playerListener.atString() + "There is already a turret here");
					return true;
				}
				else
					player.sendMessage("Target Block is null :/");
				return true;
			}
			else if (commandLabel.equalsIgnoreCase("addts") && hasPermission(player, "arrowturrets.addts"))
			{
				if (args.length != 0)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						Location tl = new Location(player.getWorld(), blk.getX(), blk.getY(), blk.getZ());
						player.sendMessage(playerListener.atString() + playerListener.addTurretSeat(player.getName(), args[0], tl));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a turret name!");
				return true;
			}
			else if (commandLabel.equalsIgnoreCase("delts") && hasPermission(player, "arrowturrets.delts"))
			{
				if (args.length != 0)
				{
					player.sendMessage(playerListener.atString() + playerListener.delTurretSeat(player.getName(), args[0]));
					return true;
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a turret name!");
				return true;
			}
			else if (commandLabel.equalsIgnoreCase("settn") && hasPermission(player, "arrowturrets.settn"))
			{
				if (args.length !=0)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						Location tl = new Location(player.getWorld(), blk.getX(), blk.getY(), blk.getZ());
						player.sendMessage(playerListener.atString() + playerListener.setTurretName(player.getName(), args[0], tl));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a turret name!");
				return true;
			}
			else if (commandLabel.equalsIgnoreCase("addta") && hasPermission(player, "arrowturrets.addta"))
			{
				if (args.length != 0)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						player.sendMessage(playerListener.atString() + playerListener.addAccess(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), args[0]));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a player");
				return true;
			}
			else if (commandLabel.equalsIgnoreCase("delta") && hasPermission(player, "arrowturrets.delta"))
			{
				if (args.length != 0)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						player.sendMessage(playerListener.atString() + playerListener.removeAccess(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), args[0]));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a player");
				return true;
			}
			else if (commandLabel.equalsIgnoreCase("addto") && hasPermission(player, "arrowturrets.addto"))
			{
				if (args.length != 0)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						player.sendMessage(playerListener.atString() + playerListener.addOwner(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), args[0]));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a player");
				return true;
			}
			else if (commandLabel.equalsIgnoreCase("delto") && hasPermission(player, "arrowturrets.delto"))
			{
				if (args.length != 0)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						player.sendMessage(playerListener.atString() + playerListener.delOwner(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), args[0]));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a player");
				return true;
			}
		}
		return false;
	}
	public Boolean hasPermission(Player base, String node)
	{
		if (permPlugin == null && base.isOp())
			return true;
		if (isGm)
		{
			GroupManager gm = (GroupManager)permPlugin;
			return gm.getWorldsHolder().getWorldPermissions(base).has(base, node);

		}
		else
		{
			Permissions pm = (Permissions)permPlugin;
			return pm.getHandler().has(base, node);
		}
	}
}
