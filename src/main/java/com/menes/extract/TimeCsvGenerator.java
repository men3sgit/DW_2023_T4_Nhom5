package com.menes.extract;

import java.io.FileWriter;
import java.io.IOException;

public class TimeCsvGenerator {

    private static final String CSV_FILE_PATH = "time_data.csv";
    private static int count = 1;

    public static void main(String[] args) {
        generateTimeCsv();
    }

    private static void generateTimeCsv() {
        try (FileWriter writer = new FileWriter(CSV_FILE_PATH)) {
            writeTimeData(writer);
            System.out.println("CSV file created successfully at: " + CSV_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeTimeData(FileWriter writer) throws IOException {
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute++) {
                for (int second = 0; second < 60; second++) {
                    writeTimeRow(writer, hour, minute, second);
                }
            }
        }
    }

    private static void writeTimeRow(FileWriter writer, int hour, int minute, int second) throws IOException {
        String timeRow = String.format(",%02d,%02d,%02d\n", hour, minute, second);
        writer.write(count++ + timeRow);
    }
}
