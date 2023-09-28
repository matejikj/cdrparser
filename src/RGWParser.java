import Types.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class RGWParser {
    private String csvRepository;
    private String cdrRepository;

    private String AREAS = "area.csv";
    private String EXIT_CODES = "exitCode.csv";
    private String OUT = "out.txt";
    private String RGW = "rgw.csv";
    private String DIRECTIONS = "direction.csv";
    private String COLUMNS = "rgw_columns.csv";

    Map<String, Area> areasHashmap;
    Map<String, Direction> directionsHashmap;
    Map<String, Rgw> rgwsHashmap;
    Map<String, ExitCode> exitCodesHashmap;

    List<String> columns;

    private PrintWriter writer;

    public RGWParser(String csvRepository, String cdrRepository) throws IOException {
        this.csvRepository = csvRepository;
        this.cdrRepository = cdrRepository;

        areasHashmap = new HashMap<>();
        rgwsHashmap = new HashMap<>();
        directionsHashmap = new HashMap<>();
        exitCodesHashmap = new HashMap<>();
        columns = new LinkedList<>();

        parseAreaFile();
        parseDirectionFile();
        parseExitFile();
        parseRgwFile();
        parseColumns();

        writer = new PrintWriter(new BufferedWriter(new FileWriter(cdrRepository + OUT)));
    }

    private void parseColumns() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + COLUMNS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] dataArray = line.split(", ");
                for (String item : dataArray) {
                    this.columns.add(item);
                }
            }
        }
    }

    private void parseAreaFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + AREAS))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= 1) {
                    continue;
                }
                String[] dataArray = line.split(";");
                this.areasHashmap.put(dataArray[0], new Area(dataArray[0], dataArray[1]));
            }
        }
    }

    private void parseDirectionFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + DIRECTIONS))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= 1) {
                    continue;
                }
                String[] dataArray = line.split(";");
                this.directionsHashmap.put(dataArray[0], new Direction(dataArray[0], dataArray[1]));
            }
        }
    }

    private void parseRgwFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + RGW))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= 1) {
                    continue;
                }
                String[] dataArray = line.split(";");
                this.rgwsHashmap.put(dataArray[0], new Rgw(dataArray[0], dataArray[1]));
            }
        }
    }

    private void parseExitFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + EXIT_CODES))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= 1) {
                    continue;
                }
                String[] dataArray = line.split(";");
                this.exitCodesHashmap.put(dataArray[0], new ExitCode(dataArray[0], dataArray[1], dataArray[2]));
            }
        }
    }

    public void parseCdrFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Map<String, Integer> indices = new HashMap<>();

            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) {
                    List<String> fileColumns = Arrays.stream(line.replaceAll("\"", "").split(",")).collect(Collectors.toList());
                    for (String column: this.columns) {
                        int i = fileColumns.indexOf(column);
                        indices.put(column, i);
                    }
                    continue;
                }
                parseDataRow(indices, line);
            }
        }
    }

    private void parseDataRow(Map<String, Integer> indices, String line) throws IOException {

    }

    public static String convertToHumanReadable(int ipAddress) {
//        return InetAddress.getByName(String.valueOf(Math.abs(ipAddress))).getHostAddress();
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                ((ipAddress >> 24) & 0xFF);
    }

    public void parseClose() {
        writer.close();
    }
}


