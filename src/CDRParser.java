import Types.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CDRParser {
    private String csvRepository;
    private String cdrRepository;

    private String OUT = "cdr_out.csv";
    private String TERMINALS = "terminals.csv";

    private String[] filterColumns = {
            "callingPartyNumber",
            "origCause_value",
            "originalCalledPartyNumber",
            "finalCalledPartyNumber",
            "destCause_value",
            "originalCalledPartyNumberPartition",
            "duration",
            "origDeviceName",
            "destDeviceName",
            "origIpv4v6Addr",
            "destIpv4v6Addr",
    };

    private String[] columns = {
            "origIpAddr",
            "callingPartyNumber",
            "origCause_value",
            "destIpAddr",
            "originalCalledPartyNumber",
            "finalCalledPartyNumber",
            "destCause_value",
            "dateTimeConnect",
            "dateTimeDisconnect",
            "duration",
            "origDeviceName",
            "destDeviceName"
    };

    private String[] headers = {
            "terminál",
            "terminál-ip",
            "hostName",
            "typ",
            "lokalita",
            "oblast",
            "den",
            "příchozí volání z mobilu",
            "odchozí volání na mobil",
            "příchozí volání z vysílačky",
            "odchozí volání na vysílačku",
            "příchozí volání z pevné linky",
            "odchozí volání na pevnou linku",
            "nepřijaté hovory z mobilu",
            "nevyzvednuté hovory mobilem",
            "nepřijaté hovory z vysílačky",
            "nevyzvednuté hovory vysílačkou",
            "nepřijaté hovory z pevné linky",
            "nevyzvednuté hovory pevnou linkou",
            "neúspěšné volání na mobil",
            "neúspěšné volání z mobilu",
            "neúspěšné volání na vysílačku",
            "neúspěšné volání z vysílačky",
            "neúspěšné volání na pevnou linku",
            "neúspěšné volání z pevné linky"
    };

    Map<String, Terminal> terminalsHashmap;

    private PrintWriter writer;

    public CDRParser(String csvRepository, String cdrRepository) throws IOException {
        this.csvRepository = csvRepository;
        this.cdrRepository = cdrRepository;

        terminalsHashmap = new HashMap<>();

        parseTerminalFile();

        writer = new PrintWriter(new BufferedWriter(new FileWriter(this.cdrRepository + OUT)));

        StringBuilder sb = new StringBuilder();
        for (String column: this.columns) {
            sb.append(column);
            sb.append(";");
        }
        for (String column: this.headers) {
            sb.append(column);
            sb.append(";");
        }

        sb.append("\n");
        writer.write(sb.toString());
        writer.flush();
    }

    private void parseTerminalFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.csvRepository + TERMINALS))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= 1) {
                    continue;
                }
                String[] dataArray = line.split(";", -1);
                this.terminalsHashmap.put(dataArray[0], new Terminal(dataArray[0], dataArray[1],
                        dataArray[2],dataArray[3],dataArray[4]));
            }
        }
    }

    public void parseCdrFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Map<String, Integer> indices = new HashMap<>();
            Map<String, Integer> selectors = new HashMap<>();

            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) {
                    List<String> fileColumns = Arrays.stream(line.replaceAll("\"", "").split(",")).collect(Collectors.toList());
                    for (String column: this.columns) {
                        int i = fileColumns.indexOf(column);
                        indices.put(column, i);
                    }
                    for (String column: this.filterColumns) {
                        int i = fileColumns.indexOf(column);
                        selectors.put(column, i);
                    }
                    continue;
                }
                parseDataRow(indices, selectors, line);
            }
        }
    }

    private void parseDataRow(Map<String, Integer> indices, Map<String, Integer> filterColumns, String line) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        String[] dataArray = line.replaceAll("\"", "").split(",", -1);
        StringBuilder sb = new StringBuilder();
        Terminal terminalObject = null;

        try {
            dataArray[indices.get("origIpAddr")] = convertToHumanReadable(Integer.parseInt(dataArray[indices.get("origIpAddr")]));
            dataArray[indices.get("destIpAddr")] = convertToHumanReadable(Integer.parseInt(dataArray[indices.get("destIpAddr")]));

        } catch (Exception e) {
            System.out.println(e);
        }

        long dateTimeConnect = (Long.parseLong(dataArray[indices.get("dateTimeConnect")])) * 1000L;
        long dateTimeDisconnect = (Long.parseLong(dataArray[indices.get("dateTimeDisconnect")])) * 1000L;
        dataArray[indices.get("dateTimeConnect")] = sdf.format(new Date(dateTimeConnect));
        dataArray[indices.get("dateTimeDisconnect")] = sdf.format(new Date(dateTimeDisconnect));

        for (String column: this.columns) {
            sb.append(dataArray[indices.get(column)]);
            sb.append(";");
        }

        String terminal = null;
        if (dataArray[indices.get("origDeviceName")].startsWith("SEP")) {
            terminal = dataArray[indices.get("origDeviceName")];
        } else if (dataArray[indices.get("destDeviceName")].startsWith("SEP")) {
            terminal = dataArray[indices.get("destDeviceName")];
        }
        sb.append(terminal);
        sb.append(";");

        String terminalIp = null;
        if (dataArray[indices.get("origDeviceName")].startsWith("SEP")) {
            terminalIp = dataArray[indices.get("origIpAddr")];
        } else if (dataArray[indices.get("destDeviceName")].startsWith("SEP")) {
            terminalIp = dataArray[indices.get("destIpAddr")];
        }
        sb.append(terminalIp);
        sb.append(";");

        if (terminalIp != null) {
            terminalObject = terminalsHashmap.get(terminalIp);
        }

        if (terminalObject != null) {
            sb.append(terminalObject.getName());
            sb.append(";");
            sb.append(terminalObject.getTyp());
            sb.append(";");
            sb.append(terminalObject.getLokalita());
            sb.append(";");
            sb.append(terminalObject.getOblast());
            sb.append(";");
        } else {
            for (int i = 0; i < 4; i++ ) {
                sb.append(";");
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");

        String dayOfWeek = dateFormat.format(new Date(dateTimeDisconnect));

        sb.append(dayOfWeek.toString());
        sb.append(";");

        int[] array = new int[18];

//        Příchozí na terminál z mobilu spojené
//        SELECT `destDeviceName`, count(`destDeviceName`) FROM `withHosts` WHERE `destDeviceName` LIKE 'SEP%' AND (`origDeviceName` != 'Radio_Controller_1' OR `origDeviceName` IS NULL) AND `duration` > 0 AND `callingPartyNumber` REGEXP '^(06|07)\d{8}$' GROUP BY `destDeviceName` ORDER BY `destDeviceName`;

        if (dataArray[filterColumns.get("destDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("origDeviceName")] != "Radio_Controller_1"
                    || dataArray[filterColumns.get("origDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) > 0
                && dataArray[filterColumns.get("callingPartyNumber")].matches("^(06|07)\\d{8}$")
        ) {
            array[0] = 1;
        }

//        Odchozí hovory z terminálů na mobil spojené
//        SELECT `origDeviceName`, count(`origDeviceName`) FROM `withHosts` WHERE `origDeviceName` LIKE 'SEP%' AND (`destDeviceName` != 'Radio_Controller_1' OR `destDeviceName` IS NULL) AND `duration` > 0 AND `finalCalledPartyNumber` REGEXP '^(06|07)\d{8}$' GROUP BY `origDeviceName` ORDER BY `origDeviceName`;

        if (dataArray[filterColumns.get("origDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("destDeviceName")] != "Radio_Controller_1"
                || dataArray[filterColumns.get("destDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) > 0
                && dataArray[filterColumns.get("finalCalledPartyNumber")].matches("^(06|07)\\d{8}$")
        ) {
            array[1] = 1;
        }

//        Příchozí na terminál z vysílačky spojené
//        SELECT `destDeviceName`, count(`destDeviceName`) FROM `withHosts` WHERE `destDeviceName` LIKE 'SEP%' AND (`origDeviceName` = 'Radio_Controller_1' OR `origDeviceName` IS NULL) AND `duration` > 0 GROUP BY `destDeviceName` ORDER BY `destDeviceName`;
        if (dataArray[filterColumns.get("destDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("origDeviceName")] == "Radio_Controller_1"
                || dataArray[filterColumns.get("origDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) > 0
        ) {
            array[2] = 1;
        }


//        Odchozí hovory z terminálů na vysílačku spojené
//        SELECT `origDeviceName`, count(`origDeviceName`) FROM `withHosts` WHERE `origDeviceName` LIKE 'SEP%' AND (`destDeviceName` = 'Radio_Controller_1' OR `destDeviceName` IS NULL) AND `duration` > 0 GROUP BY `origDeviceName` ORDER BY `origDeviceName`;
        if (dataArray[filterColumns.get("origDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("destDeviceName")] == "Radio_Controller_1"
                || dataArray[filterColumns.get("destDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) > 0
        ) {
            array[3] = 1;
        }

//        Příchozí hovory na terminál ostatní spojené
//        SELECT `destDeviceName`, count(`destDeviceName`) FROM `withHosts` WHERE `destDeviceName` LIKE 'SEP%' AND (`origDeviceName` != 'Radio_Controller_1' OR `origDeviceName` IS NULL) AND `duration` > 0 AND NOT (`callingPartyNumber` REGEXP '^(06|07)\d{8}$') GROUP BY `destDeviceName` ORDER BY `destDeviceName`;
        if (dataArray[filterColumns.get("destDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("origDeviceName")] != "Radio_Controller_1"
                || dataArray[filterColumns.get("origDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) > 0
                && !dataArray[filterColumns.get("callingPartyNumber")].matches("^(06|07)\\d{8}$")
        ) {
            array[4] = 1;
        }

//        Odchozí hovory z terminálů ostatní spojené
//        SELECT `origDeviceName`, count(`origDeviceName`) FROM `withHosts` WHERE `origDeviceName` LIKE 'SEP%' AND (`destDeviceName` != 'Radio_Controller_1' OR `destDeviceName` IS NULL) AND `duration` > 0 AND NOT (`finalCalledPartyNumber` REGEXP '^(06|07)\d{8}$') GROUP BY `origDeviceName` ORDER BY `origDeviceName`;
        if (dataArray[filterColumns.get("origDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("destDeviceName")] != "Radio_Controller_1"
                || dataArray[filterColumns.get("destDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) > 0
                && !dataArray[filterColumns.get("finalCalledPartyNumber")].matches("^(06|07)\\d{8}$")
        ) {
            array[5] = 1;
        }

//        Nevyzvednuté hovory z mobilu
//        SELECT `destDeviceName`, count(`destDeviceName`) FROM `withHosts` WHERE `destDeviceName` LIKE 'SEP%' AND (`origDeviceName` != 'Radio_Controller_1' OR `origDeviceName` IS NULL) AND `duration` = 0 AND `callingPartyNumber` REGEXP '^(06|07)\d{8}$' AND (`origCause_value` REGEXP '^(16|17|18|19|21|0)$' AND `destCause_value` REGEXP '^(16|17|18|19|21|0)$') GROUP BY `destDeviceName` ORDER BY `destDeviceName`;

        if (dataArray[filterColumns.get("destDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("origDeviceName")] != "Radio_Controller_1"
                || dataArray[filterColumns.get("origDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && dataArray[filterColumns.get("callingPartyNumber")].matches("^(06|07)\\d{8}$")
                && dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[6] = 1;
        }



//        Hovory nepřijaté na mobilu
//        SELECT `origDeviceName`, count(`origDeviceName`) FROM `withHosts` WHERE `origDeviceName` LIKE 'SEP%' AND (`destDeviceName` != 'Radio_Controller_1' OR `destDeviceName` IS NULL) AND `duration` = 0 AND `finalCalledPartyNumber` REGEXP '^(06|07)\d{8}$' AND (`origCause_value` REGEXP '^(16|17|18|19|21|0)$' AND `destCause_value` REGEXP '^(16|17|18|19|21|0)$') GROUP BY `origDeviceName` ORDER BY `origDeviceName`;
        if (dataArray[filterColumns.get("origDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("destDeviceName")] != "Radio_Controller_1"
                || dataArray[filterColumns.get("destDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && dataArray[filterColumns.get("finalCalledPartyNumber")].matches("^(06|07)\\d{8}$")
                && dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[7] = 1;
        }

//        Nevyzvednuté hovory z vysílačky
//        SELECT `destDeviceName`, count(`destDeviceName`) FROM `withHosts` WHERE `destDeviceName` LIKE 'SEP%' AND (`origDeviceName` = 'Radio_Controller_1') AND `duration` = 0 AND (`origCause_value` REGEXP '^(16|17|18|19|21|0)$' AND `destCause_value` REGEXP '^(16|17|18|19|21|0)$') GROUP BY `destDeviceName` ORDER BY `destDeviceName`;
        if (dataArray[filterColumns.get("destDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("origDeviceName")] == "Radio_Controller_1")
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[8] = 1;
        }
//
//        Hovory nepřijaté na vysílačce
//        SELECT `origDeviceName`, count(`origDeviceName`) FROM `withHosts` WHERE `origDeviceName` LIKE 'SEP%' AND (`destDeviceName` = 'Radio_Controller_1') AND `duration` = 0 AND (`origCause_value` REGEXP '^(16|17|18|19|21|0)$' AND `destCause_value` REGEXP '^(16|17|18|19|21|0)$') GROUP BY `origDeviceName` ORDER BY `origDeviceName`;
        if (dataArray[filterColumns.get("origDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("destDeviceName")] == "Radio_Controller_1")
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[9] = 1;
        }
//
//        Nevyzvednuté hovory z ostatních
//        SELECT `destDeviceName`, count(`destDeviceName`) FROM `withHosts` WHERE `destDeviceName` LIKE 'SEP%' AND (`origDeviceName` != 'Radio_Controller_1' OR `origDeviceName` IS NULL) AND `duration` = 0 AND (NOT (`callingPartyNumber` REGEXP '^(06|07)\d{8}$')) AND (`origCause_value` REGEXP '^(16|17|18|19|21|0)$' AND `destCause_value` REGEXP '^(16|17|18|19|21|0)$') GROUP BY `destDeviceName` ORDER BY `destDeviceName`;
        if (dataArray[filterColumns.get("destDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("origDeviceName")] != "Radio_Controller_1"
                || dataArray[filterColumns.get("origDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && !dataArray[filterColumns.get("callingPartyNumber")].matches("^(06|07)\\d{8}$")
                && dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[10] = 1;
        }
//
//        Hovory nepřijaté na ostatních
//        SELECT `origDeviceName`, count(`origDeviceName`) FROM `withHosts` WHERE `origDeviceName` LIKE 'SEP%' AND (`destDeviceName` != 'Radio_Controller_1' OR `destDeviceName` IS NULL) AND `duration` = 0 AND (NOT (`finalCalledPartyNumber` REGEXP '^(06|07)\d{8}$')) AND (`origCause_value` REGEXP '^(16|17|18|19|21|0)$' AND `destCause_value` REGEXP '^(16|17|18|19|21|0)$') GROUP BY `origDeviceName` ORDER BY `origDeviceName`;
        if (dataArray[filterColumns.get("origDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("destDeviceName")] != "Radio_Controller_1"
                || dataArray[filterColumns.get("destDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && dataArray[filterColumns.get("finalCalledPartyNumber")].matches("^(06|07)\\d{8}$")
                && dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[11] = 1;

        }
//
//        Neúspěšné volání z mobilu
//        SELECT `destDeviceName`, count(`destDeviceName`) FROM `withHosts` WHERE `destDeviceName` LIKE 'SEP%' AND (`origDeviceName` != 'Radio_Controller_1' OR `origDeviceName` IS NULL) AND `duration` = 0 AND `callingPartyNumber` REGEXP '^(06|07)\d{8}$'  AND ((NOT(`origCause_value` REGEXP '^(16|17|18|19|21)$')) AND (NOT(`destCause_value` REGEXP '^(16|17|18|19|21)$'))) GROUP BY `destDeviceName` ORDER BY `destDeviceName`;
        if (dataArray[filterColumns.get("destDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("origDeviceName")] != "Radio_Controller_1"
                || dataArray[filterColumns.get("origDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && dataArray[filterColumns.get("callingPartyNumber")].matches("^(06|07)\\d{8}$")
                && !dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && !dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[12] = 1;

        }
//
//        Neúspěšné volání na mobil
//        SELECT `origDeviceName`, count(`origDeviceName`) FROM `withHosts` WHERE `origDeviceName` LIKE 'SEP%' AND (`destDeviceName` != 'Radio_Controller_1' OR `destDeviceName` IS NULL) AND `duration` = 0 AND `finalCalledPartyNumber` REGEXP '^(06|07)\d{8}$' AND ((NOT(`origCause_value` REGEXP '^(16|17|18|19|21)$')) AND (NOT(`destCause_value` REGEXP '^(16|17|18|19|21)$'))) GROUP BY `origDeviceName` ORDER BY `origDeviceName`;
        if (dataArray[filterColumns.get("origDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("destDeviceName")] != "Radio_Controller_1"
                || dataArray[filterColumns.get("destDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && dataArray[filterColumns.get("finalCalledPartyNumber")].matches("^(06|07)\\d{8}$")
                && !dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && !dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[13] = 1;

        }
//
//        Neúspěšné volání z vysílačky
//        SELECT `destDeviceName`, count(`destDeviceName`) FROM `withHosts` WHERE `destDeviceName` LIKE 'SEP%' AND (`origDeviceName` = 'Radio_Controller_1') AND `duration` = 0 AND ((NOT(`origCause_value` REGEXP '^(16|17|18|19|21)$')) AND (NOT(`destCause_value` REGEXP '^(16|17|18|19|21)$'))) GROUP BY `destDeviceName` ORDER BY `destDeviceName`;
        if (dataArray[filterColumns.get("destDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("origDeviceName")] == "Radio_Controller_1"
                || dataArray[filterColumns.get("origDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && !dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && !dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[14] = 1;

        }
//
//        Neúspěšné volání na vysílačku
//        SELECT `origDeviceName`, count(`origDeviceName`) FROM `withHosts` WHERE `origDeviceName` LIKE 'SEP%' AND (`destDeviceName` = 'Radio_Controller_1') AND `duration` = 0 AND ((NOT(`origCause_value` REGEXP '^(16|17|18|19|21)$')) AND (NOT(`destCause_value` REGEXP '^(16|17|18|19|21)$'))) GROUP BY `origDeviceName` ORDER BY `origDeviceName`;
        if (dataArray[filterColumns.get("origDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("destDeviceName")] == "Radio_Controller_1"
                || dataArray[filterColumns.get("destDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && !dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && !dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[15] = 1;
        }
//
//        Neúspěšné volání z ostatních
//        SELECT `destDeviceName`, count(`destDeviceName`) FROM `withHosts` WHERE `destDeviceName` LIKE 'SEP%' AND (`origDeviceName` != 'Radio_Controller_1' OR `origDeviceName` IS NULL) AND `duration` = 0 AND (NOT (`callingPartyNumber` REGEXP '^(06|07)\d{8}$')) AND ((NOT(`origCause_value` REGEXP '^(16|17|18|19|21)$')) AND (NOT(`destCause_value` REGEXP '^(16|17|18|19|21)$'))) GROUP BY `destDeviceName` ORDER BY `destDeviceName`;
        if (dataArray[filterColumns.get("destDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("origDeviceName")] == "Radio_Controller_1"
                || dataArray[filterColumns.get("origDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && !dataArray[filterColumns.get("callingPartyNumber")].matches("^(06|07)\\d{8}$")
                && !dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && !dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[16] = 1;
        }
//
//        Neúspěšné volání na ostatní
//        SELECT `origDeviceName`, count(`origDeviceName`) FROM `withHosts` WHERE `origDeviceName` LIKE 'SEP%' AND (`destDeviceName` != 'Radio_Controller_1' OR `destDeviceName` IS NULL) AND `duration` = 0 AND (NOT (`finalCalledPartyNumber` REGEXP '^(06|07)\d{8}$')) AND ((NOT(`origCause_value` REGEXP '^(16|17|18|19|21)$')) AND (NOT(`destCause_value` REGEXP '^(16|17|18|19|21)$'))) GROUP BY `origDeviceName` ORDER BY `origDeviceName`;
        if (dataArray[filterColumns.get("origDeviceName")].startsWith("SEP")
                && (dataArray[filterColumns.get("destDeviceName")] == "Radio_Controller_1"
                || dataArray[filterColumns.get("destDeviceName")] == null)
                && Integer.parseInt(dataArray[filterColumns.get("duration")]) == 0
                && !dataArray[filterColumns.get("finalCalledPartyNumber")].matches("^(06|07)\\d{8}$")
                && !dataArray[filterColumns.get("origCause_value")].matches("^(16|17|18|19|21|0)$")
                && !dataArray[filterColumns.get("destCause_value")].matches("^(16|17|18|19|21|0)$")
        ) {
            array[17] = 1;
        }

        for (int i: array) {
            sb.append(i);
            sb.append(";");
        }

        sb.append("\n");
        writer.write(sb.toString());
        writer.flush();
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


