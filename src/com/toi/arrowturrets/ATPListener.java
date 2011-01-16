package com.toi.arrowturrets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Arrow;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;


public class ATPListener extends PlayerListener{

	Plugin plugin;
	private ArrayList<Turret> turrets = new ArrayList<Turret>();
	private float speed = 1.0F;
    private float spread = 7.0F;
	private int xDis = 5;
	private int yDis = 5;
	private int zDis = 5;
	private int numberOfArrows = 2;
	private int atItemId = 262;
	private File atdir = new File("ArrowTurrets");
	private String turretsFilePath = atdir.getPath() + File.separator + "ArrowTurrets.txt";
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private boolean execIsActivated = false;
	private long delay = 500;
	private boolean useHash = true; 
	private Hashtable<Vector, ArrayList<Integer>> hashturrets = new Hashtable<Vector, ArrayList<Integer>>();
	private tProperties properties = new tProperties("ArrowTurrets" + File.separator + "ArrowTurrets.properties");
	private Hashtable<Vector, ArrayList<Integer>> turretSeats = new Hashtable<Vector, ArrayList<Integer>>();
	private tPermissions perms = new tPermissions("ArrowTurrets" + File.separator + "ArrowTurrets.perms");
	
	public ATPListener(ArrowTurrets arrowTurrets) {
		this.plugin = arrowTurrets;
	}
	
	public void activateSchedule()
	{
		if (!execIsActivated)
		{
			executor.schedule(new Runnable(){
				@Override
				public void run() {
					for (Turret turret : turrets)
					{
						turret.setCanShoot(true);
					}
					execIsActivated = false;
				}
			}, delay, TimeUnit.MILLISECONDS);
			this.execIsActivated = true;
		}
	}
	
	public void onPlayerItem(PlayerItemEvent event)
	{
		Player player = event.getPlayer();
		Location pl = new Location(player.getWorld(),
				Math.floor(player.getLocation().getX() + 0.5),
				Math.floor(player.getLocation().getY() + 0.5),
				Math.floor(player.getLocation().getZ() + 0.3));
		
		if (this.turretSeats.containsKey(pl.toVector()))
		{
			ArrayList<Integer> tir = this.turretSeats.get(pl.toVector());
			TargetBlock ab = new TargetBlock(player, 300, 0.3);
			Block blk = ab.getTargetBlock();
			if (blk != null)
			{
				Location targetLoc = new Location(player.getWorld(), blk.getX() + 0.5, blk.getY() + 0.5, blk.getZ() + 0.5);
				
				for (Integer t : tir)
				{
					Vector shooter = this.turrets.get(t).getLoc().toVector();
					shooter = new Vector(shooter.getX() + 0.5, shooter.getY() + 0.5, shooter.getZ() + 0.5);
					shoot(player, this.speed, this.spread, targetLoc, shooter);
				}
			}
		}
	}
	
	public void onPlayerCommand(PlayerChatEvent event)
	{
		Player player = event.getPlayer();
		String split[] = event.getMessage().split(" ");
		
		if(split[0].equalsIgnoreCase("/delt") && perms.canPlayerUseCommand(player.getName(), "/delt"))
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
			event.setCancelled(true);
		}
		else if (split[0].equalsIgnoreCase("/addt") && perms.canPlayerUseCommand(player.getName(), "/addt"))
		{
			TargetBlock ab = new TargetBlock(player, 300, 0.3);
			Block blk = ab.getTargetBlock();
			if (blk != null)
			{
				Location tl = new Location(player.getWorld(), blk.getX(), blk.getY(), blk.getZ());
				if (!this.turretExists(tl))
				{
					String name = "";
					if (split.length >= 2)
						name = split[1];
					if(useHash)
					{
						ArrayList<String> owners = new ArrayList<String>();
						owners.add(player.getName());
						ArrayList<String> accessors = new ArrayList<String>();
						this.addTurret(name, tl, null, owners, accessors);
					}
					else
						this.turrets.add(new Turret(player.getName(), tl));
					player.sendMessage(atString() + "Added a turret!");
				}
				else
					player.sendMessage(atString() + "There is already a turret here");
			}
			else
				player.sendMessage("Target Block is null :/");
			event.setCancelled(true);
		}
		else if (split[0].equalsIgnoreCase("/addts") && perms.canPlayerUseCommand(player.getName(), "/addts"))
		{
			if (split.length >= 2)
			{
				TargetBlock ab = new TargetBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					Location tl = new Location(player.getWorld(), blk.getX(), blk.getY(), blk.getZ());
					player.sendMessage(atString() + this.addTurretSeat(player.getName(), split[1], tl));
				}
			}
			else
				player.sendMessage(atString() + "You need to define a turret name!");
			event.setCancelled(true);
		}
		else if (split[0].equalsIgnoreCase("/delts") && perms.canPlayerUseCommand(player.getName(), "/delts"))
		{
			if (split.length >= 2)
			{
				player.sendMessage(atString() + this.delTurretSeat(player.getName(), split[1]));
			}
			else
				player.sendMessage(atString() + "You need to define a turret name!");
			event.setCancelled(true);
		}
		else if (split[0].equalsIgnoreCase("/settn") && perms.canPlayerUseCommand(player.getName(), "/settn"))
		{
			if (split.length >= 2)
			{
				TargetBlock ab = new TargetBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					Location tl = new Location(player.getWorld(), blk.getX(), blk.getY(), blk.getZ());
					player.sendMessage(atString() + this.setTurretName(player.getName(), split[1], tl));
				}
			}
			else
				player.sendMessage(atString() + "You need to define a turret name!");
			event.setCancelled(true);
		}
		else if (split[0].equalsIgnoreCase("/addta") && perms.canPlayerUseCommand(player.getName(), "/addta"))
		{
			if (split.length > 1)
			{
				TargetBlock ab = new TargetBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					player.sendMessage(atString() + this.addAccess(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), split[1]));
				}
			}
			else
				player.sendMessage(atString() + "You need to define a player");
			event.setCancelled(true);
		}
		else if (split[0].equalsIgnoreCase("/delta") && perms.canPlayerUseCommand(player.getName(), "/delta"))
		{
			if (split.length > 1)
			{
				TargetBlock ab = new TargetBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					player.sendMessage(atString() + this.removeAccess(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), split[1]));
				}
			}
			else
				player.sendMessage(atString() + "You need to define a player");
			event.setCancelled(true);
		}
		else if (split[0].equalsIgnoreCase("/addto") && perms.canPlayerUseCommand(player.getName(), "/addto"))
		{
			if (split.length > 1)
			{
				TargetBlock ab = new TargetBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					player.sendMessage(atString() + this.addOwner(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), split[1]));
				}
			}
			else
				player.sendMessage(atString() + "You need to define a player");
			event.setCancelled(true);
		}
		else if (split[0].equalsIgnoreCase("/delto") && perms.canPlayerUseCommand(player.getName(), "/delto"))
		{
			if (split.length > 1)
			{
				TargetBlock ab = new TargetBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					player.sendMessage(atString() + this.delOwner(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ()), player.getName(), split[1]));
				}
			}
			else
				player.sendMessage(atString() + "You need to define a player");
			event.setCancelled(true);
		}
	}

	private boolean turretExists(Location loc)
	{
		for (Turret turret : this.turrets)
		{
			if (turret.getLoc().equals(loc))
				return true;
		}
		return false;
	}
	
	private void addTurret(String name, Location loc, Location seatLoc, ArrayList<String> owners, ArrayList<String> accessors)
	{
		Turret trtToAdd = new Turret(owners, accessors, loc); 
		if (!name.equals(""))
			trtToAdd.setName(name);
		if (seatLoc != null)
		{
			trtToAdd.setSeatLoc(seatLoc);
			trtToAdd.setUsingSeat(true);
		}
		this.turrets.add(trtToAdd);
        int turret_id = this.turrets.size() - 1; // Index in list.
	    // Add the current turret id to our collection
        ArrayList<Integer> turret_current_id_list = new ArrayList<Integer>();
        turret_current_id_list.add(turret_id);
		
		int cx = loc.getBlockX() - this.xDis;
		int cy = loc.getBlockY() - this.yDis;
		int cz = loc.getBlockZ() - this.zDis;
		for (int x = 0; x < (this.xDis * 2) + 1; x++)
		{
			for (int y = 0; y < (this.yDis * 2) + 1; y++)
			{
				for (int z = 0; z < (this.zDis * 2) + 1; z++)
				{
					Vector cBlock = new Vector(cx + x, cy + y, cz + z);
					ArrayList<Integer> turrets_covering_location = new ArrayList<Integer>();
                    // get scan data from scan data collection.
                    turrets_covering_location = this.hashturrets.get(cBlock);

                    if(turrets_covering_location == null)
                    {
                        // No other turrets cover this location. Add the list as-is
                        // the only the current turret
                    	hashturrets.put(cBlock, turret_current_id_list);
                    }
                    else
                    {
                        // Other turrets are in range.. add the current turret to it.
                        turrets_covering_location.add(turret_id);
                    	//hashturrets.remove(cBlock);
                    	hashturrets.put(cBlock, turrets_covering_location);
                    }
                    cBlock = null;
				}
			}
		}
		this.saveTurrets();
	}
	
	private String addAccess(Location loc, String playerName, String playerToAdd)
	{
		String line = "There is no turret here";
		for (Turret turret : this.turrets)
		{
			if (turret.getLoc().equals(loc))
			{
				if (turret.getOwners().contains(playerName))
				{
					if (!turret.getAccessors().contains(playerToAdd))
					{
						turret.getAccessors().add(playerToAdd);
						line = "Added access for player: " + playerToAdd;
						this.saveTurrets();
					}
					else
						line = "Player already have access to this turret";
					break;
				}
				else
				{
					line = "You are not an owner of this turret";
				}
				break;
			}
		}
		return line;
	}
	
	private String removeAccess(Location loc, String playerName, String playerToDel)
	{
		String line = "There is no turret here";
		for (Turret turret : this.turrets)
		{
			if (turret.getLoc().equals(loc))
			{
				if (turret.getOwners().contains(playerName))
				{
					if (turret.getAccessors().contains(playerToDel))
					{
						turret.getAccessors().add(playerToDel);
						line = "Removed access for player: " + playerToDel;
						this.saveTurrets();
					}
					else
						line = "Player doesn't have access to this turret";
					break;
				}
				else
				{
					line = "You are not an owner of this turret";
				}
				break;
			}
		}
		return line;
	}
	
	private String addOwner(Location loc, String playerName, String playerToAdd)
	{
		String line = "There is no turret here";
		for (Turret turret : this.turrets)
		{
			if (turret.getLoc().equals(loc))
			{
				if (turret.getOwners().contains(playerName))
				{
					if (!turret.getOwners().contains(playerToAdd))
					{
						turret.getOwners().add(playerToAdd);
						line = "Added owner: " + playerToAdd;
						this.saveTurrets();
					}
					else
						line = "Player already is an owner";
					break;
				}
				else
				{
					line = "You are not an owner of this turret";
				}
				break;
			}
		}
		return line;
	}
	
	private String delOwner (Location loc, String playerName, String playerToDel)
	{
		String line = "There is no turret here";
		for (Turret turret : this.turrets)
		{
			if (turret.getLoc().equals(loc))
			{
				if (turret.getOwners().contains(playerName))
				{
					if (turret.getOwners().contains(playerToDel))
					{
						turret.getOwners().add(playerToDel);
						line = "Removed owner: " + playerToDel;
						this.saveTurrets();
					}
					else
						line = "Player isn't an owner";
					break;
				}
				else
				{
					line = "You are not an owner of this turret";
				}
				break;
			}
		}
		return line;
	}
	
	private String addTurretSeat (String playerName, String turretName, Location loc)
	{
		for (int i = 0; i < this.turrets.size(); i++)
		{
			if (turrets.get(i).getName() != null)
			{
				if (turrets.get(i).getName().equalsIgnoreCase(turretName))
				{
					if (turrets.get(i).getOwners().contains(playerName))
					{
						turrets.get(i).setSeatLoc(loc.clone());
						turrets.get(i).setUsingSeat(true);
						if (useHash)
						{
							ArrayList<Integer> seatIds = new ArrayList<Integer>();
							seatIds.add(i);
							if (this.turretSeats.containsKey(loc.toVector()))
							{
								seatIds.addAll(this.turretSeats.get(loc.toVector()));
								this.turretSeats.put(loc.toVector(), seatIds);
							}
							else
							{
								this.turretSeats.put(loc.toVector(), seatIds);
								System.out.println("Put down a seat at " + loc.toString());
							}
						}
						this.saveTurrets();
						return "You set the turret seat for the " + turretName + " turret";
					}
					else
					{
						return "You are not an owner of this turret!";
					}
				}
			}
		}
		return "There is no turret by that name!";
	}
	
	private String delTurretSeat (String playerName, String turretName)
	{
		for (Turret turret : this.turrets)
		{
			if (turret.getName().equalsIgnoreCase(turretName))
			{
				if (turret.getOwners().contains(playerName))
				{
					turret.setUsingSeat(false);
					this.saveTurrets();
					return "You deleted the seat for the turret " + turretName;
				}
					
				break;
			}
		}
		return "There is no turret by the name " + turretName;
	}
	
	private String setTurretName(String playerName, String turretName, Location loc) 
	{
		int index = -1;
		boolean exists = false;
		boolean isPlayerOwner = false;
		for (int i = 0; i < this.turrets.size(); i++)
		{
			if (turrets.get(i).getLoc().equals(loc))
			{
				if (turrets.get(i).getOwners().contains(playerName))
				{
					index = i;
					isPlayerOwner = true;
				}
				else
					index = i;
			}
			if (turrets.get(i).getName() != null)
			{
				if (turrets.get(i).getName().equalsIgnoreCase(turretName))
				{
					exists = true;
				}
			}
		}
		if (index != -1 && !exists)
		{
			if (isPlayerOwner)
			{
				this.turrets.get(index).setName(turretName);
				this.saveTurrets();
				return "The turret is now called " + turretName;
			}
			else
				return "You are not an owner of this turret!";
		}
		else if (index == -1)
			return "There is no turret here!";
		else if (exists)
			return "There is already a turret by the name " + turretName + "!";
		else
			return "There is no turret here!";
	}
	
	private String atString()
	{
		return ChatColor.AQUA + "[ArrowTurrets] " + ChatColor.YELLOW; 
	}
	
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		
		if (useHash)
		{
			Vector playerHead = new Vector(player.getLocation().getX() + 0.5, player.getLocation().getY() + 1.5, player.getLocation().getZ() + 0.5);
			Vector playerHeadFloored = new Vector(Math.floor(playerHead.getX()), Math.floor(playerHead.getY()), Math.floor(playerHead.getZ()));
			if (this.hashturrets.containsKey(playerHeadFloored))
			{
				ArrayList<Integer> tsc = this.hashturrets.get(playerHeadFloored);
				this.activateSchedule();
				// For each possible turret to shoot in that location check access and if they should shoot, make them.
				for(int i : tsc)
				{
				    Turret turret = this.turrets.get(i);
			    	if (turret.canShoot())
			    	{
			    		if (!turret.getOwners().contains(player.getName()) && !turret.getAccessors().contains(player.getName()))
						{
							this.shoot(player, speed, spread, playerHead.toLocation(player.getWorld()), turret.getLoc().toVector());
							turret.setCanShoot(false);
						}
		    		}
				}
			}
		}
		else
		{
			for (Turret turret : this.turrets)
			{
				if (isInTurretArea(player, turret.getLoc().toVector()))
				{
					this.activateSchedule();
					if (!turret.getOwners().contains(player.getName()) && !turret.getAccessors().contains(player.getName()))
					{
						if (turret.canShoot())
						{
							this.shoot(player, speed, spread, player.getLocation(), turret.getLoc().toVector());
							turret.setCanShoot(false);
						}
					}
				}
			}
		}
	}
	
	protected void shoot(Player player, float speed, float spread, Location target, Vector shooter)
	{
		for (int i = 0; i < this.numberOfArrows; i++)
		{
	        Location drag = new Location(target.getWorld(), target.getX() - shooter.getX(), target.getY() - shooter.getY(), target.getZ() - shooter.getZ());
			Location startPos = new Location(target.getWorld(), target.getX() - drag.getX() + 0.5, target.getY() - drag.getY() + 0.5, target.getZ() - drag.getZ() + 0.5);
			target.getWorld().spawnArrow(startPos, drag.toVector(), speed, spread);
	        /*en arrow = new en(etc.getMCServer().e);
	        arrow.c(startPos.x + 0.5, startPos.y + 0.5,
	        		startPos.z + 0.5, 0, 0);
	        etc.getMCServer().e.a(arrow);
	        arrow.a(drag.x, drag.y, drag.z,
	        		speed, spread);*/
		}
    }
	
	public boolean removeShooter(Location loc)
	{
		for (int i = 0; i < this.turrets.size(); i++)
		{
			if (this.turrets.get(i).getLoc().equals(loc))
			{
				if (useHash)
					this.removeHashTurret(this.turrets.get(i));
				this.turrets.remove(i);
				this.saveTurrets();
				return true;
			}
		}
		return false;
	}
	
	public void removeHashTurret(Turret turret)
	{
		/*int cx = turret.getLoc().getBlockX() - this.xDis;
		int cy = turret.getLoc().getBlockY() - this.yDis;
		int cz = turret.getLoc().getBlockZ() - this.zDis;
		int nrOfBlocksRemoved = 0;
		for (int x = 0; x < (this.xDis * 2) + 1; x++)
		{
			for (int y = 0; y < (this.yDis * 2) + 1; y++)
			{
				for (int z = 0; z < (this.zDis * 2) + 1; z++)
				{
					Vector cBlock = new Vector(cx + x, cy + y, cz + z);
					ArrayList<Turret> turrets = this.hashturrets.get(cBlock);
					if (turrets != null)
					{
						for (int i = 0; i < turrets.size(); i++)
						{
							if (turrets.get(i).equals(turret))
							{
								turrets.remove(i);
								break;
							}
						}
						if (turrets.isEmpty())
						{
							this.hashturrets.remove(cBlock);
							nrOfBlocksRemoved++;
						}
					}
				}
			}
		}*/
	}
	
	public boolean isInTurretArea (Player player, Vector turretLoc)
	{
		if (player.getLocation().getX() + 0.5 < turretLoc.getX() + this.xDis - 1 && player.getLocation().getX() + 0.5 > turretLoc.getX() - this.xDis - 1 &&
			player.getLocation().getZ() + 0.5 < turretLoc.getZ() + this.zDis && player.getLocation().getZ() + 0.5 > turretLoc.getZ() - this.zDis &&
			player.getLocation().getY() + 1.5 < turretLoc.getY() + this.yDis && player.getLocation().getY() + 1.5 > turretLoc.getY() - this.yDis)
		{
			return true;
		}
		return false;
	}
	
	public void loadConfig()
	{
		try{
			this.properties.load();
			System.out.println("[ArrowTurrets] " + "Config loaded!");
		}
		catch (IOException ioe){
			ioe.printStackTrace();
			System.out.println("[ArrowTurrets] " + "Failed to load config!");
		}
		
		this.atItemId = properties.getInt("item-id", 288);
		this.numberOfArrows = properties.getInt("number-of-arrows", 1);
		this.speed = properties.getFloat("speed", 1.0F);
		this.spread = properties.getFloat("spread", 7.0F);
		this.delay = properties.getLong("delay", 500L);
		this.xDis = properties.getInt("x-distance", 5);
		this.yDis = properties.getInt("y-distance", 5);
		this.zDis = properties.getInt("z-distance", 5);
		this.turretsFilePath = properties.getString("turrets-filepath", "ArrowTurrets" + File.separator + "ArrowTurrets.txt");
		this.useHash = properties.getBoolean("use-hash", true);
		properties.save();
	}
	
	public void saveTurrets()
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(this.turretsFilePath));
			if (!this.atdir.exists())
			{
				System.out.println("[ArrowTurrets] Could not find ArrowTurrets directory!");
				if (atdir.mkdir())
					System.out.println("[ArrowTurrets] ArrowTurrets directory created!");
				else
					System.out.println("[ArrowTurrets] Failed to create ArrowTurrets directory!");
			}
			
			try
			{
				boolean first = true;
				for (Turret turret : this.turrets)
				{
					// Create owners string
					String owners = "";
					boolean fo = true;
					for (String owner : turret.getOwners())
					{
						if (fo)
						{
							owners += owner;
							fo = false;
						}
						else
							owners += "," + owner;
					}
					// Create accessors string
					String accessors = "";
					boolean fa = true;
					for (String accessor : turret.getAccessors())
					{
						if (fa)
						{
							accessors += accessor;
							fa = false;
						}
						else
							accessors += "," + accessor;
					}
					if (!first)
					{
						bw.newLine();
					}
					if (turret.getSeatLoc() != null)
					{
						bw.write(turret.getName() + ";" + owners + ";" + accessors + ";" +
								turret.getLoc().getX() + "," + turret.getLoc().getY() + "," + turret.getLoc().getZ() + ";" +
								turret.getSeatLoc().getX() + "," + turret.getSeatLoc().getY() + "," + turret.getSeatLoc().getZ());
					}
					else
					{
						bw.write(turret.getName() + ";" + owners + ";" + accessors + ";" +
								turret.getLoc().getX() + "," + turret.getLoc().getY() + "," + turret.getLoc().getZ() + ";" +
								",,");
					}
					first = false;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				bw.flush();
				bw.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadTurrets()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(this.turretsFilePath));
			try
			{
				String line = br.readLine();
				if (line != null)
				{
					while (line != null)
					{
						String[] splitted = line.split(";");
						if (splitted.length >= 5)
						{
							String name = splitted[0];
							String[] owners = splitted[1].split(",");
							String[] accessors = splitted[2].split(",");
							String[] loc = splitted[3].split(",");
							String[] seatLoc = splitted[4].split(",");
							if (loc.length == 3)
							{
								if (useHash)
								{
									if (seatLoc.length == 3)
									{
										this.addTurret(
												name,
												new Location(this.plugin.getServer().getWorlds()[0], Float.valueOf(loc[0]), Float.valueOf(loc[1]), Float.valueOf(loc[2])),
												new Location(this.plugin.getServer().getWorlds()[0], Float.valueOf(seatLoc[0]), Float.valueOf(seatLoc[1]), Float.valueOf(seatLoc[2])),
												new ArrayList<String>(Arrays.asList(owners)),
												new ArrayList<String>(Arrays.asList(accessors)));
									}
									else
										this.addTurret(
												name,
												new Location(this.plugin.getServer().getWorlds()[0], Float.valueOf(loc[0]), Float.valueOf(loc[1]), Float.valueOf(loc[2])),
												null,
												new ArrayList<String>(Arrays.asList(owners)),
												new ArrayList<String>(Arrays.asList(accessors)));
								}
								else
								{
									Turret trt = new Turret(new ArrayList<String>(Arrays.asList(owners)),
											new ArrayList<String>(Arrays.asList(accessors)),
											new Location(this.plugin.getServer().getWorlds()[0], Float.valueOf(loc[0]), Float.valueOf(loc[1]), Float.valueOf(loc[2])));
									if (!name.equals(""))
										trt.setName(name);
									if (seatLoc.length == 3)
									{
										trt.setSeatLoc(new Location(this.plugin.getServer().getWorlds()[0], Float.valueOf(seatLoc[0]), Float.valueOf(seatLoc[1]), Float.valueOf(seatLoc[2])));
										trt.setUsingSeat(true);
									}
									this.turrets.add(trt);
								}
								line = br.readLine();
							}
						}
					}
				}
			}
			catch (IOException e)
			{
				
			}
			finally
			{
				br.close();
			}
		}
		catch (IOException e)
		{
			
		}
	}

	public tPermissions getPerms() {
		return perms;
	}
}
