Welcome to TinyMachineSimulator.jar


---------------Loading files----------------------------------------------------
Click load and select a .tm file to load it into the instruction memory
The simulator will load the instructions in order of their line number 
regardless of the order they are in the file. Any line that doesn't
start as a proper format tm instruction will be ignored. This will still
read tm proper formatted tm instructions with text after it. The text is 
treated as a comment.

----------------Reset------------------------------------------------------------
Reset will clear the output, registers, and data memory.
It does not clear the instruction memory. This is so you can reset and run
your program 


--------------Run-----------------------------------------------------------------
Run will make the simulator run what ever is in the instruction memory and the current
program counter. If a program is running and there is no halt instruction to stop it 
then the simulator will stop execution if the program counter is larger than the size of 
current instruction memory loaded.

--------------SetDelay------------------------------------------------------------
Click the Set Delay button and you will be prompted to set a delay between
0 and 5 seconds. This accepts floats so you can set it to 0.5 seconds if 
you wanted to. If you have a delay set when you go to run the program
the simulator will wait the delay you set in seconds between each
instruction execution.

-------------Stop------------------------------------------------------------------
This button forces a halt and stops the simulator from running after it 
finishes the instruction it is currently on.

-------------Toggle Stepping/Step--------------------------------------------------
Clicking Toggle Stepping will put the simulator into step mode. You can 
tell if step mode is on if the button is boxed by a green border.
While in step mode the program will not execute instructions unless
you click the step button. You must click the run program first to
do the first step and start the program. Then you can click the step
button to step through each instruction