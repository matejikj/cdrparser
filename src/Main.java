import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void parseRGW() throws IOException {
        String directoryPath = "/home/matejik/cdr/";
        String csvRepository = "/home/matejik/csv-repository/";
        File directory = new File(directoryPath);

        Pattern pattern = Pattern.compile("rgwlog");

        RGWParser parser = new RGWParser(csvRepository, directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] allFiles = directory.listFiles();

            for (File file : allFiles) {
                if (file.isFile()) {
                    String fileName = file.getName();

                    Matcher matcher = pattern.matcher(fileName);
                    if (matcher.find()) {
                        try {
                            parser.parseCdrFile(file.getPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            parser.parseClose();
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }
    }

    public static void parseAllFiles() throws IOException {
//        String directoryPath = "/home/matejik/cdr/";
//        String csvRepository = "/home/matejik/csv-repository/";
        String directoryPath = "C:\\Users\\jakub_w7ohip9\\git\\cdrparser\\cdr\\";
        String csvRepository = "C:\\Users\\jakub_w7ohip9\\git\\cdrparser\\csv\\";
        File directory = new File(directoryPath);
        CDRParser parser = new CDRParser(csvRepository, directoryPath);

        parser.parseCdrFile(directoryPath + "cucm_hk_cdr_from_june.txt");
        parser.parseClose();
    }



    public static void parseCDRFilesWithDate() throws IOException {
        String directoryPath = "/home/matejik/cdr/";
        String csvRepository = "/home/matejik/csv-repository/";
        File directory = new File(directoryPath);

        Pattern pattern = Pattern.compile("\\d{8}\\d{4}");

        Date startDate, endDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

        try {
            startDate = sdf.parse("202308050000"); // Example start date
            endDate = sdf.parse("202309100000");   // Example end date
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        CDRParser parser = new CDRParser(csvRepository, directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] allFiles = directory.listFiles();

            for (File file : allFiles) {
                if (file.isFile()) {
                    String fileName = file.getName();

                    Matcher matcher = pattern.matcher(fileName);
                    if (matcher.find()) {
                        String dateString = matcher.group();

                        try {

                            Date date = sdf.parse(dateString);

                            if (date.after(startDate) && date.before(endDate)) {

                                try {
                                    parser.parseCdrFile(file.getPath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            parser.parseClose();
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }
    }

    public static void main(String[] args) throws IOException {
//        parseCDRFilesWithDate();
//        parseRGW();
        parseAllFiles();
    }
}