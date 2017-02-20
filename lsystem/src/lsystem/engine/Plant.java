package lsystem.engine;

import lsystem.visual.LSystemParams;
import java.util.*;

// The plant is a default L-System Model
public class Plant implements ILSystem {

	// The LSystem
	private StringBuilder plant;
	
	public Plant()
	{
		
	}
	
	public ILSystem initialize(StringBuilder initialState, LSystemParams params)
	{
		plant = initialState;
		
		return new SunlightPlant();
	}
	
	public void step()
	{
		String temp = plant.toString();
		
		plant = new StringBuilder(temp.replaceAll("a", "aa-[-a+a+a]+[+a-a-a]"));
	}
	
	public StringBuilder getState()
	{
		return plant == null ? new StringBuilder() : plant;
	}
}