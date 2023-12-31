package filters;

import configs.CdrHeader;

import java.util.List;
import java.util.Map;

import static configs.CdrHeader.*;

public class FailFromMobileFilter implements CdrFilter{
	@Override
	public int parse(String[] dataArray, List<String> headerColumns) {
		if (dataArray[headerColumns.indexOf(DEST_DEVICE_NAME.getCode())].startsWith("SEP")
				&& (!"Radio_Controller_1".equals(dataArray[headerColumns.indexOf(ORIG_DEVICE_NAME.getCode())])
				|| dataArray[headerColumns.indexOf(ORIG_DEVICE_NAME.getCode())] == null)
				&& Integer.parseInt(dataArray[headerColumns.indexOf(DURATION.getCode())]) == 0
				&& !dataArray[headerColumns.indexOf(CALLING_PARTY_NUMBER.getCode())].matches("^(06|07)\\d{8}$")
				&& !dataArray[headerColumns.indexOf(ORIG_CAUSE_VALUE.getCode())].matches("^(16|17|18|19|21|0)$")
				&& !dataArray[headerColumns.indexOf(DEST_CAUSE_VALUE.getCode())].matches("^(16|17|18|19|21|0)$")
		) {
			return  1;
		} else {
			return 0;
		}
	}
}
