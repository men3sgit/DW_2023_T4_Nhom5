package com.menes.scripts;

import com.menes.scripts.db.CsvLineReader;

import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        VietcombankSelenium.run();
    }
}
