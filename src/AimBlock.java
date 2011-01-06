import java.util.ArrayList;

import org.bukkit.Block;
import org.bukkit.Location;
import org.bukkit.Player;
import org.bukkit.Vector;

public class AimBlock {

    private Location loc;
    private double viewPos;
    private int maxDistance;
    private int[] blockToIgnore;
    private double checkDistance, curDistance;
    private double xRotation, yRotation;
    private Vector targetPos = new Vector();
    private Vector prevPos = new Vector();
    private Vector offset = new Vector();

    /**
     * Constructor requiring a player, uses default values
     * 
     * @param player Player to work with
     */
    public AimBlock(Player player)
    {
        this.setValues(player.getLocation(), 300, 1.65, 0.2, null);
    }

    /**
     * Constructor requiring a location, uses default values
     * 
     * @param loc Location to work with
     */
    public AimBlock(Location loc)
    {
    	this.setValues(loc, 300, 0, 0.2, null);
    }
    
    /**
     * Constructor requiring a player, max distance, and a checking distance
     * 
     * @param player Player to work with
     * @param maxDistance How far it checks for blocks
     * @param checkDistance How often to check for blocks, the smaller the more precise
     */
    public AimBlock(Player player, int maxDistance, double checkDistance)
    {
    	this.setValues(player.getLocation(), maxDistance, 1.65, checkDistance, null);
    }

    /**
     * Constructor requiring a location, max distance, and a checking distance
     * 
     * @param loc What location to work with
     * @param maxDistance How far it checks for blocks
     * @param checkDistance How often to check for blocks, the smaller the more precise
     */
    public AimBlock(Location loc, int maxDistance, double checkDistance) {
    	this.setValues(loc, maxDistance, 1.65, checkDistance, null);
    }
    
    /**
     * Constructor requiring a player, max distance, checking distance and an array of blocks to ignore
     * 
     * @param loc What location to work with
     * @param maxDistance How far it checks for blocks
     * @param checkDistance How often to check for blocks, the smaller the more precise
     * @param blocksToIgnore Array of what block ids to ignore while checking for viable targets
     */
    public AimBlock (Player player, int maxDistance, double checkDistance, int[] blocksToIgnore)
    {
    	this.setValues(player.getLocation(), maxDistance, 1.65, checkDistance, blocksToIgnore);
    }
    
    /**
     * Constructor requiring a location, max distance, checking distance and an array of blocks to ignore
     * 
     * @param loc What location to work with
     * @param maxDistance How far it checks for blocks
     * @param checkDistance How often to check for blocks, the smaller the more precise
     * @param blocksToIgnore Array of what block ids to ignore while checking for viable targets
     */
    public AimBlock (Location loc, int maxDistance, double checkDistance, int[] blocksToIgnore)
    {
    	this.setValues(loc, maxDistance, 0, checkDistance, blocksToIgnore);
    }
    
    /**
     * Constructor requiring a player, max distance, checking distance and an array of blocks to ignore
     * 
     * @param loc What location to work with
     * @param maxDistance How far it checks for blocks
     * @param checkDistance How often to check for blocks, the smaller the more precise
     * @param blocksToIgnore Array of what block ids to ignore while checking for viable targets
     */
    public AimBlock (Player player, int maxDistance, double checkDistance, ArrayList<String> blocksToIgnore)
    {
    	int[] bti = this.convertStringArraytoIntArray(blocksToIgnore);
    	this.setValues(player.getLocation(), maxDistance, 1.65, checkDistance, bti);
    }
    
    /**
     * Constructor requiring a location, max distance, checking distance and an array of blocks to ignore
     * 
     * @param loc What location to work with
     * @param maxDistance How far it checks for blocks
     * @param checkDistance How often to check for blocks, the smaller the more precise
     * @param blocksToIgnore Array of what block ids to ignore while checking for viable targets
     */
    public AimBlock (Location loc, int maxDistance, double checkDistance, ArrayList<String> blocksToIgnore)
    {
    	int[] bti = this.convertStringArraytoIntArray(blocksToIgnore);
    	this.setValues(loc, maxDistance, 0, checkDistance, bti);
    }
    
    /**
     * Set the values, all constructors uses this function
     * 
     * @param loc
     * @param maxDistance How far it checks for blocks
     * @param viewPos Where the view is positioned in y-axis
     * @param checkDistance How often to check for blocks, the smaller the more precise
     * @param blocksToIgnore Ids of blocks to ignore while checking for viable targets
     */
    private void setValues(Location loc, int maxDistance, double viewPos, double checkDistance, int[] blocksToIgnore)
    {
    	this.loc = loc;
    	this.maxDistance = maxDistance;
    	this.viewPos = viewPos;
        this.checkDistance = checkDistance;
        this.blockToIgnore = blocksToIgnore;
        this.curDistance = 0;
        xRotation = (loc.getYaw() + 90) % 360;
        yRotation = loc.getPitch() * -1;

        targetPos = new Vector((int)Math.floor(loc.getX()), (int)Math.floor(loc.getY() + viewPos), (int)Math.floor(loc.getZ()));
        prevPos = targetPos.clone();
    }

    private void reset()
    {
    	targetPos = new Vector((int)Math.floor(this.loc.getX()), (int)Math.floor(this.loc.getY() + viewPos), (int)Math.floor(this.loc.getZ()));
        prevPos = targetPos.clone();
    	this.curDistance = 0;
    }
    
    /**
     * Gets the distance to a block
     * 
     * @return double
     */
    public double getDistanceToBlock()
    {
    	Vector blockUnderPlayer = new Vector(
    			(int) Math.floor(loc.getX() + 0.5),
    			(int) Math.floor(loc.getY() - 0.5),
    			(int) Math.floor(loc.getZ() + 0.5));

    	Block blk = getTargetBlock();
    	double x = blk.getX() - blockUnderPlayer.getBlockX();
    	double y = blk.getY() - blockUnderPlayer.getBlockY();
    	double z = blk.getZ() - blockUnderPlayer.getBlockZ();
    	
    	return Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));
    }
    
    /**
     * Gets the distance to a block
     * 
     * @return int The floored value of the distance
     */
    public int getDistanceToBlockRounded()
    {
    	Vector blockUnderPlayer = new Vector(
    			(int) Math.floor(loc.getX() + 0.5),
    			(int) Math.floor(loc.getY() - 0.5),
    			(int) Math.floor(loc.getZ() + 0.5));

    	Block blk = getTargetBlock();
    	double x = blk.getX() - blockUnderPlayer.getBlockX();
    	double y = blk.getY() - blockUnderPlayer.getBlockY();
    	double z = blk.getZ() - blockUnderPlayer.getBlockZ();
    	
    	return (int) Math.round((Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)))));
    }
    
    /**
     * Gets the floored x distance to a block
     * 
     * @return int The floored value of the distance
     */
    public int getXDistanceToBlock()
    {
    	this.reset();
    	return (int) Math.floor(getTargetBlock().getX() - loc.getBlockX() + 0.5);
    }
    
    /**
     * Gets the floored y distance to a block
     * 
     * @return double
     */
    public int getYDistanceToBlock()
    {
    	this.reset();
    	return (int) Math.floor(getTargetBlock().getY() - loc.getBlockY() + 1.65);
    }
    
    /**
     * Gets the floored z distance to a block
     * 
     * @return double
     */
    public int getZDistanceToBlock()
    {
    	this.reset();
    	return (int) Math.floor(getTargetBlock().getZ() - loc.getBlockZ() + 0.5);
    }
    
    /**
     * Returns the block at the aim, null if out of range or if no viable target found
     * 
     * @return Block
     */
    public Block getTargetBlock()
    {
        while ((getNextBlock() != null) && ((getCurrentBlock().getTypeID() == 0) || this.blockToIgnoreHasValue(getCurrentBlock().getTypeID())));
        return getCurrentBlock();
    }

    /**
     * Sets the type of the block at the cursor
     * 
     * @param type Id of type to set the block to
     */
    public void setTargetBlock(int type)
    {
    	while ((getNextBlock() != null) && ((getCurrentBlock().getTypeID() == 0) || this.blockToIgnoreHasValue(getCurrentBlock().getTypeID())));
        if (getCurrentBlock() != null)
        {
        	Block blk = loc.getWorld().getBlockAt(targetPos.getBlockX(), targetPos.getBlockY(), targetPos.getBlockZ());
        	blk.setTypeID(type);
        }
    }

    /**
     * Returns the block attached to the face at the cursor, or null if out of
     * range
     * 
     * @return Block
     */
    public Block getFaceBlock()
    {
    	while ((getNextBlock() != null) && ((getCurrentBlock().getTypeID() == 0) || this.blockToIgnoreHasValue(getCurrentBlock().getTypeID())));
        if (getCurrentBlock() != null)
        {
            return getPreviousBlock();
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the type of the block attached to the face at the cursor
     * 
     * @param type
     */
    public void setFaceBlock(int type)
    {
        if (getCurrentBlock() != null)
        {
        	Block blk = loc.getWorld().getBlockAt(prevPos.getBlockX(), prevPos.getBlockY(), prevPos.getBlockZ());
        	blk.setTypeID(type);
        }
    }

    /**
     * Get next block
     * 
     * @return Block
     */
    public Block getNextBlock()
    {
        prevPos = targetPos.clone();
        do
        {
            curDistance += checkDistance;

            double h = (curDistance * Math.cos(Math.toRadians(yRotation)));
            offset.setY((curDistance * Math.sin(Math.toRadians(yRotation)))); 
            offset.setX((h * Math.cos(Math.toRadians(xRotation))));
            offset.setZ((h * Math.sin(Math.toRadians(xRotation))));

            targetPos.setX((int) Math.floor(offset.getX() + loc.getX()));
            targetPos.setY((int) Math.floor(offset.getY() + loc.getY() + viewPos));
            targetPos.setZ((int) Math.floor(offset.getZ() + loc.getZ()));
            
        }
        while (curDistance <= maxDistance && targetPos.getBlockX() == prevPos.getBlockX() && targetPos.getBlockY() == prevPos.getBlockY() && targetPos.getBlockZ() == prevPos.getBlockZ());
        if (curDistance > maxDistance)
        {
            return null;
        }

        return this.loc.getWorld().getBlockAt(this.targetPos.getBlockX(), this.targetPos.getBlockY(), this.targetPos.getBlockZ());
    }

    /**
     * Returns the current block along the line of vision
     * 
     * @return Block
     */
    public Block getCurrentBlock()
    {
        if (curDistance > maxDistance)
        {
            return null;
        }
        else
        {
            return this.loc.getWorld().getBlockAt(this.targetPos.getBlockX(), this.targetPos.getBlockY(), this.targetPos.getBlockZ());
        }
    }

    /**
     * Sets current block type id
     * 
     * @param type
     */
    public void setCurrentBlock(int type)
    {
    	Block blk = getCurrentBlock();
        if (blk != null)
        {
        	blk.setTypeID(type); 
        }
    }

    /**
     * Returns the previous block in the aimed path
     * 
     * @return Block
     */
    public Block getPreviousBlock()
    {
        return this.loc.getWorld().getBlockAt(prevPos.getBlockX(), prevPos.getBlockY(), prevPos.getBlockZ());
    }

    /**
     * Sets previous block type id
     * 
     * @param type
     */
    public void setPreviousBlock(int type)
    {
    	Block blk = getPreviousBlock(); 
        if (blk != null)
        {
            blk.setTypeID(type);
        }
    }
    
    private int[] convertStringArraytoIntArray(ArrayList<String> array)
    {
		if (array != null)
		{
			int intarray[] = new int[array.size()];
			for (int i = 0; i < array.size(); i++)
			{
				try
				{
					intarray[i] = Integer.parseInt(array.get(i));
				}
				catch (NumberFormatException nfe)
				{
					intarray[i] = 0;
				}
			}
			return intarray;
		}
		return null;
    }
    
    private boolean blockToIgnoreHasValue(int value)
    {
    	if (this.blockToIgnore != null)
    	{
	    	if (this.blockToIgnore.length > 0)
	    	{
		    	for (int i : this.blockToIgnore)
		    	{
		    		if (i == value)
		    			return true;
		    	}
	    	}
    	}
    	return false;
    }
}
