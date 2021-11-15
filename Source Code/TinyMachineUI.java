/*
 * @Author: Tanner Smith
 * @Date: 11/15/2021
 */
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class TinyMachineUI
{
	public JFrame frame;
	public JPanel mainPanel, leftPanel, centerPanel, rightPanel, registerPanel, nextInstructionPanel, outputPanel;
	public JLabel lreg0, lreg1, lreg2, lreg3, lreg4, lreg5, lreg6, lreg7;
	public JTextField reg0, reg1, reg2, reg3, reg4, reg5, reg6, reg7;
	public JTextArea output;
	public JTable dataMemoryTable, instructionMemoryTable, nexttInstructionTable;
	public JScrollPane dataScroller, instructionScroller, nextInstructionScroller;
	public JMenuBar menuBar;
	public JMenu load, reset, run, setDelay, stop, toggleStep, step;		
	
	public TinyMachineUI()
	{
		createUI();
		System.out.println(System.getProperty("user.dir"));
	}
		
	private void createUI()
	{
		frame = new JFrame("Tiny Machine Simulator");
		frame.setSize(1600,900);
		createMenuBar();
		createMainPanel();
		frame.add(mainPanel);
		frame.setJMenuBar(menuBar);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void createMainPanel()
	{
		GridLayout layout = new GridLayout(1, 3);
		mainPanel = new JPanel();
		mainPanel.setLayout(layout);
		createLeftPanel();
		createCenterPanel();
		createRightPanel();
		mainPanel.add(leftPanel);
		mainPanel.add(centerPanel);
		mainPanel.add(rightPanel);	
	}
	
	private void createRightPanel()
	{
		rightPanel = new JPanel();
		//rightPanel.setBackground(Color.GRAY);
		String [] col = {"Line", "Opcode", "arg1", "arg2", "arg3"};
		Object [][] data = new Integer[1][5];
		instructionMemoryTable = buildCustomTable(data, col);
		instructionScroller = new JScrollPane(instructionMemoryTable);
		instructionScroller.setPreferredSize(new Dimension(450, 800));
		rightPanel.add(instructionScroller);
	}
	
	private void createCenterPanel()
	{
		centerPanel = new JPanel();
		//centerPanel.setBackground(Color.GRAY);
		String [] col = {"Address", "Data"};
		Object [][] data = new Integer[1024][2];
		for (int i = 0; i < data.length; i++) //Starting data to fill the JTable
		{
			data[i][0] = new Integer(i);
			data[i][1] = new Integer(0);
		}
		dataMemoryTable = buildCustomTable(data, col);
		dataScroller = new JScrollPane(dataMemoryTable);
		dataScroller.setPreferredSize(new Dimension(450, 800));
		centerPanel.add(dataScroller);
	}
	
	private void createLeftPanel() //contains 3 sub panels
	{
		GridLayout layout = new GridLayout(3, 1);
		leftPanel = new JPanel();
		//leftPanel.setBackground(Color.GRAY);
		leftPanel.setLayout(layout);
		createRegisterPanel();
		createNextInstructionPannel();
		createOutputPanel();
		leftPanel.add(registerPanel);
		leftPanel.add(nextInstructionPanel);
		leftPanel.add(outputPanel);		
	}
	
	private void createOutputPanel()
	{
		outputPanel = new JPanel();
		GridLayout layout = new GridLayout(1, 2);
		outputPanel.setLayout(layout);
		
		output = new JTextArea();
		JScrollPane outputScroller = new JScrollPane(output);
		output.setEditable(false);
		JLabel lbl = new JLabel("Output:");
		lbl.setPreferredSize(new Dimension(300,300));
		lbl.setHorizontalAlignment(JLabel.CENTER);
		outputPanel.add(lbl);
		outputPanel.add(outputScroller);
	}
	
	private void createNextInstructionPannel()
	{
		nextInstructionPanel = new JPanel();
		GridLayout layout = new GridLayout(2, 1);
		nextInstructionPanel.setLayout(layout);
		
		String [] cols = {"Line", "Opcode", "arg1", "arg2", "arg3"};
		Object [][] data = {{"-", "-", "-", "-", "-"}};
		nexttInstructionTable = buildCustomTable(data, cols);
		nextInstructionScroller = new JScrollPane(nexttInstructionTable);
		JLabel lbl = new JLabel("Next Instruction");
		lbl.setHorizontalAlignment(JLabel.CENTER);
		nextInstructionPanel.add(lbl);
		nextInstructionPanel.add(nextInstructionScroller);
	}
	
	
	private void createRegisterPanel()
	{	
		GridLayout layout = new GridLayout(4, 4);
		registerPanel = new JPanel();
		registerPanel.setBackground(Color.LIGHT_GRAY);
		//registerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		registerPanel.setPreferredSize(new Dimension(400,300));
		registerPanel.setLayout(layout);
		lreg0 = new JLabel("r0: ");
		lreg1 = new JLabel("r1: ");
		lreg2 = new JLabel("r2: ");
		lreg3 = new JLabel("r3: ");
		lreg4 = new JLabel("r4: ");
		lreg5 = new JLabel("r5: ");
		lreg6 = new JLabel("r6: ");
		lreg7 = new JLabel("r7: ");
		
		lreg0.setHorizontalAlignment(JLabel.CENTER);
		lreg1.setHorizontalAlignment(JLabel.CENTER);
		lreg2.setHorizontalAlignment(JLabel.CENTER);
		lreg3.setHorizontalAlignment(JLabel.CENTER);
		lreg4.setHorizontalAlignment(JLabel.CENTER);
		lreg5.setHorizontalAlignment(JLabel.CENTER);
		lreg6.setHorizontalAlignment(JLabel.CENTER);
		lreg7.setHorizontalAlignment(JLabel.CENTER);
		
		reg0 = new JTextField("0");
		reg1 = new JTextField("0");
		reg2 = new JTextField("0");
		reg3 = new JTextField("0");
		reg4 = new JTextField("0");
		reg5 = new JTextField("0");
		reg6 = new JTextField("0");
		reg7 = new JTextField("0");
		
		Dimension regDim = new Dimension(40, 30);
		reg0.setPreferredSize(regDim);
		reg1.setPreferredSize(regDim);
		reg2.setPreferredSize(regDim);
		reg3.setPreferredSize(regDim);
		reg4.setPreferredSize(regDim);
		reg5.setPreferredSize(regDim);
		reg6.setPreferredSize(regDim);
		reg7.setPreferredSize(regDim);
		
		reg0.setHorizontalAlignment(JTextField.CENTER);
		reg1.setHorizontalAlignment(JTextField.CENTER);
		reg2.setHorizontalAlignment(JTextField.CENTER);
		reg3.setHorizontalAlignment(JTextField.CENTER);
		reg4.setHorizontalAlignment(JTextField.CENTER);
		reg5.setHorizontalAlignment(JTextField.CENTER);
		reg6.setHorizontalAlignment(JTextField.CENTER);
		reg7.setHorizontalAlignment(JTextField.CENTER);
		
		reg0.setEditable(false);
		reg1.setEditable(false);
		reg2.setEditable(false);
		reg3.setEditable(false);
		reg4.setEditable(false);
		reg5.setEditable(false);
		reg6.setEditable(false);
		reg7.setEditable(false);
		
		registerPanel.add(lreg0);
		registerPanel.add(reg0);
		registerPanel.add(lreg1);
		registerPanel.add(reg1);
		registerPanel.add(lreg2);
		registerPanel.add(reg2);
		registerPanel.add(lreg3);
		registerPanel.add(reg3);
		registerPanel.add(lreg4);
		registerPanel.add(reg4);
		registerPanel.add(lreg5);
		registerPanel.add(reg5);
		registerPanel.add(lreg6);
		registerPanel.add(reg6);
		registerPanel.add(lreg7);
		registerPanel.add(reg7);
	}
	
	private void createMenuBar()
	{
		menuBar = new JMenuBar();
		load = new JMenu("Load");
		reset = new JMenu("Reset");
		run = new JMenu("Run");
		setDelay = new JMenu("Set Delay");
		stop = new JMenu("Stop");
		toggleStep = new JMenu("Toggle Stepping");
		step = new JMenu("Step");
		menuBar.add(load);
		menuBar.add(reset);
		menuBar.add(run);
		menuBar.add(setDelay);
		menuBar.add(stop);
		menuBar.add(toggleStep);
		menuBar.add(step);
	}
	
	public JTable buildCustomTable(Object [][] rowData, Object [] colNames)
	{
		DefaultTableModel tableModel = new DefaultTableModel(rowData, colNames) 
		{

		    @Override
		    public boolean isCellEditable(int row, int column) 
		    {
		       //all cells false
		       return false;
		    }
		};
		JTable table = new JTable(tableModel);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER );
		for (int i = 0; i < table.getColumnCount(); i++)
		{
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		table.setFocusable(false);
		table.setRowSelectionAllowed(false);
		return table;
	}
	
	public void updateInstructionTable(ArrayList<Instruction> instructions)
	{
		String [] colNames = {"Line", "Opcode", "arg1", "arg2", "arg3"};
		Object [][] rowData = new Object[instructions.size()][5];
		for (int i = 0; i < instructions.size(); i++)
		{
			rowData[i][0] = i; //Line number
			rowData[i][1] = instructions.get(i).getType().toString(); //Opcode
			rowData[i][2] = instructions.get(i).getArg1(); //arg1
			rowData[i][3] = instructions.get(i).getArg2(); //arg2
			rowData[i][4] = instructions.get(i).getArg3(); //arg3
		}
		instructionMemoryTable = buildCustomTable(rowData, colNames);
		instructionScroller = new JScrollPane(instructionMemoryTable);
		instructionScroller.setPreferredSize(new Dimension(450, 800));
		rightPanel.removeAll(); //There is likely a better way to update the visual but this works so :)
		rightPanel.add(instructionScroller);
		//System.out.println("Table updated");
		frame.setVisible(true); //This is actually really dumb but this updated the visual to show the new table
		
	}
	
	public void updateNextInstructionTable(Instruction i)
	{
		nexttInstructionTable.setValueAt(i.getLineNumber(), 0, 0);
		nexttInstructionTable.setValueAt(i.getType().toString(), 0, 1);
		nexttInstructionTable.setValueAt(i.getArg1(), 0, 2);
		nexttInstructionTable.setValueAt(i.getArg2(), 0, 3);
		nexttInstructionTable.setValueAt(i.getArg3(), 0, 4);
	}
	
	public void updateRegisterUI(int [] r)
	{
		reg0.setText(r[0] + "");
		reg1.setText(r[1] + "");
		reg2.setText(r[2] + "");
		reg3.setText(r[3] + "");
		reg4.setText(r[4] + "");
		reg5.setText(r[5] + "");
		reg6.setText(r[6] + "");
		reg7.setText(r[7] + "");
		
	}
	 
	
	
}
