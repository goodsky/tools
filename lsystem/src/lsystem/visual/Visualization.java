/*
 * VISUALIZATION.JAVA
 * 
 * This class primarily works on displaying the pre-made L-System strings.
 * The tasks include drawing the image based on a string, displaying the 
 * drawing on a JFrame and whatever other features we can fit in before finals week.
 * 
 * TODO:
 * -Everything
 * -Draw L-System from String
 * -Display Window
 * -Read in Parameters File
 */
package lsystem.visual;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;

// ***********************
// * Visualization Class *
// ***********************
public class Visualization {
	
	// Static Fields
	public static int width = 500;
	public static int height = 500;
	
	// LSystem: The L-System string that we will be drawing
	public StringBuilder LSystem = null;
	
	// Params: The Parameters used to draw the L-System
	LSystemParams Params = new LSystemParams(); // default values

	// These are used to reflect the step and get methods from our engine classes
	Object sim;
	Method step;
	Method get;
	
	// ************
	// SetLSystem
	// Sets the L-System string that we will be drawing
	// The engine that creates these strings will be created later
	public void setLSystem(StringBuilder s)
	{
		LSystem = s;
	}
	
	// SetParams
	// Set the L-System parameters
	public void setParams(LSystemParams l)
	{
		Params = l;
	}

	// *******
	public void setReflectionMethods(Object si, Method s, Method g)
	{
		sim = si;
		step = s;
		get = g;
	}

	// *******
	public void step()
	{
		try {
			// Step
			step.invoke(sim);

			// Get the resulting String Builder
			get.setAccessible(true);
			Object ret = get.invoke(sim);

			if (ret instanceof StringBuilder)
				LSystem = (StringBuilder)ret;
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	// ************
	// DrawLSystem
	// The meat of the visualization tool
	// Draws an LSystem on the provided Graphics object
	private void drawLSystem(Graphics g)
	{
		// If we never set the L-System do nothing
		if (LSystem == null) return;
		
		// I will do 2 passes of the data
		// First I store all points into an ArrayList (and keep track of extreme values for normalization)
		// Then I draw the lines
		ArrayList<Pos> pts = new ArrayList<Pos>();
		
		// These parameters are used in the simulation
		double x = Params.startx;
		double y = Params.starty;
		double dir = Params.startdir;
		
		// Use this for normalizing the data
		double minx = x;
		double maxx = x;
		double miny = y;
		double maxy = y;
		
		// Use this for visualization awesomeness if you would like
		int count = 0;
		int maxcount = 0;
		
		// Use this stack for recursing
		Stack<Pos> stack = new Stack<Pos>();
		
		// Add the starting point
		pts.add(new Pos(x, y, dir, true));
		
		// Simulate all points
		for (int i = 0; i < LSystem.length(); i++)
		{
			// Next character
			char cur = LSystem.charAt(i);
			
			boolean step = false;
			boolean draw = true;
			
			// Go Forward
			for (int k = 0; k < Params.lenAlphabet.length && !step; k++)
			{
				if (cur == Params.lenAlphabet[k])
				{
					x += Params.length[k]*Math.cos(dir);
					y += Params.length[k]*Math.sin(dir);
					
					step = true;
				}
			}
			// Turn Right
			for (int k = 0; k < Params.lenAlphabet.length && !step; k++)
			{
				if (cur == Params.angAlphabetR[k])
				{
					//dir = (dir+Params.angle[k]) % (Math.PI * 2);
					dir -= Params.angle[k];
					if (dir < 0.0) dir += Math.PI*2;
					step = true;
				}
			}

			// Turn Left
			for (int k = 0; k < Params.lenAlphabet.length && !step; k++)
			{
				if (cur == Params.angAlphabetL[k])
				{
					//dir -= Params.angle[k];
					//if (dir < 0.0) dir += Math.PI*2;
					dir = (dir+Params.angle[k]) % (Math.PI * 2);
					step = true;
				}
			}
			
			// recursive steps
			// add to stack
			if (cur == '[')
			{
				Pos marker = new Pos(x, y, dir, false);
				marker.dist = count;
				stack.push(marker);
				continue;
			}
			
			// pop from stack and go back to that position
			if (cur == ']')
			{
				Pos back = stack.pop();
				x = back.x;
				y = back.y;
				dir = back.dir;
				count = back.dist;
				
				pts.add(back);
				continue;
			}
			
			// check for extremes
			minx = Math.min(x, minx);
			miny = Math.min(y, miny);
			maxx = Math.max(x, maxx);
			maxy = Math.max(y, maxy);
			
			// Set the point
			Pos temp = new Pos(x, y, dir, draw);
			temp.dist = count;
			pts.add(temp);
			
			// count
			count++;
			maxcount = Math.max(maxcount, count);
		}
		
		// If we are drawing the sun, then make sure the sun fits in our drawing
		if (Params.graphics == 1 && !Params.graphicsAux.isEmpty())
		{
			minx = Math.min(Params.graphicsAux.get(0), minx);
			miny = Math.min(Params.graphicsAux.get(1), miny);
			maxx = Math.max(Params.graphicsAux.get(0), maxx);
			maxy = Math.max(Params.graphicsAux.get(1), maxy);
		}
		
		// Normalize to the 500 by 500 field
		
		// divide by 0 error... shouldn't happen but who knows!
		if (maxx == minx) 
		{
			maxx += 0.5;
			minx -= 0.5;
		}
		
		if (maxy == miny)
		{
			maxy += 0.5;
			miny -= 0.5;
		}
		
		// Scale
		double scalex = width / (maxx - minx);
		double scaley = height / (maxy - miny);
		
		// Output the Size of the Visualization
		System.out.printf("Visualization Bounding Box: minx=%.1f maxx=%.1f  miny=%.1f maxy=%.1f\n",minx, maxx, miny, maxy);
		
		// Translation
		int dx = 0;
		int dy = 0;
		
		// If the aspect ratio is not constant, select the minimum of the two to keep the scaling ratio
		// Then grab the translation value
		if (scalex > scaley + 1e-9 || scalex < scaley - 1e-9)
		{
			// select the smaller scale
			// shift the other one to be centered
			if (scalex < scaley)
			{
				scaley = scalex;
				dx += (int)Math.ceil(minx*-1*scalex) + 25;
				dy += (int)Math.ceil(miny*-1*scaley) + 25;
			
				dy += (int)Math.ceil((height - (scaley*maxy + dy))/2);
			}
			else
			{
				scalex = scaley;
				dx += (int)Math.ceil(minx*-1*scalex) + 25;
				dy += (int)Math.ceil(miny*-1*scaley) + 25;
				
				dx += (int)Math.ceil((width - (scalex*maxx + dx))/2);
			}
		}
		else
		{
			// Displacement
			dx += (int)Math.ceil(minx*-1*scalex) + 25;
			dy += (int)Math.ceil(miny*-1*scaley) + 25;
		}
		
		// Clear the background
		g.setColor(Color.WHITE);
		g.fillRect(25, 25, width, height);
		
		// Draw the environment
		// Sun
		if (Params.graphics == 1 && !Params.graphicsAux.isEmpty())
		{
			g.setColor(Color.RED);
			g.fillOval((int)(Params.graphicsAux.get(0)*scalex) + dx, (int)(Params.graphicsAux.get(1)*scaley) + dy, (int)(2.5*scalex), (int)(2.5*scaley));
		}
		
		// Rocks
		if (Params.graphics == 3 && !Params.graphicsAux.isEmpty())
		{
			int numRocks = (int)(Params.graphicsAux.get(0)*1.0);
			g.setColor(Color.BLACK);
			System.out.printf("There are %d rocks.\n",numRocks);
			for(int j = 0; j < numRocks; j++)
			{
				double tx = Params.graphicsAux.get(1+3*j);
				double ty = Params.graphicsAux.get(2+3*j);
				double tr = Params.graphicsAux.get(3+3*j);
				g.fillOval((int)((tx-tr)*scalex)+dx,(int)((ty-tr)*scaley)+dy,(int)(tr*2*scalex),(int)(tr*2*scaley));
			}
		}
                if(Params.graphics == 4 && !Params.graphicsAux.isEmpty())
                {
                    int numRocks = (int)(Params.graphicsAux.get(0)*1.0);
                    int base = 3*numRocks+1;
                    g.setColor(Color.BLUE);
                    int numPools = (int)(Params.graphicsAux.get(base)*1.0);
                    System.out.printf("There are %d pools.\n",numPools);
                    for(int j = 0; j < numPools; j++)
                    {
                        double tx = Params.graphicsAux.get(1+3*j+base);
			double ty = Params.graphicsAux.get(2+3*j+base);
			double tr = Params.graphicsAux.get(3+3*j+base);
			g.fillOval((int)((tx-tr)*scalex)+dx,(int)((ty-tr)*scaley)+dy,(int)(tr*2*scalex),(int)(tr*2*scaley));
                    }
                    g.setColor(Color.BLACK);
                    System.out.printf("There are %s rocks.\n",numRocks);
                    for(int j = 0; j < numRocks; j++)
                    {
                        double tx = Params.graphicsAux.get(1+3*j);
			double ty = Params.graphicsAux.get(2+3*j);
			double tr = Params.graphicsAux.get(3+3*j);
			g.fillOval((int)((tx-tr)*scalex)+dx,(int)((ty-tr)*scaley)+dy,(int)(tr*2*scalex),(int)(tr*2*scaley));
                    }
                }
		
		// Loop through the points and draw them now
		if (Params.graphics == 0)
			g.setColor(Color.BLACK);
		else if (Params.graphics == 1)
			g.setColor(new Color(0,128,0));
		else if(Params.graphics == 3 || Params.graphics == 4) //set root color
			g.setColor(new Color(139,69,19));
		
		Pos last = null;
		for (Pos p : pts)
		{
                        if (last != null && p.draw)
			{
				// Set the Color if necessary
				if (Params.graphics == 1)
				{
					g.setColor(new Color(0, (int)(((double)last.dist/maxcount)*128 + 127), 0));
				}
				else if (Params.graphics == 2)
				{
					double dist = Math.sqrt(last.x*last.x + last.y*last.y);
					//normalize between 127 - 255
					double maxdist = Math.max(Math.abs(maxy), Math.abs(miny)); // simplification
					g.setColor(new Color(0, (int)((dist/maxdist)*128 + 127), 0));
				}
				
				// Draw the Line
				g.drawLine((int)(last.x*scalex) + dx, (int)(last.y*scaley) + dy, (int)(p.x*scalex) + dx, (int)(p.y*scaley) + dy);
			}
			
			last = p;
		}
	}
	
	// *************
	// GetWindow
	// This will return a handle to a window with your L-System Drawn on it
	public Window getWindow()
	{
		return new Window();
	}
	
	// ***********************
	// * Window Class        *
	// ***********************
	// This class is a window that displays the resultant LSystem.
	// Uses javax.swing.JFrame as the container.
	@SuppressWarnings("serial")
	public class Window extends JFrame
	{
		public JButton step;

		// ***********
		// Constructor
		public Window()
		{
			// Default Parameters
			// Window Title
			super("L-System Visualization");
			
			// Close frame when you click on the 'exit'
			// TODO: We may need to remove this depending on how we integrate it into the rest of the system
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			// Step
			step = new JButton("Step");

			setLayout(null);
			add(step);

			step.setBounds(width/2 - 25, height + 10, 100, 25);
			
			// Meh, no resizing to reduce potential headaches
            setResizable(false);
            
            // Feel free to change the window size
            setSize(width+ 50, height + 75);
            
            // This will center the window on your screen. Change it if you need to.
            setLocationRelativeTo(null);
		}
		
		// *********
		// Update
		// When you shift the JFrame or something covers it up you need to update...
		// I don't care about being too efficient, just redraw everything
		public void update(Graphics g)
		{
			super.update(g);
			paint(g);
		}
		
		// ***********
		// Paint
		// Draw the L-System
		public void paint(Graphics g)
		{
			super.paint(g);
			
			// Draw the L-System, how nice and easy!
			drawLSystem(g);
		}
	}
	
	// I don't want to do this, but the awt Point class does not seem to like doubles
	private class Pos {
		public double x, y, dir;
		
		// for coloring (says which step this was drawn on)
		public int dist;
		
		// if this is a drawn point
		public boolean draw;
		
		public Pos(double xx, double yy, double dd1, boolean dd2)
		{
			x = xx;
			y = yy;
			dir = dd1;
			
			draw = dd2;
		}
	}
}
