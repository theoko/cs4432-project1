package cs4432.project1;

import cs4432.project1.db.BufferPool;
import cs4432.project1.db.Command;
import cs4432.project1.exceptions.IllegalCommandException;
import cs4432.project1.utils.CommandManager;
import cs4432.project1.utils.InputManager;
import cs4432.project1.utils.OutputManager;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: Main.java bufferPoolSize");
            System.exit(-1);
        }

        // Get buffer pool size
        int bufferPoolSize = Integer.parseInt(args[0]);
        // Initialize buffer pool
        BufferPool bufferPool = new BufferPool(bufferPoolSize);
        // Print message
        OutputManager.programReady();
        while(true) {
            // Scan command
            String input = InputManager.scanCommand();
            CommandManager commandManager = new CommandManager(input);
            Command command = commandManager.getCommandMap();
            // Print command
            // System.out.println(command.toString());
            try {
                bufferPool.run(command);
            } catch (IllegalCommandException e) {
                e.printStackTrace();
            }
        }

    }
}
