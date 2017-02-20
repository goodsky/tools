package lsystem.engine;

import lsystem.visual.LSystemParams;

public class Sierpinski implements ILSystem
{
	private StringBuilder triangle;
	
	public Sierpinski()
	{
		
	}
	
	public ILSystem initialize(StringBuilder initialState, LSystemParams params)
	{
		triangle = initialState;
		
		return new Sierpinski();
	}
	
	public void step()
	{
		String temp = triangle.toString();
		
		temp = temp.replaceAll("b", "bb");
		temp = temp.replaceAll("a", "a-b+a+b-a");
		
		triangle = new StringBuilder(temp);
	}

	public StringBuilder getState() {
		
		return triangle == null ? new StringBuilder() : triangle;
	}
}
