package cs4432.project1.db;

import java.util.HashMap;
import java.util.Map;

public class Command {
    String type;
    Map<String, Object> command;

    public Command(String type) {
        this.type = type;
        this.command = new HashMap<>();
    }

    public String getType() {
        return type;
    }

    public void addK(int k) {
        this.command.put("k", k);
    }

    public int getK() {
        return (int) this.command.get("k");
    }

    public void addRecord(String record) {
        this.command.put("record", record);
    }

    public String getRecord() {
        return (String) this.command.get("record");
    }

    public void addBID(int BID) {
        this.command.put("BID", BID);
    }

    public int getBID() {
        return (int) this.command.get("BID");
    }

    @Override
    public String toString() {
        return "Command{" +
                "type='" + type + '\'' +
                ", command=" + command.toString() +
                '}';
    }
}
