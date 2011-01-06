import org.bukkit.Vector;


public class Turret {

	private Vector loc;
	private String owner;
	private boolean canShoot = true;
	
	public Turret (String owner, Vector loc)
	{
		this.owner = owner;
		this.loc = loc;
	}

	public Vector getLoc() {
		return loc;
	}

	public void setLoc(Vector loc) {
		this.loc = loc;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean canShoot() {
		return canShoot;
	}

	public void setCanShoot(boolean canShoot) {
		this.canShoot = canShoot;
	}
}
