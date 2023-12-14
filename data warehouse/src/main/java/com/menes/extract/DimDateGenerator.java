package com.menes.extract;

import com.menes.utils.ExceptionMailer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DimDateGenerator {

    public static void main(String[] args) {
        try {
            generateCsv("dim_date.csv");
        } catch (IOException e) {
            ExceptionMailer.handleException(e);
            e.printStackTrace();
        }
    }

    private static void generateCsv(String outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {

            // Write CSV header
            writer.println("full_date,day_of_week,day_of_month,month,month_number,quarter,year");

            // Generate data and write to CSV from January 1, 2020, to December 31, 2026
            Calendar calendar = Calendar.getInstance();
            calendar.set(2020, Calendar.JANUARY, 1);

            Calendar endDate = Calendar.getInstance();
            endDate.set(2026, Calendar.DECEMBER, 31);

            while (calendar.before(endDate) || calendar.equals(endDate)) {
                Date currentDate = calendar.getTime();

                writer.printf("%s,%d,%d,%s,%d,%d,%d%n",
                        formatDate(currentDate),
                        calendar.get(Calendar.DAY_OF_WEEK),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        new SimpleDateFormat("MMMM").format(currentDate),
                        calendar.get(Calendar.MONTH) + 1,
                        (calendar.get(Calendar.MONTH) / 3) + 1,
                        calendar.get(Calendar.YEAR));

                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            System.out.println("CSV file has been generated successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
