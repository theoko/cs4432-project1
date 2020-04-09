package cs4432.project1.exceptions;

public class IllegalCommandException extends Exception {
    public IllegalCommandException(String command) {
        System.err.println("Invalid command: " + command);
        System.out.println("Exiting...");
        System.exit(-1);
    }
}
