package cs4432.project1.db;

import cs4432.project1.exceptions.IllegalCommandException;
import cs4432.project1.utils.CommandFactory;
import cs4432.project1.utils.CommandType;
import cs4432.project1.utils.DiskFactory;
import cs4432.project1.utils.OutputManager;

import java.util.HashMap;
import java.util.Map;

public class BufferPool {
    private Frame[] buffers;

    public BufferPool(int size) {
        this.initialize(size);
    }

    private void initialize(int size) {
        buffers = new Frame[size];
        for (int i=0; i<buffers.length; i++) {
            buffers[i] = new Frame("", -1, -1);
        }
    }

    /**
     * This method calculated the boundaries (start, end) of a record
     * @param k
     * @return an integer array representing the boundaries (start, end) of a record
     */
    private int[] calcBoundaries(int k) {
        // We need to multiply k with the number of bytes to get to the start of the record
        // We also need to subtract fileNumber * 100 - 100 since we are getting a chunk of data, otherwise it would go up to 4K * 7 (total files)
        int number = k + 99;
        int fileNumber = 0;
        while(number > 0) {
            fileNumber = number % 10;
            number = number / 10;
        }
        k = k - (fileNumber * 100 - 100);
        int start = 40 * (k - 1);
        int end = start + 40; // the first 40 bytes
        return new int[] {
            start, end
        };
    }

    /**
     * This method extracts the record contents from a 4KB frame
     */
    private String extractRecordContent(Frame frame, int k) {
        int[] boundaries = this.calcBoundaries(k);
        int start = boundaries[0];
        int end = boundaries[1];
        return frame.getContent().substring(start, end);
    }

    /**
     * This method calculated the file number based on the provided integer k
     * @return an integer representing a file number
     */
    private int calcFileNumber(int k) {
        int number = k + 99;
        int fileNumber = -1;
        while(number > 0) {
            fileNumber = number % 10;
            number = number / 10;
        }
        return fileNumber;
    }

    /**
     * This method calculate which block (file) contains this record
     */
    private int get(int k) {
        // Calculate file number
        int fileNumber = this.calcFileNumber(k);
        // Check buffer pool
        int search = this.search(fileNumber);
        if (search != -1) {
            int[] boundaries = calcBoundaries(k);
            String content = buffers[search].getContent().substring(boundaries[0], boundaries[1]);
            String action = "File " + fileNumber + " already in memory";
            String placement = "Located in Frame " + (search + 1);
            OutputManager.writeGetContent(content, action, placement);
            return search;
        }

        this.addToPool(k, fileNumber, CommandType.GET_COMMAND);
//        int emptyIndex = getEmpty();
//        if (emptyIndex != -1 && content != null) {
//            // Initialize frame using content, record id and block id (file number)
//            buffers[emptyIndex] = new Frame(content, k, fileNumber);
//            // Extract the record content from the frame
//            String recordContent = this.extractRecordContent(buffers[emptyIndex], k);
//            // Output message
//            String action = "Brought file " +  fileNumber + " from disk";
//            String placement = "Placed in frame " + buffers[emptyIndex].getBlockId();
//            OutputManager.writeRecordContent(recordContent, action, placement);
//            return emptyIndex;
//        }
//        // The block is not in memory, the buffer pool array is full (no empty frames),
//        // but some frames can be taken out
//        this.makeSpace(fileNumber);

        return -1; // stub
    }

    /**
     * This method sets the content of record #k to the given string
     * @param k
     * @param newRecordContent
     */
    private void set(int k, String newRecordContent) {
        // Calculate file number
        int fileNumber = calcFileNumber(k);
        // Calculate boundaries
        int[] boundaries = calcBoundaries(k);
        // First we need to search for the frame containing the record we are looking for
        int search = this.search(fileNumber);
        if (search != -1) {
            // Go ahead and set the content of the frame to newRecordContent
            String old = buffers[search].getContent().substring(boundaries[0], boundaries[1]);
            buffers[search].setContent(buffers[search].getContent().replace(old, newRecordContent));

            // buffers[search].getContent().substring(boundaries[0], boundaries[1])
            OutputManager.writeSetContent("Write was successful; File " + fileNumber + " already in memory; " + "Located in Frame " + (search + 1));
            return;
        }
        // If we don't find the record in memory, we can go ahead and bring it from disk
        String content;
        // We need to get the first empty frame to place the 4KB content
        int emptyIndex = getEmpty();
        if (emptyIndex != -1) {
            content = Disk.INSTANCE.readTable(DiskFactory.DATASET_DIR, k);
            assert content != null;
            // Place content in empty frame
            buffers[emptyIndex] = new Frame(content, fileNumber, emptyIndex);
            // Set record content to new content
            // Get old content first because we need to replace it with the new one
            String old = buffers[emptyIndex].getContent().substring(boundaries[0], boundaries[1]);
            buffers[emptyIndex].setContent(buffers[emptyIndex].getContent().replace(old, newRecordContent));

            // buffers[emptyIndex].getContent().substring(boundaries[0], boundaries[1]);
            OutputManager.writeSetContent("Write was successful; Brought File " + fileNumber + " from disk; " + "Placed in Frame " + (emptyIndex + 1));
            return;
        }
        // If we are not successful, we need to free up one spot
        Map<String, Integer> map = this.makeSpace();
        if (map.size() > 0) {
            content = Disk.INSTANCE.readTable(DiskFactory.DATASET_DIR, k);
            assert content != null;
            // Place content in free frame
            buffers[map.get("freeIndex")] = new Frame(content, fileNumber, map.get("freeIndex"));
            // Set record content to new content
            // Get old content first because we need to replace it with the new one
            String old = buffers[map.get("freeIndex")].getContent().substring(boundaries[0], boundaries[1]);
            buffers[map.get("freeIndex")].setContent(buffers[map.get("freeIndex")].getContent().replace(old, newRecordContent));

            // OutputManager.writeSetContent(buffers[map.get("freeIndex")].getContent().substring(boundaries[0], boundaries[1]));
            OutputManager.writeSetContent("Write was successful; Brought File " + fileNumber + " from disk; " + "Placed in Frame " + (map.get("freeIndex") + 1) + "; Evicted file " + map.get("prevBlockId") + " from Frame " + (map.get("freeIndex") + 1));
            return;
        }
        // If we are still not successful, we cannot move forward
        OutputManager.writeSetContent("Output: The corresponding block #" + fileNumber + " cannot be accessed from disk because the memory buffers are full; Write was unsuccessful");
    }

    /**
     * This method searches if the frame (block) provided is available in the buffer pool.
     * @param blockId
     */
    private int search(int blockId) {
        for (int i=0; i<buffers.length; i++) {
            if (buffers[i].getBlockId() == blockId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method returns the content of a given block Id
     * @param blockId
     * @return a string representing the content of the block
     */
    private String getContent(int blockId) {
        // Call the search method to know the buffer number
        int search = this.search(blockId);
        if (search != -1) {
            // Read the content
            return buffers[search].getContent();
        }
        // Since we did not find the block in memory, return null
        return null;
    }

    /**
     * This method checks if the needed blockId is not in the buffer pool and if it's not it brings it to the buffer pool (in an empty frame)
     */
    private void checkAndBringBlockID(int queryForId, int blockId) {
        // Call the search method to know the buffer number
        int search = this.search(blockId);
        if (search == -1) {
            // We need to bring the block to the pool since it was not found
            String content = Disk.INSTANCE.readBlock(DiskFactory.DATASET_DIR, blockId);
            // this.addToPool(queryForId, blockId);
        }
    }

    private int addToPool(int queryForId, int blockId, CommandType commandType) {
        // Calculate file number
        int fileNumber = calcFileNumber(queryForId);
        // Calculate boundaries for record
        int[] boundaries = calcBoundaries(queryForId);
        // We assume that the block was searched for before calling this method
        // First, we need try and get an empty spot
        int emptyIndex = getEmpty();
        if (emptyIndex != -1) {
            // If block is not in memory, but there are empty buffers in the buffer pool array:
            // Read the right file from disk and copy its content to an empty buffer frame
            String content = Disk.INSTANCE.readTable(DiskFactory.DATASET_DIR, queryForId);
            // Initialize frame using content, record id and block id (file number)
            buffers[emptyIndex] = new Frame(content, fileNumber, emptyIndex);
            // Output message
            String action = "Brought file " +  blockId + " from disk";
            String placement = "Placed in Frame " + (emptyIndex + 1);
            assert content != null;
            String output = content.substring(boundaries[0], boundaries[1]);
            OutputManager.writeGetContent(output, action, placement);
            return emptyIndex;
        }
        // The block is not in memory, the buffer pool array is full (no empty frames),
        // but some frames can be taken out
        Map<String, Integer> map = this.makeSpace();
        if (map.size() > 0) {
            // If block is not in memory, but we just created a free spot in the pool array:
            // Read the right file from disk and copy its content to an empty buffer frame
            String content = Disk.INSTANCE.readTable(DiskFactory.DATASET_DIR, queryForId);
            // Initialize frame using content, record id and block id (file number)
            buffers[map.get("freeIndex")] = new Frame(content, blockId, map.get("freeIndex"));
            // Output message
            String action = "Brought file " +  blockId + " from disk";
            String placement = "Placed in Frame " + (map.get("freeIndex") + 1);
            String eviction = "Evicted file " + map.get("prevBlockId") + " from frame " + (map.get("freeIndex") + 1);
            assert content != null;
            String output = content.substring(boundaries[0], boundaries[1]);
            OutputManager.writeGetContent(output, action, placement, eviction);
            return map.get("freeIndex");
        }
        // The block is not in memory, the buffer pool array is full (no empty frames),
        // and no blocks can be taken out which means that all the frames have content and are pinned
        // so we cannot take out any frame so we just need to print a message
        OutputManager.cannotTakeOutFrame(blockId);
        return -1;
    }

    /**
     * This method searches and gives us back the number in the array for an empty frame (if any):
     * - We locate an empty frame by searching all slots in the array and checking the "blockId" value
     * - If the "blockId" value is -1, then this frame is empty and can be used
     */
    private int getEmpty() {
        for (int i=0; i<buffers.length; i++) {
            if (buffers[i].getBlockId() == -1) {
                return i;
            }
        }
        return -1; // If we don't find any empty frame
    }

    /**
     * This method should be used if there is no available space in the buffer pool as it takes out one frame and returns it back to disk (if possible) based on the placement policy:
     * - We select the first empty frame that we find starting from the top of the buffer pool
     * @return
     */
    private Map<String, Integer> makeSpace() {
        // We need to first make sure that the frame's pinned flag is set to false
        for (int i=0; i<buffers.length; i++) {
            if (!buffers[i].getPinned()) {
                // There are two cases:
                // - The dirty flag is false which means that it can be taken out without the need to write back to disk
                // - The dirty flag is true so we have to write back by overwriting the file, otherwise the changes will be lost
                Map<String, Integer> values = new HashMap<>();
                if (!buffers[i].getDirty()) {
                    values.put("prevBlockId", buffers[i].getBlockId());
                    buffers[i] = new Frame("", -1, -1);
                } else {
                    values.put("prevBlockId", buffers[i].getBlockId());
                    Disk.INSTANCE.writeBlock(DiskFactory.DATASET_DIR, buffers[i].getBlockId(), buffers[i].getContent());
                    buffers[i] = new Frame("", -1, -1);
                }
                values.put("freeIndex", i);
                return values;
            }
        }
        return new HashMap<>();
    }

    /**
     * The pin command pins the blockId (file number) in memory. There are a few cases:
     * - Block is already in the buffer pool, set the pinned flag to true if it is not already set
     * - Block is not in memory but the buffer pool has available space or we can take out a block, bring the block to memory and set pinned flag to true
     * - Block is not in memory and buffer pool is full and no blocks can be taken out, print message "The corresponding block BID cannot be pinned because the memory buffers are full"
     * @param bid
     */
    private void pin(int bid) {
        // Search for the block in the pool
        int search = this.search(bid);
        if (search != -1) {
            if (buffers[search].getPinned()) {
                OutputManager.writePin(bid, search + 1, true, false);
                return;
            }
            buffers[search].setPinned(true);
            OutputManager.writePin(bid, search + 1, false, false);
            return;
        }
        String content;
        // Block is not in memory so try to add it
        int emptyIndex = getEmpty();
        if (emptyIndex != -1) {
            content = Disk.INSTANCE.readBlock(DiskFactory.DATASET_DIR, bid);
            assert content != null;
            buffers[emptyIndex] = new Frame(content, bid, emptyIndex);
            buffers[emptyIndex].setPinned(true);
            OutputManager.writePin(bid, emptyIndex + 1, false, false);
            return;
        }
        Map<String, Integer> map = this.makeSpace();
        if (map.size() > 0) {
            content = Disk.INSTANCE.readBlock(DiskFactory.DATASET_DIR, bid);
            assert content != null;
            buffers[map.get("freeIndex")] = new Frame(content, bid, map.get("freeIndex"));
            buffers[map.get("freeIndex")].setPinned(true);
            OutputManager.writePin(bid, (map.get("freeIndex") + 1), map.get("prevBlockId"));
            return;
        }

        OutputManager.writePin(bid, -1, false, true);
    }

    /**
     * The unpin command unpins the blockId (file number) in memory. There are a few cases:
     * - Block is in memory, set the pinned flag to false
     * - Block is not in memory, print message "The corresponding block <BID> cannot be unpinned because it is not in memory"
     * @param bid
     */
    private void unpin(int bid) {
        int search = this.search(bid);
        if (search != -1) {
            if (!buffers[search].getPinned()) {
                OutputManager.writeUnpin(bid, search + 1,true, true);
                return;
            }
            // The block is in memory, set pinned to false
            buffers[search].setPinned(false);
            OutputManager.writeUnpin(bid, search + 1, true, false);
            return;
        }
        // The block is not in memory, print message
        OutputManager.writeUnpin(bid, -1, false, false);
    }

    /**
     * This method executes the command specified
     * @param command
     */
    public void run(Command command) throws IllegalCommandException {
        switch (command.getType()) {
            case CommandFactory.GET_COMMAND:
                int k = command.getK();
                int blockId = this.get(k);
                break;
            case CommandFactory.SET_COMMAND:
                k = command.getK();
                String newRecordContent = command.getRecord();

                // System.out.println(k);
                // System.out.println(newRecordContent);
                this.set(k, newRecordContent);
                break;
            case CommandFactory.PIN_COMMAND:
                int BID = command.getBID();
                this.pin(BID);
                break;
            case CommandFactory.UNPIN_COMMAND:
                BID = command.getBID();
                this.unpin(BID);
                break;
            default:
                throw new IllegalCommandException(command.type);
        }
    }
}
