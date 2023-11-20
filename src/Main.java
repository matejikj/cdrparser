import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void parseRGWFilesWithDate() {
        String rgwRepositoryValue = "/home/matejik/git/cdrparser/rgw/";
        Pattern pattern = Pattern.compile("\\d{8}");
        Date startDate, endDate;
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
        try {
            startDate = sdf.parse("05082023");
            endDate = sdf.parse("09122023");
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        String filename = rgwRepositoryValue + "rgw_statistics_" + System.currentTimeMillis() + ".csv";
        try {
            File directory = new File(rgwRepositoryValue);
            if (directory.exists() && directory.isDirectory()) {
                File[] allFiles = directory.listFiles();
                Parser parser = new RGWParser(rgwRepositoryValue, filename);
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
                                        parser.parseFile(file.getPath());
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
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void parseCDRFilesWithDate() {
        String cdrRepositoryValue = "/home/matejik/git/cdrparser/cdr/";
        String csvRepository = "/home/matejik/git/cdrparser/csv-repository/";
        Pattern pattern = Pattern.compile("\\d{8}");
        Date startDate, endDate;
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
        try {
            startDate = sdf.parse("05082023");
            endDate = sdf.parse("09122023");
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        String filename = cdrRepositoryValue + "cdr_statistics_" + System.currentTimeMillis() + ".csv";
        try {
            File directory = new File(cdrRepositoryValue);
            if (directory.exists() && directory.isDirectory()) {
                File[] allFiles = directory.listFiles();
                CDRParser parser = new CDRParser(cdrRepositoryValue, filename);
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
                                        parser.parseFile(file.getPath());
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
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        parseRGWFilesWithDate();
    }
}