/*
 * @Author: Tanner Smith
 * @Date: 11/15/2021
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Program
{

	private ArrayList<Instruction> instructions;
	private int maxReqDataMem;

	//Regular expressions
	private final String roInstruction = "[0-9]+:(HALT|IN|OUT|ADD|SUB|MUL|DIV)[0-7],[0-7],[0-7].*";
	private final String rmInstruction = "[0-9]+:(LD|LDA|LDC|ST|JLT|JLE|JGE|JGT|JEQ|JNE)[0-7],-?[0-9]+\\([0-7]\\).*";


	public Program(File f)
	{
		instructions = generateInstructionsFromFile(f);
	}

	public ArrayList<Instruction>  getInstructions() 
	{
		return instructions;
	}

	public int getNumberOfInstructions()
	{
		return instructions.size();
	}

	public int getMinReqDataMem()
	{
		return maxReqDataMem;
	}
	private ArrayList<Instruction> generateInstructionsFromFile(File f)
	{
		ArrayList<Instruction> a = new ArrayList<Instruction>();
		//File file = new File("filename.tm"); //replace later
		maxReqDataMem = 0;
		try
		{
			//System.out.println("reading from file: " + f.getPath());
			Scanner scanner = new Scanner(f);
			String currentLine;
			Instruction i;
			while (scanner.hasNextLine()) //Loop through each line of the file
			{
				currentLine = scanner.nextLine().trim();
				currentLine = currentLine.replaceAll("\\s+", "");
				//System.out.println(currentLine);
				if (currentLine.matches("\\*.*") || currentLine.matches("\\s*\\n"))
				{
					//System.out.println("comment");
					//Matched a comment line ignore
				} else if (currentLine.matches(roInstruction))
				{
					//Process roInstruction
					//System.out.println("Ro Instruct");
					i = processRoInstruction(currentLine);
					a.add(i);
					if (i.getType() == InstructionType.ST)
						maxReqDataMem++;
					//System.out.println(currentLine);
				} else if(currentLine.matches(rmInstruction))
				{
					//Process rmInstruction
					//System.out.println("Rm Instruct");
					i = processRmInstruction(currentLine);
					a.add(i);
					if (i.getType() == InstructionType.ST)
						maxReqDataMem++;
					//System.out.println(currentLine);
				} else
				{
					//Neither a comment or proper instruction
					//Choosing to ignore for now
				}
			}
			scanner.close(); //Done reading from the file
		} catch (FileNotFoundException e)
		{
			System.out.println("File not found");
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if (maxReqDataMem == 0) //Just in case there was no ST we don't want 0 data memory
			maxReqDataMem++;
		//System.out.println("At most " + maxReqDataMem + " data memory needed");
		if (a.size() == 0) //Fail-safe if there are no valid instructions read from file
		{
			a.add(new Instruction(0, InstructionType.HALT, 0, 0, 0));
		}
		Collections.sort(a, new Comparator<Instruction>() //This will sort the array by line number read allowing for back-filled instructions to run fine
		{
			@Override
			public int compare(Instruction instruct1, Instruction instruct2)
			{
				/*
				 * The lower the number returned the lower instruct1 will be sorted in the list
				 * so for example line 0 - line 1 will be -1 
				 *	This should sort it by lowest # instruction to highest
				 */
				return instruct1.getLineNumber() - instruct2.getLineNumber(); 

			}
		});
		return a;
	}

	private Instruction processRoInstruction(String s)
	{
		//Declarations for our return value
		Instruction i;
		InstructionType type;
		int arg1;
		int arg2;
		int arg3;
		int lineNumber;

		//Gets the line number and then removes that part from the input string
		lineNumber = Integer.parseInt(s.substring(0, s.indexOf(":")));
		s = s.substring(s.indexOf(":") + 1);

		//Will loop through the string and eat it up until it gets to a digit. This should get the opcode
		String typeString = "";
		char c = s.charAt(0);
		while (!Character.isDigit(c))
		{
			typeString += c;
			s = s.substring(1); //eat the first letter of the string
			c = s.charAt(0);
		}
		type = InstructionType.valueOf(typeString); //Matches the literal opcode string to our enum

		//because our regular expression already matched the format of the string we can extract the rest based of knowing indexes and it's format
		arg1 = Integer.parseInt(s.substring(0, s.indexOf(',')));
		s = s.substring(s.indexOf(',') + 1);
		arg2 = Integer.parseInt(s.substring(0, s.indexOf(',')));
		s = s.substring(s.indexOf(',') + 1);
		arg3 = Integer.parseInt(s.substring(0, 1));

		i = new Instruction(lineNumber, type, arg1, arg2, arg3); //makes the parsed instruction
		return i;
	}

	private Instruction processRmInstruction(String s)
	{
		//Declarations for our return value
		Instruction i;
		InstructionType type;
		int arg1;
		int arg2;
		int arg3;
		int lineNumber;

		//Gets the line number and then removes that part from the input string
		lineNumber = Integer.parseInt(s.substring(0, s.indexOf(":")));
		s = s.substring(s.indexOf(":") + 1);

		//Will loop through the string and eat it up until it gets to a digit. This should get the opcode
		String typeString = "";
		char c = s.charAt(0);
		while (!Character.isDigit(c))
		{
			typeString += c;
			s = s.substring(1); //eat the first letter of the string
			c = s.charAt(0);
		}
		type = InstructionType.valueOf(typeString); //Matches the literal opcode string to our enum

		//because our regular expression already matched the format of the string we can extract the rest based of knowing indexes and it's format

		arg1 = Integer.parseInt(s.substring(0, s.indexOf(',')));
		s = s.substring(s.indexOf(',') + 1);
		arg2 = Integer.parseInt(s.substring(0, s.indexOf('(')));
		s = s.substring(s.indexOf('('));
		arg3 = Integer.parseInt(s.substring(1, 2)); // S should be ([0-7]) followed by anything else

		i = new Instruction(lineNumber, type, arg1, arg2, arg3); //makes the parsed instruction
		return i;
	}


}
