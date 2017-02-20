/*
 * LSYSTEMPARAMS.JAVA
 * 
 * This class works a lot like a struct. It will read in the parameters for 
 * the L-System so that they are somewhere nice and easy to modify.
 * 
 * TODO:
 * -Everything
 */
package lsystem.visual;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.ArrayList;

public class LSystemParams {

	// Currenly I am just hacking in the parameters
	// Later I suggest we have an experiment.params file that we read in from
	public char[] lenAlphabet = {'a', 'b', 'c'};
	public char[] angAlphabetL = {'-', '&', '%'};
	public char[] angAlphabetR = {'+', '*', '^'};

	public double[] length = {1.0, 10.0, 100.0};
	public double[] angle  = {20.0*Math.PI/180.0, 20.0*Math.PI/180.0, 20.0*Math.PI/180.0};

	public double startx = 0.0;
	public double starty = 0.0;
	public double startdir = 0.0;
	
	// flag for any special graphics states
	public int graphics = 0;
	// note: use this list for anything you may need to draw. a general storage place.
	public ArrayList<Double> graphicsAux = new ArrayList<Double>();
	
	// ADDED 11/25/2011
	public StringBuilder initial = new StringBuilder();

	public static LSystemParams readInParams(String fileName) throws IOException
	{
		LSystemParams ret = new LSystemParams();
		Scanner in = new Scanner(new File(fileName));
		if(!in.hasNextLine())
		{
			System.out.println("empty parameter file, using default settings\n");
			return ret;
		}
		String alphabet = in.nextLine();
		StringTokenizer st = new StringTokenizer(alphabet," :=,");
		//trash
		String id = st.nextToken();
		if(id.startsWith("alphabet"))
		{
			int index = 0;
			try {
				while(st.hasMoreTokens())
				{
					if(index==3)
						throw new Exception();
					
					char alphaChar = st.nextToken().charAt(0);
					double length = Double.parseDouble(st.nextToken());

					ret.lenAlphabet[index]=alphaChar;
					ret.length[index++]=length;
				}

			} catch(Exception e) {
				
				System.out.printf("Additionally you are restricted to 3 letters in your alphabet.\n");
				System.out.printf("Reserved characters are *space*, *comma*, :, =\n");
				System.out.println("Failed reading in the alphabet.");
				System.out.println("Please format the alphabet as the following line shows.");
				System.out.printf("%-15s%%c=%%f\n\n","alphabet:");
			}
			st = new StringTokenizer(in.nextLine()," =:,");
			id = st.nextToken();
		}
		else
			System.out.println("No alphabet, using default. Checking turn grammar.\nIf you wanted an alphabet please start the line with \"alphabet:\"");

		if(id.startsWith("turn"))
		{
			int index = 0;
			try {
				while(st.hasMoreTokens())
				{
					if(index==3)
						throw new Exception();
					char left = st.nextToken().charAt(0);
					char right = st.nextToken().charAt(0);
					double angle = Double.parseDouble(st.nextToken());

					ret.angAlphabetL[index]=left;
					ret.angAlphabetR[index]=right;
					//convert from degrees to radians
					ret.angle[index++]=Math.toRadians(angle);
				}

			} catch(Exception e) {
				
				System.out.printf("Additionally you are restricted to 3 letters in your alphabet.\n");
				System.out.printf("Reserved characters are *space*, *comma*, :, =\n");
				System.out.println("Failed reading in the turn grammar.");
				System.out.println("Please format the alphabet as the following line shows.");
				System.out.printf("%-15s%%c,%%c=%%f\n","turn:");
				System.out.println("The first char is left, the second is for right turns, the number is the angle in degrees.\n");
			}
			st = new StringTokenizer(in.nextLine()," =:,");
			id = st.nextToken();
		}

		if(id.startsWith("start"))
		{
			try {
				double sx = 0, sy = 0, startAngle = 0;
				if(st.hasMoreTokens())
					sx = Double.parseDouble(st.nextToken());
				if(st.hasMoreTokens())
					sy = Double.parseDouble(st.nextToken());
				if(st.hasMoreTokens())
					startAngle = Math.toRadians(Double.parseDouble(st.nextToken()));

				ret.startx = sx;
				ret.starty = sy;
				ret.startdir = startAngle;

			} catch(Exception e) {

				System.out.printf("Reserved characters are *space*, *comma*, :, =\n");
				System.out.println("Failed reading in the start parameters.");
				System.out.println("Please format the alphabet as the following line shows.");
				System.out.printf("%-15s%%f,%%f,%%f\n","start:");
				System.out.println("They are, in order: start x, start y, start angle");
				System.out.println("If you put less than 3 parameters it will automatically set the others to default.");
				System.out.println("For instance: \"1.5,2.5\" will make start x=1.5, start y=2.5 and start angle=0\n");

			}
			
			st = new StringTokenizer(in.nextLine()," =:,");
			id = st.nextToken();
		}

		// ADDED INITIAL -skyler
		if(id.startsWith("initial"))
		{
			try {
				StringBuilder initial;

				String si = st.nextToken();
				initial = new StringBuilder(si);
				
				ret.initial = initial;

			} catch(Exception e) {

				System.out.printf("Reserved characters are *space*, *comma*, :, =\n");
				System.out.println("Failed reading in the initial axium.");
			}
			
			if (in.hasNext())
			{
				st = new StringTokenizer(in.nextLine()," =:,");
				id = st.nextToken();
			}
		}
		
		// ADDED GRAPHICS -skyler
		if(id.length() > 0 && id.startsWith("graphics"))
		{
			try {
				String g = st.nextToken();
				
				// TODO: Add your own graphics flags here if you need them
				if (g.equals("plant"))
					ret.graphics = 1;
				if (g.equals("radial"))
					ret.graphics = 2;
				if (g.equals("root"))
					ret.graphics = 3;
                                if (g.equals("rootwater"))
                                        ret.graphics = 4;

			} catch(Exception e) {

				System.out.printf("Reserved characters are *space*, *comma*, :, =\n");
				System.out.println("Failed reading in the graphics mode.");
			}
		}
		
		return ret;
	}

	public String toString()
	{
		StringBuilder ret = new StringBuilder();
		ret.append("Length Grammar: ");
		for(int i = 0; i < lenAlphabet.length; i++)
			ret.append(String.format("%c=%.2f%s",lenAlphabet[i],length[i],i==lenAlphabet.length-1?"\n":", "));

		ret.append("Turn Grammar: ");
		for(int i = 0; i < length.length; i++)
			ret.append(String.format("%c,%c=%.2f%s",angAlphabetL[i],angAlphabetR[i],Math.toDegrees(angle[i]),i==length.length-1?"\n":", "));
		
		ret.append(String.format("Starting Values: (%.2f, %.2f) facing %.2f\n",this.startx, this.starty, Math.toDegrees(this.startdir)));
		
		ret.append("Starting Axium: " + this.initial + "\n");
		
		ret.append("Graphics Mode: " + this.graphics);
		
		return ret.toString();
	}
}