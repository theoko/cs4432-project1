package cs4432.project1.utils;

import cs4432.project1.db.Command;
import cs4432.project1.exceptions.IllegalCommandException;
import cs4432.project1.exceptions.InvalidCommandSyntaxException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    String rawInput;
    String parsedCommand;
    List<String> inputs;
    Command commandMap;

    public CommandManager(String rawInput) {
        this.rawInput = rawInput;
        try {
            this.parseCommand();
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }
    }

    public String getRawInput() {
        return rawInput;
    }

    public String getParsedCommand() {
        return parsedCommand;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public Command getCommandMap() {
        return commandMap;
    }

    /**
     * This method parses the first word of the input to determine the command for the system execute
     */
    private void parseCommand() throws IllegalCommandException {
        this.inputs = Arrays.asList(this.rawInput.split(" "));
        this.parsedCommand = this.inputs.get(0).toUpperCase();
        switch (this.parsedCommand) {
            case CommandFactory.GET_COMMAND:
                int k = 0;
                try {
                    k = this.getCommand();
                    this.commandMap = new Command(CommandFactory.GET_COMMAND);
                    this.commandMap.addK(k);
                } catch (InvalidCommandSyntaxException e) {
                    e.printStackTrace();
                }
                break;
            case CommandFactory.SET_COMMAND:
                Map<String, Object> inputMap = null;
                try {
                    inputMap = this.setCommand();
                    this.commandMap = new Command(CommandFactory.SET_COMMAND);
                    this.commandMap.addK((int) inputMap.get("k"));
                    this.commandMap.addRecord((String) inputMap.get("record"));
                } catch (InvalidCommandSyntaxException e) {
                    e.printStackTrace();
                }
                break;
            case CommandFactory.PIN_COMMAND:
                try {
                    int pinBID = this.pinCommand();
                    this.commandMap = new Command(CommandFactory.PIN_COMMAND);
                    this.commandMap.addBID(pinBID);
                } catch (InvalidCommandSyntaxException e) {
                    e.printStackTrace();
                }
                break;
            case CommandFactory.UNPIN_COMMAND:
                try {
                    int unpinBID = this.unpinCommand();
                    this.commandMap = new Command(CommandFactory.UNPIN_COMMAND);
                    this.commandMap.addBID(unpinBID);
                } catch (InvalidCommandSyntaxException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalCommandException(this.parsedCommand);
        }
    }

    /**
     * This method parses the get command to get the integer k
     * @return parsed integer k which refers to a record with id k
     */
    public int getCommand() throws InvalidCommandSyntaxException {
        try {
            return Integer.parseInt(this.inputs.get(1));
        } catch (NumberFormatException e) {
            throw new InvalidCommandSyntaxException("Invalid k given for " + this.parsedCommand + " command.");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidCommandSyntaxException("Please provide k.");
        }
    }

    /**
     * This method parses the set command and constructs a Map
     * @return a Map that contains the arguments of the set command, an integer k and a string of size 40 bytes
     */
    public Map<String, Object> setCommand() throws InvalidCommandSyntaxException {
        int k = Integer.parseInt(this.inputs.get(1));
        StringBuilder rawRecord = new StringBuilder();
        for (int i=2; i<this.inputs.size(); i++) {
             rawRecord.append(this.inputs.get(i)).append(" ");
        }
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("k", k);
        int recordLength = rawRecord.toString().length();
        try {
            String record = rawRecord.toString().substring(1, recordLength - 2);
            inputMap.put("record", record);
            return inputMap;
        } catch (StringIndexOutOfBoundsException e) {
            throw new InvalidCommandSyntaxException("Invalid " + this.parsedCommand + " command.");
        }
    }

    /**
     * This method parses the pin command and gets the block id to be pinned in memory
     * @return an integer which is the block id of the block to be pinned
     */
    public int pinCommand() throws InvalidCommandSyntaxException {
        try {
            return Integer.parseInt(this.inputs.get(1));
        } catch (NumberFormatException e) {
            throw new InvalidCommandSyntaxException("Invalid BID given for " + this.parsedCommand + " command.");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidCommandSyntaxException("Please provide BID.");
        }
    }

    /**
     * This method parses the unpin command and gets the block id to be unpinned in memory
     * @return an integer which is the block id of the block to be unpinned
     */
    public int unpinCommand() throws InvalidCommandSyntaxException {
        try {
            return Integer.parseInt(this.inputs.get(1));
        } catch (NumberFormatException e) {
            throw new InvalidCommandSyntaxException("Invalid BID given for " + this.parsedCommand + " command.");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidCommandSyntaxException("Please provide BID.");
        }
    }
}
