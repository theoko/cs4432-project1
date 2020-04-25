Name: Theodoros Konstantopoulos
Student ID: 827449875

Section I:
- Import the project (src folder) in an IDE (such as Eclipse or IntelliJ)
- Copy the "Files" directory (dataset) in the same directory as the project (you can change the name of the table by modifying the constant DATASET_DIR in DiskFactory.java)
- Setup SDK (I developed this using version 12)
- Run the main method in the Main.java file and provide the pool size as the first argument

The program will not terminate unless an invalid command is given or it is stopped

Section II:
All of the test cases provided in the testcase_commands_and_output.txt file are working except for the very last one which is producing a different output.
If you execute the program, you will notice that in the last case (command: Pin 6), it is producing a slightly different output ("Output: File 6 pinned in Frame 2; Not already pinned; Evicted file 1 from frame 2") but the rest of the cases are producing the same output.

Section III:
According to the design guidelines there are two classes that represent the high-level design ideas and those are Frame and BufferPool classes.
In the Frame class, I added the queryForId attribute which represents the record ID of the queried record.
I added Command which holds the data of a command (type, hashmap of command properties) and Disk enum (a singleton) which is responsible of reading and writing blocks to disk.
Also, I created two exceptions, the IllegalCommandException and the InvalidCommandSyntaxException to handle the cases where a command is not valid or doesn't meet the syntax provided.
Under the utils package, I added CommandFactory which defines a valid command (GET, SET, PIN, UNPIN), CommandManager which parses the command input, CommandType which is an enum holding the command types,
DiskFactory which contains all the necessary constants for accessing files on disk, InputManager which just scans the input based on the next line character
and OutputManager which produces the output to the console when running each command.