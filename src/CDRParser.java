import configs.CdrHeader;
import filters.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static configs.CdrHeader.*;

public class CDRParser implements Parser {
    private String csvRepository;
    private CdrHeader[] headersToPrint = new CdrHeader[] {
            ORIG_CALLED_PARTY_NUMBER,
            CALLING_PARTY_NUMBER,
            FINAL_CALLED_PARTY_NUMBER,
            ORIG_IP_ADDR,
            DEST_IP_ADDR,
            ORIG_CAUSE_VALUE,
            DEST_CAUSE_VALUE,
            DATETIME_CONNECT,
            DATETIME_DISCONNECT,
            DURATION,
            ORIG_DEVICE_NAME,
            DEST_DEVICE_NAME,
            TERMINAL,
            TERMINAL_IP,
            HOSTNAME,
            DAY,
            INCOMING_TERMINAL_FROM_MOBILE,
            OUTGOING_TERMINAL_TO_MOBILE,
            INCOMING_TERMINAL_FROM_TRANSMITTER,
            OUTGOING_TERMINAL_TO_TRANSMITTER,
            INCOMING_TERMINAL_FROM_OTHERS,
            OUTGOING_TERMINAL_TO_OTHERS,
            MISSED_FROM_MOBILE,
            MISSED_ON_MOBILE,
            MISSED_FROM_TRANSMITTER,
            MISSED_ON_TRANSMITTER,
            MISSED_FROM_OTHERS,
            MISSED_ON_OTHERS,
            FAIL_TO_MOBILE,
            FAIL_FROM_MOBILE,
            FAIL_TO_TRANSMITTER,
            FAIL_FROM_TRANSMITTER,
            FAIL_TO_LANDLINE,
            FAIL_FROM_LANDLINE
    };

    Map<String, String> terminalsHostNames;
    private PrintWriter writer;
    List<CdrFilter> cdrFilters;

    public CDRParser(String cdrConfigsRepository, String outputFile) throws IOException {
        this.csvRepository = cdrConfigsRepository;
        terminalsHostNames = new HashMap<>();
        cdrFilters = new LinkedList<>();
        writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)));
        printHeaders(headersToPrint, writer);

        initializeCdrFilters();
    }

    @Override
    public void parseFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line != null) {
                //  Get headers indices from first line
                List<String> headerColumns = Arrays.stream(line.replaceAll("\"", "").split(",")).collect(Collectors.toList());

                // Parse data rows
                while ((line = reader.readLine()) != null && !(line.trim().equals("")) ) {
                    parseDataRow(headerColumns, line);
                }
            }
        } catch (Exception e) {
            System.out.println("It is not possible to parse file: " + filePath);
        }
    }

    private void parseDataRow(List<String> headerColumns, String line) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        String[] dataColumns = line.replaceAll("\"", "").split(",", -1);
        StringBuilder sb = new StringBuilder();

        dataColumns[getIndexOfColumn(headerColumns, ORIG_IP_ADDR)] = convertToHumanReadable(Integer.parseInt(dataColumns[getIndexOfColumn(headerColumns, ORIG_IP_ADDR)]));
        dataColumns[getIndexOfColumn(headerColumns, DEST_IP_ADDR)] = convertToHumanReadable(Integer.parseInt(dataColumns[getIndexOfColumn(headerColumns, DEST_IP_ADDR)]));

        long dateTimeConnect = (Long.parseLong(dataColumns[getIndexOfColumn(headerColumns, DATETIME_CONNECT)])) * 1000L;
        long dateTimeDisconnect = (Long.parseLong(dataColumns[getIndexOfColumn(headerColumns, DATETIME_DISCONNECT)])) * 1000L;
        dataColumns[getIndexOfColumn(headerColumns, DATETIME_CONNECT)] = sdf.format(new Date(dateTimeConnect));
        dataColumns[getIndexOfColumn(headerColumns, DATETIME_DISCONNECT)] = sdf.format(new Date(dateTimeDisconnect));

        for (CdrHeader column: Arrays.asList(headersToPrint).subList(0, 12)) {
            sb.append(dataColumns[getIndexOfColumn(headerColumns, column)]);
            sb.append(";");
        }

        appendTerminalInfo(sb, dataColumns, headerColumns);
        appendDayOfWeek(sb, dateTimeDisconnect);

        this.cdrFilters.forEach(x -> sb.append(x.parse(dataColumns, headerColumns)).append(";"));

        sb.setLength(sb.length() - 1);
        sb.append("\n");
        writer.write(sb.toString());
        writer.flush();
    }

    private void appendTerminalInfo(StringBuilder sb, String[] dataArray, List<String> headerColumns) {
        String terminal = "";
        String terminalIp = "";
        if (dataArray[getIndexOfColumn(headerColumns, ORIG_DEVICE_NAME)].startsWith("SEP")) {
            terminal = dataArray[getIndexOfColumn(headerColumns, ORIG_DEVICE_NAME)];
            terminalIp = dataArray[getIndexOfColumn(headerColumns, ORIG_IP_ADDR)];
        } else if (dataArray[getIndexOfColumn(headerColumns, DEST_DEVICE_NAME)].startsWith("SEP")) {
            terminal = dataArray[getIndexOfColumn(headerColumns, DEST_DEVICE_NAME)];
            terminalIp = dataArray[getIndexOfColumn(headerColumns, DEST_IP_ADDR)];
        }
        sb.append(terminal);
        sb.append(";");
        sb.append(terminalIp);
        sb.append(";");
        if (!terminalIp.equals("")) {
            String terminalHostName = terminalsHostNames.get(terminalIp);
            if (terminalHostName != null) {
                sb.append(terminalHostName);
            }
        }
        sb.append(";");
    }

    private void appendDayOfWeek(StringBuilder sb, long dateTimeDisconnect) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        String dayOfWeek = dateFormat.format(new Date(dateTimeDisconnect));

        sb.append(dayOfWeek);
        sb.append(";");
    }

    public static String convertToHumanReadable(int ipAddress) throws UnknownHostException {
        return InetAddress.getByName(String.valueOf(Math.abs(ipAddress))).getHostAddress();
    }

    private void initializeCdrFilters() {
        cdrFilters = new LinkedList<>();
        cdrFilters.add(new IncomingFromMobileFilter());
        cdrFilters.add(new OutgoingToMobileFilter());
        cdrFilters.add(new IncomingFromTransmitterFilter());
        cdrFilters.add(new OutgoingToTransmitterFilter());
        cdrFilters.add(new IncomingFromOthersFilter());
        cdrFilters.add(new OutgoingToOthersFilter());
        cdrFilters.add(new MissedFromMobileFilter());
        cdrFilters.add(new MissedOnMobileFilter());
        cdrFilters.add(new MissedFromTransmitterFilter());
        cdrFilters.add(new MissedOnTransmitterFilter());
        cdrFilters.add(new MissedFromOthersFilter());
        cdrFilters.add(new MissedOnOthersFilter());
        cdrFilters.add(new FailToMobileFilter());
        cdrFilters.add(new FailFromMobileFilter());
        cdrFilters.add(new FailToTransmitterFilter());
        cdrFilters.add(new FailFromTransmitterFilter());
        cdrFilters.add(new FailToOthersFilter());
        cdrFilters.add(new FailFromOthersFilter());
    }

    public void parseClose() {
        writer.close();
    }
}


