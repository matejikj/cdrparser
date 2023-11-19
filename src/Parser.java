import configs.Header;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public interface Parser {
    void parseFile(String filePath) throws IOException;
    default void printHeaders(Header[] headersToPrint, PrintWriter writer) {
        StringBuilder sb = new StringBuilder();
        for (Header column: headersToPrint) {
            sb.append(column.getDescription());
            sb.append(";");
        }
        sb.setLength(sb.length() - 1);
        sb.append("\n");
        writer.write(sb.toString());
        writer.flush();
    }
    default int getIndexOfColumn(List<String> columns, Header header) {
        return columns.indexOf(header.getDescription());
    }

}
