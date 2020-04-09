package cs4432.project1.utils;

public class OutputManager {
    public static void programReady() {
        System.out.println("The program is ready for your next command");
        line();
    }

    public static void writeGetContent(String content, String action, String placement) {
        // Writes the content of a 40 byte record
        System.out.println("Output: " + content + "; " + action + "; " + placement);
        line();
    }

    public static void writeGetContent(String content, String action, String placement, String eviction) {
        // Writes the content of a 40 byte record and the file that was evicted
        System.out.println("Output: " + content + "; " + action + "; " + placement + "; " + eviction);
        line();
    }

    public static void writeSetContent(String content) {
        System.out.println(content);
        line();
    }

    public static void writePin(int bid, int fid, boolean alreadyPinned, boolean unableToMakeSpace) {
        if (unableToMakeSpace) {
            System.out.println("Output: The corresponding block " + bid + " cannot be pinned because the memory buffers are full");
            line();
            return;
        }

        if (alreadyPinned) {
            System.out.println("Output: File " + bid + " pinned in Frame " + fid + "; Already pinned");
        } else {
            System.out.println("Output: File " + bid + " is pinned in Frame " + fid + "; Not already pinned");
        }
        line();
    }

    public static void writePin(int bid, int fid, int evictedFid) {
        System.out.println("Output: File " + bid + " is pinned in Frame " + fid + "; Not already pinned; " + "Evicted file " + evictedFid + " from Frame " + fid);
        line();
    }

    public static void writeUnpin(int bid, int fid, boolean inMemory, boolean alreadyUnpinned) {
        if (!inMemory) {
            System.out.println("Output: The corresponding block " + bid + " cannot be unpinned because it is not in memory.");
            line();
            return;
        }
        if (alreadyUnpinned) {
            System.out.println("Output: File " + bid + " in frame " + fid + " is unpinned; Frame " + fid + " was already unpinned");
        } else {
            System.out.println("Output: File " + bid + " is unpinned in frame " + fid + "; Frame " + fid + " was not already unpinned");
        }

        line();
    }

    public static void cannotTakeOutFrame(int blockId) {
        System.out.println("The corresponding block #" + blockId + " cannot be accessed from disk because the memory buffers are full");
        line();
    }

    public static void line() {
        System.out.println();
    }
}
