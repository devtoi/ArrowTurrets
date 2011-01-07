import java.util.ArrayList;

import org.bukkit.Location;


public class Turret {

	private Location loc;
	private ArrayList<String> accessors = new ArrayList<String>();
	private ArrayList<String> owners = new ArrayList<String>();
	private boolean canShoot = true;
	
	public Turret (String owner, Location loc)
	{
		this.owners.add(owner);
		this.loc = loc;
	}
	
	public Turret (ArrayList<String> owners, ArrayList<String> accessors, Location loc)
	{
		this.owners.addAll(owners);
		this.accessors.addAll(accessors);
		this.loc = loc;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public ArrayList<String> getOwners() {
		return owners;
	}

	public void setOwners(ArrayList<String> owners) {
		this.owners = owners;
	}

	public boolean canShoot() {
		return canShoot;
	}

	public void setCanShoot(boolean canShoot) {
		this.canShoot = canShoot;
	}

	public ArrayList<String> getAccessors() {
		return accessors;
	}

	public void setAccessors(ArrayList<String> accessors) {
		this.accessors = accessors;
	}
}
