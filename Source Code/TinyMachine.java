/*
 * @Author: Tanner Smith
 * @Date: 11/15/2021
 */
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;

public class TinyMachine
{
	

	private int[] register; //Registers
	private ArrayList<Instruction> im; //Instruction Memory Space
	private int[] dm; //Data Memory Space

	private int instructionDelay; //Delay in ms in-between each instruction (1 second = 1000 ms)
	boolean halt;
	boolean isPaused;
	boolean stepMode;
	//boolean isRunning;
	boolean startProgram;


	private final int MAX_DATA_MEMORY_SIZE = 1024;
	private final int PC_REG = 7;
	private final int MAX_DELAY = 5; //In seconds

	private TinyMachineUI UI;

	public TinyMachine()
	{
		UI = new TinyMachineUI();
		isPaused = false;
		stepMode = false;
		//isRunning = false;
		startProgram = false;
		halt = true;
		instructionDelay = 0;
		resetAll();
		UI.updateInstructionTable(im);
		createListeners();
	}

	public void start()
	{
		/*
		 * Evil while loop >:). I did this because of some thread issues with the event dispatch thread making the call to run.
		 * So instead it just sets startProgram to true when you hit run instead of calling run so step mode still works.
		 * There is a better way to handle this with an entity that subscribes to some sort of trigger but this works and that's fine. 
		 */
		while(true) 
		{			
			//System.out.println("waiting" + startProgram);
			try
			{
				Thread.sleep(100); //Just slows down the looping so it doesn't refresh as fast as possible
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (startProgram)
			{
				runProgram();
				startProgram = false;
			}
			//System.out.println(im.size());
		}
	}

	public void runProgram() //Main simulation loop
	{
		//isRunning = true;
		halt = false;
		UI.dataMemoryTable.setValueAt(dm.length - 1, 0, 1);
		while(!halt && register[PC_REG] < im.size())
		{
			if (stepMode) //Set the pause to true so it will always get stuck in the loop at the bottom while in step mode
				isPaused = true;

			if (instructionDelay > 0) //only delay if the delay value is greater than 0
				try
			{
					Thread.sleep(instructionDelay);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			executeInstruction(im.get(register[PC_REG]));
			if (register[PC_REG] < im.size()) 
			{
				UI.updateNextInstructionTable(im.get(register[PC_REG]));
			}
			while(isPaused && !halt)
			{
				try
				{
					Thread.sleep(100); //Just slows down the loop so it doesn't refresh as fast as possible. 
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				////System.out.println("waiting for step");
				//freeze execution here until the nextStep or pause button is clicked. If in step mode isPaused will be set back to true at the top of the loop.
				//This in theory should allow you to step through.
			}
		}
		UI.output.append("HALTED\n");
		halt = true; //This is here in-case there wasn't a halt and the loop ended due to running out of instructions 
		//System.out.println("finished");
		//isRunning = false;
	}


	public void loadProgram(File f) 
	{
		////System.out.println(f.getAbsolutePath());
		resetAll();
		Program p = new Program(f);
		im = p.getInstructions();
		//resizeDataMemory(p.getMinReqDataMem());
		//clearDataMemory();
		UI.updateInstructionTable(im);
		UI.updateNextInstructionTable(im.get(0));
		//System.out.println(im.size());
	}

	public void resetAll()
	{
		clearRegisters();
		clearDataMemory();
		clearInstructions();
		UI.output.setText("");
	}

	public void clearDataMemory()
	{
		dm = new int[MAX_DATA_MEMORY_SIZE];
		for (int i = 0; i < UI.dataMemoryTable.getRowCount(); i++)
		{
			UI.dataMemoryTable.setValueAt(0, i, 1);
		}
		dm[0] = dm.length - 1;
		UI.dataMemoryTable.setValueAt(dm[0], 0, 1); //Visually update the UI of this change in the line above
	}

	public void clearRegisters()
	{
		register = new int[8];
		UI.updateRegisterUI(register);
	}

	public void clearInstructions()
	{
		im = new ArrayList<Instruction>();
		im.add(new Instruction(0, InstructionType.HALT, 0, 0, 0));
		UI.updateNextInstructionTable(im.get(0));
	}

	public void resizeDataMemory(int newSize)
	{
		if (newSize > MAX_DATA_MEMORY_SIZE) //Can not exceed the max allowed size
		{
			dm = new int[MAX_DATA_MEMORY_SIZE];
		}
		else if (newSize < 1) //In the event some dares to try this
		{
			dm = new int[1];
		}
		else
		{
			dm = new int[newSize];
		}
	}

	public void setInstructionDelay(float d) //d = delay in seconds
	{
		if (d > MAX_DELAY) //Maximum allowed delay value to protect the user from putting in something very long on accident.
		{
			d = MAX_DELAY * 1000;
		}
		else if (d >= 0) //No negative delay
		{
			instructionDelay = (int)(d * 1000); //Example: d = 0.5 then instruction delay would be 500 (i.e half of second)
		} else
		{
			instructionDelay = 0;
		}
	}

	public void executeInstruction(Instruction i)
	{
		if (checkIM_ERR())
		{
			register[PC_REG]++;
			InstructionType type = i.getType();
			int arg1 = i.getArg1();
			int arg2 = i.getArg2();
			int arg3 = i.getArg3();
			switch(type)
			{
			case HALT:
				HALT(arg1, arg2, arg3);
				break;
			case IN:
				IN(arg1, arg2, arg3);
				break;
			case OUT:
				OUT(arg1, arg2, arg3);
				break;
			case ADD:
				ADD(arg1, arg2, arg3);
				break;
			case SUB:
				SUB(arg1, arg2, arg3);
				break;
			case MUL:
				MUL(arg1, arg2, arg3);
				break;
			case DIV:
				DIV(arg1, arg2, arg3);
				break;
			case LD:
				LD(arg1, arg2, arg3);
				break;
			case LDA:
				LDA(arg1, arg2, arg3);
				break;
			case LDC:
				LDC(arg1, arg2, arg3);
				break;
			case ST:
				ST(arg1, arg2, arg3);
				break;
			case JLT:
				JLT(arg1, arg2, arg3);
				break;
			case JLE:
				JLE(arg1, arg2, arg3);
				break;
			case JGE:
				JGE(arg1, arg2, arg3);
				break;
			case JGT:
				JGT(arg1, arg2, arg3);
				break;
			case JEQ:
				JEQ(arg1, arg2, arg3);
				break;
			case JNE:
				JNE(arg1, arg2, arg3);
				break;
			default:
				break;
			}
			UI.updateRegisterUI(register);
		}

	}

	/*
	 * RO Instructions
	 * opcode r, s, t
	 */

	public void HALT(int r, int s, int t)
	{
		halt = true;
	}

	public void IN(int r, int s, int t)
	{
		if (checkREG_ERR(r, 0, 0))
		{
			String i = JOptionPane.showInputDialog("Input Required"); //I hope this pauses the threads execution 
			try
			{
				register[r] = Integer.parseInt(i); //Tiny Machine only takes integers 
			}
			catch(NumberFormatException | NullPointerException ex)
			{
				JOptionPane.showMessageDialog(UI.frame, "Invalid Input. Program Stopped");
				halt = true;
			}
		}
	}

	public void OUT(int r, int s , int t)
	{
		if(checkREG_ERR(r, 0, 0)) 
		{
			//System.out.println("Output: " + register[r]);
			UI.output.append(register[r] + "\n");
		}
	}

	public void ADD(int r, int s , int t)
	{
		if(checkREG_ERR(r, s, t))
			register[r] = register[s] + register[t];
	}

	public void SUB(int r, int s , int t)
	{
		if(checkREG_ERR(r, s, t))
			register[r] = register[s] - register[t];
	}

	public void MUL(int r, int s , int t)
	{
		if(checkREG_ERR(r, s, t))
			register[r] = register[s] * register[t];
	}

	public void DIV(int r, int s , int t)
	{
		if (checkZERO_DIV(register[t]) && checkREG_ERR(r, s, t))
			register[r] = register[s] / register[t];
	}

	/*
	 * RM Instructions
	 * opcode r, d(s)
	 */
	public void LD(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0 ,s))
		{
			int a = d + register[s];
			if(checkDMEM_ERR(a))
			{
				register[r] = dm[a];
			}
		}

	}

	public void LDA(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0 ,s))
		{
			int a = d + register[s];
			register[r] = a;
		}

	}

	public void LDC(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0 ,0))
			register[r] = d;
	}

	public void ST(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0 ,s))
		{
			int a = d + register[s];
			if(checkDMEM_ERR(a))
			{
				////System.out.println("Stored " + register[r] + " at data memory address: " + a);
				dm[a] = register[r];
				UI.dataMemoryTable.setValueAt(register[r], a, 1);
			}
		}

	}

	public void JLT(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0, s))
		{
			int a = d + register[s];
			if(register[r] < 0)
				register[PC_REG] = a;
		}


	}

	public void JLE(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0, s))
		{
			int a = d + register[s];
			if(register[r] <= 0)
				register[PC_REG] = a;
		}

	}

	public void JGE(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0, s))
		{
			int a = d + register[s];
			if(register[r] >= 0)
				register[PC_REG] = a;
		}
	}

	public void JGT(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0, s))
		{
			int a = d + register[s];
			if(register[r] > 0)
				register[PC_REG] = a;
		}
	}

	public void JEQ(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0, s))
		{
			int a = d + register[s];
			if(register[r] == 0)
				register[PC_REG] = a;
		}
	}

	public void JNE(int r, int d, int s)
	{
		if (checkREG_ERR(r, 0, s))
		{
			int a = d + register[s];
			if(register[r] != 0)
				register[PC_REG] = a;
		}
	}

	public boolean checkZERO_DIV(int a)
	{
		if (a == 0)
		{
			halt = true;
			UI.output.append("ZERO_DIV HALTING \n");
			return false;
		}
		return true;
	}

	public boolean checkREG_ERR(int r, int d, int s)
	{
		if (r < 0 || r > 7 || d < 0 || d > 7 || s < 0 || s > 7)
		{
			halt = true;
			UI.output.append("REG_ERR HALTING \n");
			return false;
		}
		return true;
	}

	public boolean checkIM_ERR()
	{
		if (register[PC_REG] < 0 || register[PC_REG] >= im.size())
		{
			halt = true;
			UI.output.append("IM_ERR HALTING \n");
			return false;
		}
		return true;
	}

	public boolean checkDMEM_ERR(int a)
	{
		if (a < 0 || a > dm.length - 1)
		{
			halt = true;
			UI.output.append("DMEM_ERR HALTING \n");
			return false;
		}
		return true;
	}

	public void createListeners() //Makes the buttons do things
	{

		UI.load.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) 
			{
				//System.out.println(halt);
				if (halt)
				{
					//loadProgram("");
					//loadProgram("C:/Users/Tanner/eclipse-workspace/TinyMachineEmulator/src/simple.tm");
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
					int result = fileChooser.showOpenDialog(fileChooser);
					if (result == JFileChooser.APPROVE_OPTION) {
					    File selectedFile = fileChooser.getSelectedFile();
					    if (selectedFile.getName().matches(".*\\.tm"))
					    {
					    	loadProgram(selectedFile);
						    ////System.out.println("Selected file: " + selectedFile.getName());
					    }
					    else
					    {
							JOptionPane.showMessageDialog(UI.frame, "Please select a .tm file" );

					    }
					}
				}
			}
		});

		UI.reset.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) 
			{
				clearDataMemory();
				clearRegisters();
				UI.output.setText("");
				UI.updateNextInstructionTable(im.get(0));
			}
		});

		UI.run.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) 
			{

				if (im.size() > 0 && halt)
				{
					startProgram = true;
					//System.out.println("started " + startProgram);
				}
			}
		});

		UI.setDelay.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) 
			{
				String s = JOptionPane.showInputDialog("Enter a delay between from 0 to " + MAX_DELAY + " seconds");
				try
				{
					setInstructionDelay(Float.parseFloat(s));
					JOptionPane.showMessageDialog(UI.frame, "Delay set to: " + (float)instructionDelay/1000f + " seconds" );
				}
				catch(NumberFormatException ex)
				{
					JOptionPane.showMessageDialog(UI.frame, "Invalid Input");
				}
				catch (NullPointerException ex)
				{
					//Do nothing, input was canceled 
				}
				////System.out.println(s);
			}
		});

		UI.stop.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if (!halt) //Check to see if it's running 
				{
					halt = true;
					UI.output.append("PROGRAM INTERRUPTED\n");
				}
			}
		});

		UI.toggleStep.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if (!stepMode)
				{
					isPaused = true;
					stepMode = true;
					UI.toggleStep.setBorderPainted(true);
					UI.toggleStep.setBorder(BorderFactory.createLineBorder(Color.GREEN)); //Visual feedback that it's on
				} else
				{
					isPaused = false;
					stepMode = false;
					UI.toggleStep.setBorderPainted(false);
					UI.toggleStep.setBackground(Color.WHITE);
				}
			}
		});

		UI.step.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) 
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) 
			{
				isPaused = false;
				//System.out.println("stepped " + isPaused);
			}
		});
	}


}
