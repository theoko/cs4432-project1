package cs4432.project1.db;

import cs4432.project1.utils.DiskFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Disk {
    INSTANCE;

    /**
     * This strucure maps a table to a list of files
     */
    Map<String, List<String>> tables = new HashMap<>();

    /**
     * Is called by class loader so before any other method
     */
    Disk() {
        this.initialize();
    }

    /**
     * This method initializes our enum by calling discoverTables
     */
    private void initialize() {
        this.discoverTables();
    }

    private void discoverTables() {
        File tablePath = new File(DiskFactory.DB_PATH);
        File[] tables = tablePath.listFiles();
        for (int i=0; i<tables.length; i++) {
            if (tables[i].isDirectory()) {
                String name = tables[i].getName();
                if (!DiskFactory.EXCLUDED_FILES.contains(name)) {
                    this.discoverDatablocks(name);
                }
            }
        }
    }

    private void discoverDatablocks(String path) {
        File dataset = new File(DiskFactory.DB_PATH + File.separator + path);
        File[] datablocks = dataset.listFiles();
        List<String> datablocksList = new ArrayList<>();
        for (int i=0; i<datablocks.length; i++) {
            if (datablocks[i].isFile()) {
                String name = datablocks[i].getName();
                datablocksList.add(name);
            }
        }
        this.tables.put(path, datablocksList);
    }

    public Map<String, List<String>> getTables() {
        return tables;
    }

    public String readTable(String tableName, int k) {
        // Get the file number
        // We use 2 digits for file number and 3 digits for record number
        // Rec 1-100: F1
        // Rec 101-200: F2
        // Rec 201-300: F3
        // Rec 301-400: F4
        // ...
        int number = k + 99;
        int fileNumber = 0;
        while(number > 0) {
            fileNumber = number % 10;
            number = number / 10;
        }
        // System.out.println("File: " + String.format("%02d", fileNumber));
        List<String> dataBlocks = this.tables.get(tableName);
        // System.out.println("Reading: " + tableName);
        String fileName = "F" + fileNumber + ".txt";
        if (dataBlocks.contains(fileName)) {
            // System.out.println("File " + fileName + " is an existing datablock!");
            File block = new File(tableName + File.separator + fileName);
            try (BufferedReader br = new BufferedReader(new FileReader(block))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // The line represents one Frame
                    return line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // In case we haven't returned anything
        return null;
    }

    public String readBlock(String tableName, int blockId) {
        String fileName = "F" + blockId + ".txt";
        List<String> dataBlocks = this.tables.get(tableName);
        if (dataBlocks.contains(fileName)) {
            File block = new File(tableName + File.separator + fileName);
            try (BufferedReader br = new BufferedReader(new FileReader(block))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // The line represents one Frame
                    return line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // In case we haven't returned anything
        return null;
    }

    public void writeBlock(String tableName, int blockId, String newContent) {
        String fileName = "F" + blockId + ".txt";
        List<String> dataBlocks = this.tables.get(tableName);
        if (dataBlocks.contains(fileName)) {
            File block = new File(tableName + File.separator + fileName);
            try {
                FileWriter blockWriter = new FileWriter(block, false); // false to overwrite
                blockWriter.write(newContent);
                blockWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "Disk{" +
                "tables=" + tables.toString() +
                '}';
    }

    public static void main(String[] args) {
//        Disk disk = new Disk();
//        System.out.println(disk.toString());
//        disk.readTable("Files");
    }
}
