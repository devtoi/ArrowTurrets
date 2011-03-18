package com.toi.arrowturrets;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;


public class ArrowTurrets extends JavaPlugin
{
	private String name;
	private String version;
	private final ATPListener playerListener = new ATPListener(this);

	public ArrowTurrets()
	{

		name = this.getDescription().getName();
		version = this.getDescription().getVersion();

		this.initCmds();
		playerListener.loadConfig();
		playerListener.getPerms().loadPermissions();
		playerListener.getPerms().savePermissions();
		playerListener.loadTurrets();
	}

	public void onEnable()
	{
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
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


			if (args[0].equalsIgnoreCase("/delt") && playerListener.perms.canPlayerUseCommand(player.getName(), "/delt"))
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
			else if (args[0].equalsIgnoreCase("/addt") && playerListener.perms.canPlayerUseCommand(player.getName(), "/addt"))
			{
				TargetBlock ab = new TargetBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					Location tl = new Location(player.getWorld(), blk.getX(), blk.getY(), blk.getZ());
					if (!playerListener.turretExists(tl))
					{
						String tname = "";
						if (args.length >= 2)
							tname = args[1];
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
			else if (args[0].equalsIgnoreCase("/addts") && playerListener.perms.canPlayerUseCommand(player.getName(), "/addts"))
			{
				if (args.length >= 2)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						Location tl = new Location(player.getWorld(), blk.getX(), blk.getY(), blk.getZ());
						player.sendMessage(playerListener.atString() + playerListener.addTurretSeat(player.getName(), args[1], tl));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a turret name!");
				return true;
			}
			else if (args[0].equalsIgnoreCase("/delts") && playerListener.perms.canPlayerUseCommand(player.getName(), "/delts"))
			{
				if (args.length >= 2)
				{
					player.sendMessage(playerListener.atString() + playerListener.delTurretSeat(player.getName(), args[1]));
					return true;
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a turret name!");
				return true;
			}
			else if (args[0].equalsIgnoreCase("/settn") && playerListener.perms.canPlayerUseCommand(player.getName(), "/settn"))
			{
				if (args.length >= 2)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						Location tl = new Location(player.getWorld(), blk.getX(), blk.getY(), blk.getZ());
						player.sendMessage(playerListener.atString() + playerListener.setTurretName(player.getName(), args[1], tl));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a turret name!");
				return true;
			}
			else if (args[0].equalsIgnoreCase("/addta") && playerListener.perms.canPlayerUseCommand(player.getName(), "/addta"))
			{
				if (args.length > 1)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						player.sendMessage(playerListener.atString() + playerListener.addAccess(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), args[1]));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a player");
				return true;
			}
			else if (args[0].equalsIgnoreCase("/delta") && playerListener.perms.canPlayerUseCommand(player.getName(), "/delta"))
			{
				if (args.length > 1)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						player.sendMessage(playerListener.atString() + playerListener.removeAccess(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), args[1]));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a player");
				return true;
			}
			else if (args[0].equalsIgnoreCase("/addto") && playerListener.perms.canPlayerUseCommand(player.getName(), "/addto"))
			{
				if (args.length > 1)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						player.sendMessage(playerListener.atString() + playerListener.addOwner(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), args[1]));
						return true;
					}
				}
				else
					player.sendMessage(playerListener.atString() + "You need to define a player");
				return true;
			}
			else if (args[0].equalsIgnoreCase("/delto") && playerListener.perms.canPlayerUseCommand(player.getName(), "/delto"))
			{
				if (args.length > 1)
				{
					TargetBlock ab = new TargetBlock(player, 300, 0.3);
					Block blk = ab.getTargetBlock();
					if (blk != null)
					{
						player.sendMessage(playerListener.atString() + playerListener.delOwner(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), args[1]));
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
}
