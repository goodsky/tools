package lsystem.engine;

import lsystem.visual.LSystemParams;
import java.util.*;

// The Sunlight Plant is attracted to the sun! There is a slightly higher probablity of it growing towards the sun
public class SunlightPlant implements ILSystem {
	// The LSystem
	private StringBuilder plant;
	private LSystemParams params;

	Double sunx;
	Double suny;
	
	boolean verbose = false;
	
	public SunlightPlant()
	{
		sunx = null;
		suny = null;
	}
	
	// Create the initial axium that we will use to grow the plant
	public ILSystem initialize(StringBuilder initialState, LSystemParams params)
	{
		this.params = params;
		plant = initialState;

		if (sunx != null && suny != null) return this;
		
		// Input the sun's coordinates
		System.out.print("Input the sun coordinates x,y: ");
		Scanner in = new Scanner(System.in);
		
		sunx = in.nextDouble();
		suny = in.nextDouble()*-1; // silly y flip @-@
		
		params.graphicsAux.add(sunx);
		params.graphicsAux.add(suny);

		System.out.printf("sun:(%.2f, %.2f)\n", sunx, suny);
		return this;
	}
	
	public void step()
	{
		// This will be the new plant when we are done
		StringBuilder newplant = new StringBuilder("");
	
		// Simulate the current plant position
		double x = params.startx;
		double y = params.starty;
		double dir = params.startdir;
		
		Stack<Pos> stack = new Stack<Pos>();
		
		if (verbose)
			System.out.println("***************");
			
		for (int i = 0; i < plant.length(); i++)
		{
			// Next character
			char cur = plant.charAt(i);
			boolean step = false;
			
			// always cap the dir
			while (dir < Math.PI * -1) dir += 2*Math.PI;
			while (dir > Math.PI) dir -= 2*Math.PI;
			
			// Go Forward
			for (int k = 0; k < params.lenAlphabet.length && !step; k++)
			{
				// Straight Lines is where the GROWING happens!
				if (cur == params.lenAlphabet[k])
				{
					// calculate which side the sun is on (and to what degree it is on that side)
					double sundir = Math.atan2(y-suny,x-sunx)*-1;
					double diff = dir - sundir;
					
					while (diff < Math.PI * -1) diff += 2*Math.PI;
					while (diff > Math.PI) diff -= 2*Math.PI;
					
					if (verbose)
						System.out.printf("(%.2f,%.2f) sundir: %.2f dir: %.2f diff: %.2f\n", x, y, sundir, dir, diff);
					
					// turn left
					if (diff < 0)
					{
						newplant.append("aa-[-a+a+a]+[+a-a-a]");
					}
					// turn right
					else
					{
						newplant.append("aa+[+a-a-a]-[-a+a+a]");
					}

					// we end up moving twice the distance
					x += 2*params.length[k]*Math.cos(dir);
					y += 2*params.length[k]*Math.sin(dir);
					step = true;
				}
			}
			
			// make sure we don't double print the straight lines
			if (step) continue;
			
			// Turn Right
			for (int k = 0; k < params.lenAlphabet.length && !step; k++)
			{
				if (cur == params.angAlphabetR[k])
				{
					dir = (dir+params.angle[k])%(Math.PI*2);
					step = true;
				}
			}

			// Turn Left
			for (int k = 0; k < params.lenAlphabet.length && !step; k++)
			{
				if (cur == params.angAlphabetL[k])
				{
					dir -= params.angle[k];
					if (dir < 0.0) dir += Math.PI*2;
					
					step = true;
				}
			}
			
			// recursive steps
			// add to stack
			if (cur == '[')
			{
				stack.push(new Pos(x, y, dir));
			}
			
			// pop from stack and go back to that position
			if (cur == ']')
			{
				Pos back = stack.pop();
				x = back.x;
				y = back.y;
				dir = back.dir;
			}
			
			// Print Out
			newplant.append(cur);
		}

		plant = newplant;
	}
	
	public StringBuilder getState()
	{
		return plant == null ? new StringBuilder() : plant;
	}
	
	private class Pos {
		double x,y,dir;
		public Pos(double xx, double yy, double dd)
		{
			x = xx;
			y = yy;
			dir = dd;
		}
	}
}
