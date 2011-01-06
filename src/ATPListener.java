import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Arrow;
import org.bukkit.Block;
import org.bukkit.Location;
import org.bukkit.Player;
import org.bukkit.Vector;
import org.bukkit.event.player.PlayerChatEvent;
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
	private File settingsFile = new File(atdir.getPath() + File.separator + "at.settings");
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private boolean execIsActivated = false;
	private long delay = 500; 
	
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
	
	public void onPlayerCommand(PlayerChatEvent event)
	{
		Player player = event.getPlayer();
		String split[] = event.getMessage().split(" ");
		
		if(split[0].equalsIgnoreCase("/delt"))
		{
			if (player.getSelectedItem().getTypeID() == atItemId)
			{
				AimBlock ab = new AimBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					if (this.removeShooter(new Location(blk.getWorld(), blk.getX(), blk.getY(), blk.getZ())))
					{
						player.sendMessage("Removed a turret!");
						this.saveTurrets();
					}
				}
			}
		}
		else if (split[0].equalsIgnoreCase("/addt"))
		{
			if (player.getSelectedItem().getTypeID() == atItemId)
			{
				AimBlock ab = new AimBlock(player, 300, 0.3);
				Block blk = ab.getTargetBlock();
				if (blk != null)
				{
					turrets.add(new Turret(player.getName(), new Vector(blk.getX(), blk.getY(), blk.getZ())));
					player.sendMessage("Added a turret!");
					this.saveTurrets();
				}
			}
		}
	}

	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		for (Turret turret : this.turrets)
		{
			if (isInTurretArea(player, turret.getLoc()))
			{
				this.activateSchedule();
				if (!player.getName().equalsIgnoreCase(turret.getOwner()))
				{
					if (turret.canShoot())
					{
						this.shoot(player, speed, spread, player.getLocation(), turret.getLoc());
						turret.setCanShoot(false);
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
			Arrow arrow = target.getWorld().spawnArrow(startPos, drag.toVector(), speed, spread);
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
				this.turrets.remove(i);
				this.saveTurrets();
				return true;
			}
		}
		return false;
	}
	
	public boolean isInTurretArea (Player player, Vector turretLoc)
	{
		boolean inside = false;
		if (player.getLocation().getX() + 0.5 < turretLoc.getX() + this.xDis - 1 && player.getLocation().getX() + 0.5 > turretLoc.getX() - this.xDis - 1 &&
			player.getLocation().getZ() + 0.5 < turretLoc.getZ() + this.zDis && player.getLocation().getZ() + 0.5 > turretLoc.getZ() - this.zDis &&
			player.getLocation().getY() + 1 < turretLoc.getY() + this.yDis && player.getLocation().getY() + 1 > turretLoc.getY() - this.yDis)
		{
			inside = true;
		}
		return inside;
	}
	
	public void tryLoadConfig()
	{
		if (atdir.exists())
		{
			if (this.settingsFile.exists())
			{
				loadConfig(this.settingsFile);
			}
			else
				this.saveConfig();
		}
		else
		{
			if (atdir.mkdir())
				System.out.println("[ArrowTurrets] Created ArrowTurrets folder!");
			else
				System.out.println("[ArrowTurrets] Failed to create ArrowTurrets folder!");
			
		}
	}
	
	private void loadConfig(File file)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file.getName()));
			try
			{
				String line = br.readLine();
				if (line != null)
				{
					while (line != null)
					{
						if (!line.startsWith("//") || !line.startsWith("#") || !line.startsWith(";"))
						{
							String[] splitted = line.split("=");
							if (splitted.length > 1)
							{
								if (!splitted[0].equals("") && !splitted[1].equals(""))
								{
									if (splitted[0].equalsIgnoreCase("item-id"))
									{
										try {this.atItemId = Integer.valueOf(splitted[1]);}
										catch (NumberFormatException nfe){System.out.println("[ArrowTurrets] Failed to read item id from configure folder!");}
									}
									else if (splitted[0].equalsIgnoreCase("number-of-arrows"))
									{
										try {this.numberOfArrows = Integer.valueOf(splitted[1]);}
										catch (NumberFormatException nfe){System.out.println("[ArrowTurrets] Failed to read number of arrows from configure folder!");}
									}
									else if (splitted[0].equalsIgnoreCase("speed"))
									{
										try {this.speed = Float.valueOf(splitted[1]);}
										catch (NumberFormatException nfe){System.out.println("[ArrowTurrets] Failed to read speed from configure folder!");}
									}
									else if (splitted[0].equalsIgnoreCase("spread"))
									{
										try {this.spread = Integer.valueOf(splitted[1]);}
										catch (NumberFormatException nfe){System.out.println("[ArrowTurrets] Failed to read spread from configure folder!");}
									}
									else if (splitted[0].equalsIgnoreCase("turrets-filepath"))
									{
										this.turretsFilePath = splitted[1];
									}
									else if (splitted[0].equalsIgnoreCase("x-distance"))
									{
										try {this.xDis = Integer.valueOf(splitted[1]);}
										catch (NumberFormatException nfe){System.out.println("[ArrowTurrets] Failed to read x distance from configure folder!");}
									}
									else if (splitted[0].equalsIgnoreCase("y-distance"))
									{
										try {this.yDis = Integer.valueOf(splitted[1]);}
										catch (NumberFormatException nfe){System.out.println("[ArrowTurrets] Failed to read y distance from configure folder!");}
									}
									else if (splitted[0].equalsIgnoreCase("z-distance"))
									{
										try {this.zDis = Integer.valueOf(splitted[1]);}
										catch (NumberFormatException nfe){System.out.println("[ArrowTurrets] Failed to read z distance from configure folder!");}
									}
									else if (splitted[0].equalsIgnoreCase("delay"))
									{
										try {this.delay = Long.valueOf(splitted[1]);}
										catch (NumberFormatException nfe){System.out.println("[ArrowTurrets] Failed to read delay from configure folder!");}
									}
								}
							}
						}
						line = br.readLine();
					}
				}
			}
			catch (IOException e)
			{
				System.out.println(e.getStackTrace());
			}
			finally
			{
				br.close();
			}
		}
		catch (IOException e)
		{
			System.out.println(e.getStackTrace());
		}
	}
	
	private void saveConfig()
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(this.settingsFile));
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
				bw.write("; Id of what item you need to have in your hand");
				bw.newLine();
				bw.write("item-id=" + this.atItemId);
				bw.newLine();
				bw.write("; How many arrows that are shot at each burst");
				bw.newLine();
				bw.write("number-of-arrows=" + this.numberOfArrows);
				bw.newLine();
				bw.write("; The speed of the arrows");
				bw.newLine();
				bw.write("speed=" + this.speed);
				bw.newLine();
				bw.write("; How much the arrows are spread, similar to recoil and accuracy");
				bw.newLine();
				bw.write("spread=" + this.spread);
				bw.newLine();
				bw.write("; The delay between shots, to low and you might crash your server!");
				bw.newLine();
				bw.write("delay=" + this.delay);
				bw.newLine();
				bw.write("; The distance in the x-axis that the turrets search");
				bw.newLine();
				bw.write("x-distance=" + this.xDis);
				bw.newLine();
				bw.write("; The distance in the y-axis that the turrets search");
				bw.newLine();
				bw.write("y-distance=" + this.yDis);
				bw.newLine();
				bw.write("; The distance in the z-axis that the turrets search");
				bw.newLine();
				bw.write("z-distance=" + this.zDis);
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
					if (first)
					{
						bw.write(turret.getOwner() + ";" + turret.getLoc().getX() + "," + turret.getLoc().getY() + "," + turret.getLoc().getZ());
						first = false;
					}
					else
					{
						bw.newLine();
						bw.write(turret.getOwner() + ";" + turret.getLoc().getX() + "," + turret.getLoc().getY() + "," + turret.getLoc().getZ());
					}
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
						if (splitted.length > 1)
						{
							String[] loc = splitted[1].split(",");
							if (loc.length > 2)
							{
								this.turrets.add(new Turret(splitted[0], new Vector(Float.valueOf(loc[0]), Float.valueOf(loc[1]), Float.valueOf(loc[2]))));
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
}
