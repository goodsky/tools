package lsystem.engine;

import lsystem.visual.LSystemParams;
import java.util.*;

// The Sunlight Plant is attracted to the sun! There is a slightly higher probablity of it growing towards the sun
public class RootsWater implements ILSystem {
	// The LSystem
	private StringBuilder roots;
	private LSystemParams params;
	private Random r;
	
	ArrayList<Double> rockx, rocky, radii;
        ArrayList<Double> waterx, watery, waterr;
	int nrocks, nwaters;
	
	boolean verbose = false;
	
	public RootsWater()
	{
		rockx = new ArrayList<Double>();
		rocky = new ArrayList<Double>();
		radii = new ArrayList<Double>();
                waterx = new ArrayList<Double>();
                watery = new ArrayList<Double>();
                waterr = new ArrayList<Double>();
		r = new Random();
	}
	
	// Create the initial axium that we will use to grow the plant
	public ILSystem initialize(StringBuilder initialState, LSystemParams params)
	{
		this.params = params;
		roots = initialState;
		
		//read in rocks
		Scanner in = new Scanner(System.in);
		while(true)
		{
			System.out.printf("Please input the (x,y) and radius of the rocks: ");
			String t = in.next();
			if(t.startsWith("e"))
				break;
			rockx.add(Double.parseDouble(t));
			rocky.add(in.nextDouble() * -1); // flip y
			radii.add(in.nextDouble());
		}

                while(true)
		{
			System.out.printf("Please input the (x,y) and radius of the pools: ");
			String t = in.next();
			if(t.startsWith("e"))
				break;
			waterx.add(Double.parseDouble(t));
			watery.add(in.nextDouble() * -1); // flip y
			waterr.add(in.nextDouble());
		}
		
		nrocks = rockx.size();
		params.graphicsAux.add(nrocks*1.0);
		for(int i = 0; i < rockx.size(); i++)
		{
			params.graphicsAux.add(rockx.get(i));
			params.graphicsAux.add(rocky.get(i));
			params.graphicsAux.add(radii.get(i));
		}

                nwaters = waterx.size();
		params.graphicsAux.add(nwaters*1.0);
		for(int i = 0; i < nwaters; i++)
		{
			params.graphicsAux.add(waterx.get(i));
			params.graphicsAux.add(watery.get(i));
			params.graphicsAux.add(waterr.get(i));
		}
		return this;
	}
	
	public void step()
	{
		// This will be the new plant when we are done
		StringBuilder newroots = new StringBuilder("");
	
		// Simulate the current plant position
		double x = params.startx;
		double y = params.starty;
		double dir = params.startdir;
		
		Stack<Pos> stack = new Stack<Pos>();
					
		for (int i = 0; i < roots.length(); i++)
		{
			// Next character
			char cur = roots.charAt(i);
			boolean step = false;
	
			if(cur=='b' || cur == 'c')
			{
				newroots.append(cur);
				for (int k = 0; k < params.lenAlphabet.length; k++)
				{
					if (cur == params.lenAlphabet[k])
					{
						x += params.length[k]*Math.cos(dir);
						y += params.length[k]*Math.sin(dir);
					}
				}
			
				step = true;
			}
			
			// always cap the dir
			while (dir < Math.PI * -1) dir += 2*Math.PI;
			while (dir > Math.PI) dir -= 2*Math.PI;
			
			// Go Forward
			for (int k = 0; k < params.lenAlphabet.length && !step; k++)
			{
				// Straight Lines is where the GROWING happens!
				if (cur == params.lenAlphabet[k])
				{
					String tappend = "ccc"+(r.nextBoolean()?"-[-cca][cca]":"+[+cca][cca]");
					//find the closest rock
					//indices of close rock
					int indexr = -1;
					//likely few rocks, linear search is acceptable for closest neighbor
					int tstepr = -1;
					double turnr = -1;
					for(int j = 0; j < rockx.size(); j++)
					{
						double nx = x;
						double ny = y;
						double tdir = dir;
						Stack<Pos> tstack = new Stack<Pos>();
						for(int l = 0; l < tappend.length(); l++)
						{
							char tcur = tappend.charAt(l);
							double dx = rockx.get(j)-nx;
							double dy = rocky.get(j)-ny;
							double distance = Math.sqrt(dx*dx+dy*dy);
							//System.out.printf("Distance from ghost point (%.2f, %.2f) is %.2f\n",nx,ny,distance);
							if(distance < radii.get(j))
							{
								double angToCenter = Math.atan2(rocky.get(j)-ny, rockx.get(j)-nx);
								while (angToCenter < Math.PI * -1) angToCenter += 2*Math.PI;
								while (angToCenter > Math.PI) angToCenter -= 2*Math.PI;
								turnr = angToCenter-tdir;
								indexr = j;
								tstepr = l;
								break;
							}
							else
							{
								if(tcur == 'a' || tcur=='c')
								{
									nx+=params.length[k]*Math.cos(tdir);
									ny+=params.length[k]*Math.sin(tdir);
								}
								// Turn Right
								if(tcur=='+')
									tdir-=Math.toRadians(15);
							
								// Turn Left
								if(tcur=='-')
									tdir+=Math.toRadians(15);
								
								// recursive steps
								// add to stack
								if (tcur == '[')
								{
									tstack.push(new Pos(nx, ny, tdir));
								}
								
								// pop from stack and go back to that position
								if (tcur == ']')
								{
									Pos back = tstack.pop();
									nx = back.x;
									ny = back.y;
									tdir = back.dir;
								}
							}
						}
					}
                                        
                                        int indexw = -1;
					//likely few rocks, linear search is acceptable for closest neighbor
					int tstepw = -1;
					double turnw = -1;

                                        for(int j = 0; j < waterx.size(); j++)
					{
						double nx = x;
						double ny = y;
						double tdir = dir;
						Stack<Pos> tstack = new Stack<Pos>();
						for(int l = 0; l < tappend.length(); l++)
						{
							char tcur = tappend.charAt(l);
							double dx = waterx.get(j)-nx;
							double dy = watery.get(j)-ny;
							double distance = Math.sqrt(dx*dx+dy*dy);
							//System.out.printf("Distance from ghost point (%.2f, %.2f) is %.2f\n",nx,ny,distance);
							if(distance < waterr.get(j))
							{
								double angToCenter = Math.atan2(watery.get(j)-ny, waterx.get(j)-nx);
								while (angToCenter < Math.PI * -1) angToCenter += 2*Math.PI;
								while (angToCenter > Math.PI) angToCenter -= 2*Math.PI;
								turnw = angToCenter-tdir;
								indexw = j;
								tstepw = l;
								break;
							}
							else
							{
								if(tcur == 'a' || tcur=='c')
								{
									nx+=params.length[k]*Math.cos(tdir);
									ny+=params.length[k]*Math.sin(tdir);
								}
								// Turn Right
								if(tcur=='+')
									tdir-=Math.toRadians(15);

								// Turn Left
								if(tcur=='-')
									tdir+=Math.toRadians(15);

								// recursive steps
								// add to stack
								if (tcur == '[')
								{
									tstack.push(new Pos(nx, ny, tdir));
								}

								// pop from stack and go back to that position
								if (tcur == ']')
								{
									Pos back = tstack.pop();
									nx = back.x;
									ny = back.y;
									tdir = back.dir;
								}
							}
						}
					}
					
					//no close rocks to avoid
					if(indexr==-1)
					{
                                            if(indexw!=-1)
                                            {
                                                newroots.append("ccc");
                                                newroots.append(tappend);
                                            }
                                            else
                                                newroots.append(tappend);
					}
					else
					{
					//	newroots.append('c');/*
						if(turnr < 0)
                                                {
							while(turnr<0)
							{
								turnr+=Math.toRadians(15);
								newroots.append('-');
							}
                                                }
                                                else if(turnr > 0)
                                                {
                                                    while (turnr > 0)
							{
								turnr-=Math.toRadians(15);
								newroots.append('+');
							}
                                                }
                                                int count = 0;
						for(int l = 0; l < tstepr; l++)
						{
							newroots.append(tappend.charAt(l));
							if(tappend.charAt(l)=='[')
								count++;
							if(tappend.charAt(l)==']')
								count--;
						}
						
						while (turnr < Math.PI * -1) turnr += 2*Math.PI;
						while (turnr > Math.PI) turnr -= 2*Math.PI;
						
						if(count>0)
                                                {
                                                    newroots.append('a');
                                                    newroots.append(']');
                                                }
					}
					
					// we end up moving twice the distance
					x += params.length[k]*Math.cos(dir);
					y += params.length[k]*Math.sin(dir);
					step = true;
				}
			}
			
			// make sure we don't double print the straight lines
			if (step) continue;
			
			// Turn Right
			for (int k = 0; k < params.angAlphabetR.length && !step; k++)
			{
				if (cur == params.angAlphabetR[k])
				{
					dir -= params.angle[k];
					if (dir < 0.0) dir += Math.PI*2;
					step = true;
				}
			}

			// Turn Left
			for (int k = 0; k < params.angAlphabetL.length && !step; k++)
			{
				if (cur == params.angAlphabetL[k])
				{
					dir = (dir+params.angle[k])%(Math.PI*2);
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
			newroots.append(cur);
		}
		
		roots = newroots;
	}
	
	public StringBuilder getState()
	{
		return roots == null ? new StringBuilder() : roots;
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
