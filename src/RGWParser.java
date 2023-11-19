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

import static configs.CdrFiles.*;
import static configs.RgwHeader.*;

public class RGWParser implements Parser {
    private String csvRepository;
    private String cdrRepository;
    Map<String, Area> areasHashmap;
    Map<String, Direction> directionsHashmap;
    Map<String, Rgw> rgwsHashmap;
    Map<String, ExitCode> exitCodesHashmap;
    private PrintWriter writer;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
    private DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private ZoneId cestZone = ZoneId.of("Europe/Paris");

    private RgwHeader[] mappedHeaders = new RgwHeader[] {
            FROM,
            TO,
            AREA,
            DIRECTION,
            RGW_HEADER,
            CONNECTED_TIME,
            CALL_END_TIME,
            EXIT_CODE
    };

    private RgwHeader[] headersToPrint = new RgwHeader[] {
            FROM_HEADER,
            TO_HEADER,
            AREA_HEADER,
            DIRECTION_HEADER,
            RGW_HEADER_HEADER,
            CONNECTED_TIME_HEADER,
            CALL_END_TIME_HEADER,
            EXIT_CODE_HEADER,
            RGW_NAME_HEADER,
            MESSAGE_HEADER,
            USPECH_HEADER,
            DAY_HEADER,
            DURATION_HEADER
    };

    public RGWParser(String csvRepository, String cdrRepository) throws IOException {
        this.csvRepository = csvRepository;
        this.cdrRepository = cdrRepository;

        rgwsHashmap = new HashMap<>();
        areasHashmap = new HashMap<>();
        directionsHashmap = new HashMap<>();
        exitCodesHashmap = new HashMap<>();

        parseAreaFile();
        parseRgwFile();
        parseExitFile();
        parseDirectionFile();

        writer = new PrintWriter(new BufferedWriter(new FileWriter(this.cdrRepository + "OUT")));
        printHeaders();

    }

    private void printHeaders() {
        StringBuilder sb = new StringBuilder();

        for (RgwHeader column: headersToPrint) {
            sb.append(column.getDescription());
            sb.append(";");
        }
        sb.setLength(sb.length() - 1);
        sb.append("\n");
        writer.write(sb.toString());
        writer.flush();
    }

    private void parseAreaFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + AREAS))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dataArray = line.split(";");
                this.areasHashmap.put(dataArray[0], new Area(dataArray[0], dataArray[1]));
            }
        }
    }

    private void parseDirectionFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + DIRECTIONS))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dataArray = line.split(";");
                this.directionsHashmap.put(dataArray[0], new Direction(dataArray[0], dataArray[1]));
            }
        }
    }

    private void parseRgwFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + RGW))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dataArray = line.split(";");
                this.rgwsHashmap.put(dataArray[0], new Rgw(dataArray[0], dataArray[1]));
            }
        }
    }

    private void parseExitFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + EXIT_CODES))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dataArray = line.split(";");
                this.exitCodesHashmap.put(dataArray[0], new ExitCode(dataArray[0], dataArray[1], dataArray[2]));
            }
        }
    }

    public void parseCdrFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            Map<String, Integer> indices = new HashMap<>();
            while ((line = reader.readLine()) != null && !(line.trim().equals("")) ) {
                parseDataRow(indices, line);
            }
        }
    }

    private void parseDataRow(Map<String, Integer> indices, String line) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        String[] dataArray = line.replaceAll("\"", "").split(", ", -1);

        Map<String, String> dataMap = Arrays.stream(dataArray)
                .map(pair -> pair.split("="))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));

        parseDateTime(dataMap);

//        if (dataMap.get(FROM).contains("#")) {
//            dataMap.put(FROM, dataMap.get(FROM).split("#")[0]);
//        }
//        if (dataMap.get(TO.getDescription()).contains("#")) {
//            dataMap.put(TO, dataMap.get(TO).split("#")[0]);
//        }
//
//        Area area = this.areasHashmap.get(dataMap.get(AREA));
//        if (area != null) {
//            dataMap.put(TO, area.getName());
//        }
//        Direction direction = this.directionsHashmap.get(dataMap.get(DIRECTION));
//        if (direction != null) {
//            dataMap.put(DIRECTION, direction.getName());
//        }
//
//        dataMap.put(DIRECTION, dataMap.get(DIRECTION));
//
//        StringBuilder sb = new StringBuilder();
//
//        for (String column: mappedHeaders) {
//            sb.append(dataMap.get(column));
//            sb.append(";");
//        }

//        appendTerminalInfo(sb, dataMap);
//
//        sb.append("\n");
//
//        writer.write(sb.toString());
//        writer.flush();
    }

    private void parseDateTime(Map<String, String> dataMap) {
//        try {
//            String trimmedStartTimeStr = dataMap.get(CONNECTED_TIME).replace("CEST ", "").trim();
//            String trimmedEndTimeStr = dataMap.get(CALL_END_TIME).replace("CEST ", "").trim();
//
//            ZonedDateTime zonedStartTime = ZonedDateTime.parse(trimmedStartTimeStr, formatter.withZone(cestZone));
//            ZonedDateTime zonedEndTime = ZonedDateTime.parse(trimmedEndTimeStr, formatter.withZone(cestZone));
//
//            dataMap.put(CONNECTED_TIME, zonedStartTime.format(outputFormatter));
//            dataMap.put(CALL_END_TIME, zonedEndTime.format(outputFormatter));
//        } catch (Exception e) {
//            dataMap.put(CONNECTED_TIME, "");
//            dataMap.put(CALL_END_TIME, "");
//        }
    }

    private void appendTerminalInfo(StringBuilder sb, Map<String, String> dataMap) {
        try {
            sb.append(this.rgwsHashmap.get(dataMap.get(RGW_HEADER)).getRgw()).append(";");
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

    @Override
    public void parseFile(String filePath) throws IOException {

    }
}


