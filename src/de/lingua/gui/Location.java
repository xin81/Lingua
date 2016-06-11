package de.lingua.gui;
/**
 * Location points for {@link de.lingua.gui.LFrame}. This class supports x, y, and z values. */
public class Location {
	private int x;	// x position
	private int y;	// y position
	private int z;	// z position
	
	Location(int x, int y){
		this(x, y, 0);
	}
	
	Location(int x, int y, int z){
		setX(x);
		setY(y);
		setZ(z);
	}
	
	protected void setX(int x){
		this.x=x;
	}
	
	protected void setY(int y){
		this.y=y;
	}
	protected void setZ(int z){
		this.z=z;
	}
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	public int getZ(){
		return z;
	}
}
