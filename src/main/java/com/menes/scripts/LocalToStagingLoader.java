package com.menes.scripts;

import com.menes.scripts.db.CsvLineReader;

public class LocalToStagingLoader {
    public static void main(String[] args) {
        CsvLineReader.run();
    }
}
