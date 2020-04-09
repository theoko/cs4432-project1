package cs4432.project1.utils;

import java.util.ArrayList;
import java.util.List;

public class DiskFactory {
    public static final String DB_PATH = System.getProperty("user.dir");
    public static final String DATASET_DIR = "Files";
    public static final List<String> EXCLUDED_FILES = new ArrayList<>() {{
        // Exclude directories from being considered tables
        add("out");
        add(".idea");
        add("src");
    }};
}
