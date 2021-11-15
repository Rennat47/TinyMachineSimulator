/*
 * @Author: Tanner Smith
 * @Date: 11/15/2021
 */
public class Instruction
{
	
	private InstructionType type;
	private int line, arg1, arg2, arg3;

	public Instruction(int l, InstructionType t, int a1, int a2, int a3)
	{
		line = l;
		type = t;
		arg1 = a1;
		arg2 = a2;
		arg3 = a3;
	}
	
	public int getLineNumber()
	{
		return line;
	}
	
	public InstructionType getType()
	{
		return type;
	}
	
	public int getArg1()
	{
		return arg1;
	}
	
	public int getArg2()
	{
		return arg2;
	}
	
	public int getArg3()
	{
		return arg3;
	}

}
