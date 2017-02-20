package lsystem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import lsystem.visual.*;
import lsystem.engine.*;

public class Main {
	
	// Main Entry Point for our program
	public static void main(String[] args)
	{
		Main program = new Main();
	}
	
	public Main()
	{
		MainWindow window = new MainWindow();
		window.setVisible(true);
		
		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
 
		// Determine the new location of the window
		int w = window.getSize().width;
		int h = window.getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		 
		// Move the window
		window.setLocation(x, y);
	}
	
	@SuppressWarnings("unchecked")
	private void runSimulation(String domain, String params, int steps)
	{
		// Load the Parameters File FIRST
		LSystemParams ls = new LSystemParams();
	
		// Try reading in a parameters file
		System.out.println("attempting to open " + params + ".lsp");
		try {
			ls = LSystemParams.readInParams(params + ".lsp");
			System.out.println(ls);
		} catch(Exception e){ System.out.println("Could not load Parameter File. Using default parameters."); }

		// Create the Simulation via reflection
		// This is the first time I've used reflection like this, so I apologize for the mess. -skyler
		Class simraw;
		Method init;
		Method step;
		Method get;
		
		// Load the class
		// Load the 3 primary methods from our interface (intitialize, step, getState)
		try {
			simraw = Class.forName("lsystem.engine." + domain);
			
			Class[] p1 = new Class[2];
			p1[0] = Class.forName("java.lang.StringBuilder");
			p1[1] = Class.forName("lsystem.visual.LSystemParams");
			
			init = simraw.getMethod("initialize", p1);
			step = simraw.getMethod("step");
			get = simraw.getMethod("getState");
		} catch (Exception e) { e.printStackTrace(); return; }

		// Initialize the simulation, step it however many times we need to, then return the string builder
		StringBuilder output = new StringBuilder();
		Object sim;
		try {
			// Create the final simulation object
			Constructor simct = simraw.getConstructor();
			sim = simct.newInstance();
			
			// Initialize
			init.invoke(sim, ls.initial, ls);
			
			// Step
			for (int i = 0; i < steps; i++)
				step.invoke(sim);
			
			// Get the resulting String Builder
			get.setAccessible(true);
			Object ret = get.invoke(sim);
			
			if (ret instanceof StringBuilder)
				output = (StringBuilder)ret;
		} catch (Exception e) { e.printStackTrace(); return; }
		
		// We are DONE!
		// Show the visualization, whew
		// Create a new Visualiation Object
		Visualization visualization = new Visualization();
		
		// Set the Parameters file
		visualization.setParams(ls);
		
		// Set the L-System
		// note: I am just hacking in a string right now, we will generate these later
		visualization.setLSystem(output);
		
		// Show the L-System
		Visualization.Window window = visualization.getWindow();
		window.setVisible(true);

		// Prepare the step button
		visualization.setReflectionMethods(sim, step, get);
		// Add the step button
		window.step.addActionListener(new StepAction(visualization, window));
		
		// Put out some output
		System.out.println("*************************");
	}

	// This is the little JFrame to help you select your domain and stuff
	private class MainWindow extends JFrame
	{
		private JLabel labelTitle;
		private JLabel labelDomain;
		private JLabel labelParams;
		private JLabel labelSteps;
		
		private JComboBox comboDomain;
		private JComboBox comboParams;
		private JSlider slideSteps;
		
		private JSeparator line1;
		private JButton buttonGo;
		
		public MainWindow()
		{
			try {
				initComponents();
			} catch (IOException e) { System.out.println("It looks like you are running this program from a different folder. Make sure the executable is running in the folder 'LSystem'"); }
		
			initListeners();
		}
		
		/* Set up the listeners for the components */
		private void initListeners()
		{
			buttonGo.addActionListener(new ActionListener() { 
						public void actionPerformed(ActionEvent e) {
							runSimulation(comboDomain.getSelectedItem().toString(), comboParams.getSelectedItem().toString(), slideSteps.getValue());
							}});
		}
		
		// Use this to see what different domains and param files we have
		private String[] populateFiles(String dir, String type) throws IOException
		{
			File location = new File(dir);
			String[] allfiles = location.list();
		
			ArrayList<String> files = new ArrayList<String>();
			for (String s : allfiles)
				if (s.endsWith(type) && !s.equals("ILSystem.class") && s.indexOf('$') == -1) files.add(s.substring(0,s.length()-type.length()));
				
			String[] goodfiles = new String[files.size()];
			for (int i = 0; i < files.size(); i++)
				goodfiles[i] = files.get(i);

			return goodfiles;
		}

		/* Thank you NetBeans for your pretty GUI editor */
		@SuppressWarnings("unchecked")
		private void initComponents() throws IOException
		{

			labelTitle = new javax.swing.JLabel();
			line1 = new javax.swing.JSeparator();
			comboDomain = new javax.swing.JComboBox();
			labelDomain = new javax.swing.JLabel();
			labelParams = new javax.swing.JLabel();
			comboParams = new javax.swing.JComboBox();
			slideSteps = new javax.swing.JSlider();
			labelSteps = new javax.swing.JLabel();
			buttonGo = new javax.swing.JButton();

			setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

			labelTitle.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
			labelTitle.setText("Welcome to the Dynamic L-System Creator:");

			comboDomain.setModel(new javax.swing.DefaultComboBoxModel(populateFiles("./bin/lsystem/engine", ".class")));

			labelDomain.setText("Select Domain:");

			labelParams.setText("Select Parameters:");

			comboParams.setModel(new javax.swing.DefaultComboBoxModel(populateFiles("./", ".lsp")));

			slideSteps.setMajorTickSpacing(1);
			slideSteps.setMaximum(12);
			slideSteps.setPaintLabels(true);
			slideSteps.setPaintTicks(true);
			slideSteps.setSnapToTicks(true);
			slideSteps.setValue(3);
			
			labelSteps.setText("Set Steps:");

			buttonGo.setText("Run");

			javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(line1, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(labelTitle)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(labelParams)
								.addComponent(labelDomain))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(comboDomain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(comboParams, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGroup(layout.createSequentialGroup()
							.addComponent(labelSteps)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
							.addComponent(slideSteps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(18, 18, 18)
							.addComponent(buttonGo)))
					.addContainerGap())
			);
			layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(labelTitle)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(line1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(labelDomain)
								.addComponent(comboDomain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(labelParams)
								.addComponent(comboParams, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGap(18, 18, 18)
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(labelSteps)
								.addComponent(slideSteps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGap(28, 28, 28))
						.addGroup(layout.createSequentialGroup()
							.addComponent(buttonGo)
							.addContainerGap())))
			);

			pack();
		}// </editor-fold>
	}

	private class StepAction implements ActionListener
	{
		Visualization visual;
		Visualization.Window window;

		StepAction(Visualization v, Visualization.Window w)
		{ 
			super();

			visual = v;
			window = w;
		}

		public void actionPerformed(ActionEvent e) 
		{
			visual.step();
			window.repaint();
		}
	}
}
