package cs4432.project1.exceptions;

public class InvalidCommandSyntaxException extends Exception {
    public InvalidCommandSyntaxException(String msg) {
        System.err.println(msg);
        System.exit(-1);
    }
}
