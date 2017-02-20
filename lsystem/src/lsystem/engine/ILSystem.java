package lsystem.engine;
import lsystem.visual.LSystemParams;

public interface ILSystem {
	// Pass in the axium to our LSystem
	public ILSystem initialize(StringBuilder initialState, LSystemParams params);
	//public ILSystem initialize(StringBuilder initialState /*, other param*/);
	
	// Hard-code the grammar rules
	public void step();
	
	// Return the final StringBuilder representing our LSystem
	public StringBuilder getState();
}
