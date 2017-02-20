package lsystem.engine;

import lsystem.visual.LSystemParams;
import java.util.Stack;


public class Tree implements ILSystem
{
	private StringBuilder tree;
	private LSystemParams params;
	
	public Tree()
	{
		
	}
	
	public ILSystem initialize(StringBuilder initialState, LSystemParams params)
	{
		this.params = params;
		tree = initialState;
		
		return new Tree();
	}
	
	public void step()
	{
		// This will be the new plant when we are done
		StringBuilder newtree = new StringBuilder("");
		
		double x = params.startx;
		double y = params.starty;
		double dir = params.startdir;
	
		Stack<Pos> stack = new Stack<Pos>();
			
		for (int i = 0; i < tree.length(); i++)
		{
			// Next character
			char cur = tree.charAt(i);
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
					if(cur == 'a')
					{
						double r = Math.random() * 100;
						
						if(r < 90)
						{
							newtree.append("a[a+a][a-a]");
						}
						else if(r < 95)
						{
							newtree.append("[a*a]");
						}
						else if(r < 100)
						{
							newtree.append("[a&a]");
						}
					}
					else if(cur == 'b')
					{
						newtree.append("b");
					}
					else if(cur == 'c')
					{
						double r_1 = Math.random() * 100;
						
						if(r_1 < 100)
						{
							newtree.append("bbb[*[a+a]]b[&[a-a]]b");
						}
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
			newtree.append(cur);
		}

		tree = newtree;
	}

	public StringBuilder getState() {
		
		return tree == null ? new StringBuilder() : tree;
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
