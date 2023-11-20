import configs.Header;
import configs.RgwHeader;
import types.Area;
import types.Direction;
import types.ExitCode;
import types.Rgw;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static configs.RgwHeader.*;

public class RGWParser implements Parser {
    private String rgwRepository;
    private String outputFileName;

    private String csvRepository = "/home/matejik/git/cdrparser/csv/";

    Map<String, Area> areasHashmap;
    Map<String, Direction> directionsHashmap;
    Map<String, Rgw> rgwsHashmap;
    Map<String, ExitCode> exitCodesHashmap;
    private PrintWriter writer;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
    private DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private ZoneId cestZone = ZoneId.of("Europe/Paris");

    private RgwHeader[] headersToPrint = new RgwHeader[] {
            FROM,
            TO,
            AREA,
            DIRECTION,
            RGW_HEADER,
            CONNECTED_TIME,
            CALL_END_TIME,
            RGW_NAME,
            EXIT_CODE,
            MESSAGE,
            SUCCESS,
            DAY,
            DURATION
    };

    public RGWParser(String rgwRepository, String outputFileName) throws IOException {
        this.rgwRepository = rgwRepository;
        this.outputFileName = outputFileName;

        rgwsHashmap = new HashMap<>();
        areasHashmap = new HashMap<>();
        directionsHashmap = new HashMap<>();
        exitCodesHashmap = new HashMap<>();

        parseAreaFile();
        parseRgwFile();
        parseExitFile();
        parseDirectionFile();

        writer = new PrintWriter(new BufferedWriter(new FileWriter(this.outputFileName)));
        printHeaders();

    }

    private void printHeaders() {
        StringBuilder sb = new StringBuilder();

        for (RgwHeader column: headersToPrint) {
            sb.append(column.getTranslation());
            sb.append(";");
        }
        sb.setLength(sb.length() - 1);
        sb.append("\n");
        writer.write(sb.toString());
        writer.flush();
    }

    private void parseAreaFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + "area.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dataArray = line.split(";");
                this.areasHashmap.put(dataArray[0], new Area(dataArray[0], dataArray[1]));
            }
        }
    }

    private void parseDirectionFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + "direction.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dataArray = line.split(";");
                this.directionsHashmap.put(dataArray[0], new Direction(dataArray[0], dataArray[1]));
            }
        }
    }

    private void parseRgwFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + "rgw.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dataArray = line.split(";");
                this.rgwsHashmap.put(dataArray[0], new Rgw(dataArray[0], dataArray[1]));
            }
        }
    }

    private void parseExitFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + "exitCode.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dataArray = line.split(";");
                this.exitCodesHashmap.put(dataArray[0], new ExitCode(dataArray[0], dataArray[1], dataArray[2]));
            }
        }
    }

    @Override
    public void parseFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line != null) {
                // Parse data rows
                while ((line = reader.readLine()) != null && !(line.trim().equals("")) ) {
                    parseDataRow(line);
                }
            }
        }
    }

    private void parseDataRow(String line) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        String[] dataArray = line.replaceAll("\"", "").split(", ", -1);

        Map<Header, String> dataMap = Arrays.stream(dataArray)
                .map(pair -> pair.split("="))
                .filter(parts -> parts.length == 2 && Header.getByCode(RgwHeader.class, parts[0]) != null)
                .collect(Collectors.toMap(parts -> Header.getByCode(RgwHeader.class, parts[0]), parts -> parts[1]));

        parseDateTime(dataMap);

        if (dataMap.get(FROM).contains("#")) {
            dataMap.put(FROM, dataMap.get(FROM).split("#")[0]);
        }

        if (dataMap.get(TO).contains("#")) {
            dataMap.put(TO, dataMap.get(TO).split("#")[0]);
        }

        Area area = this.areasHashmap.get(dataMap.get(AREA));
        if (area != null) {
            dataMap.put(AREA, area.getName());
        }

        Direction direction = this.directionsHashmap.get(dataMap.get(DIRECTION));
        if (direction != null) {
            dataMap.put(DIRECTION, direction.getName());
        }

        dataMap.put(DIRECTION, dataMap.get(DIRECTION));

        StringBuilder sb = new StringBuilder();

        for (RgwHeader column: Arrays.asList(headersToPrint).subList(0, 5)) {
            sb.append(dataMap.get(column));
            sb.append(";");
        }

        try {
            sb.append(this.rgwsHashmap.get(dataMap.get(RGW_HEADER)).getRgw()).append(";");
        } catch (Exception e) {
            sb.append(";");
        }

        for (RgwHeader column: Arrays.asList(headersToPrint).subList(5, 7)) {
            sb.append(dataMap.get(column));
            sb.append(";");
        }


        appendTerminalInfo(sb, dataMap);

        sb.append("\n");

        writer.write(sb.toString());
        writer.flush();
    }

    private void parseDateTime(Map<Header, String> dataMap) {
        try {
            String trimmedStartTimeStr = dataMap.get(CONNECTED_TIME).replace("CEST ", "").replace("CET ", "").trim();
            String trimmedEndTimeStr = dataMap.get(CALL_END_TIME).replace("CEST ", "").replace("CET ", "").trim();

            ZonedDateTime zonedStartTime = ZonedDateTime.parse(trimmedStartTimeStr, formatter.withZone(cestZone));
            ZonedDateTime zonedEndTime = ZonedDateTime.parse(trimmedEndTimeStr, formatter.withZone(cestZone));

            dataMap.put(CONNECTED_TIME, zonedStartTime.format(outputFormatter));
            dataMap.put(CALL_END_TIME, zonedEndTime.format(outputFormatter));
        } catch (Exception e) {
            dataMap.put(CONNECTED_TIME, "");
            dataMap.put(CALL_END_TIME, "");
        }
    }

    private void appendTerminalInfo(StringBuilder sb, Map<Header, String> dataMap) {
        try {
            sb.append(this.exitCodesHashmap.get(dataMap.get(EXIT_CODE)).getExitCode()).append(";");
        } catch (Exception e) {
            sb.append(";");
        }

        try {
            sb.append(this.exitCodesHashmap.get(dataMap.get(EXIT_CODE)).getMessage()).append(";");
        } catch (Exception e) {
            sb.append(";");
        }

        try {
            sb.append(this.exitCodesHashmap.get(dataMap.get(EXIT_CODE)).getSuccess()).append(";");
        } catch (Exception e) {
            sb.append(";");
        }

        try {
            LocalDateTime startDate = LocalDateTime.parse(dataMap.get(CONNECTED_TIME), outputFormatter);
            LocalDateTime endDate = LocalDateTime.parse(dataMap.get(CALL_END_TIME), outputFormatter);
            Duration duration = Duration.between(startDate, endDate);
            sb.append(startDate.getDayOfWeek()).append(";").append(duration.getSeconds()).append(";");
        } catch (Exception e) {
            sb.append(";");
        }
    }

    public void parseClose() {
        writer.close();
    }
}


