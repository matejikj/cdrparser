package filters;

import configs.CdrHeader;

import java.util.List;
import java.util.Map;

public class FailFromMobileFilter implements CdrFilter{
	@Override
	public int parse(String[] dataArray, List<String> headerColumns) {
//		if (dataArray[indices.get(CdrHeader.DEST_DEVICE_CDR_HEADER)].startsWith("SEP")
//				&& (!"Radio_Controller_1".equals(dataArray[indices.get(CdrHeader.ORIG_DEVICE_CDR_HEADER)])
//				|| dataArray[indices.get(CdrHeader.ORIG_DEVICE_CDR_HEADER)] == null)
//				&& Integer.parseInt(dataArray[indices.get(CdrHeader.DURATION_CDR_HEADER)]) == 0
//				&& !dataArray[indices.get(CdrHeader.CALLING_PARTY_CDR_HEADER)].matches("^(06|07)\\d{8}$")
//				&& !dataArray[indices.get(CdrHeader.ORIG_CAUSE_CDR_HEADER)].matches("^(16|17|18|19|21|0)$")
//				&& !dataArray[indices.get(CdrHeader.DEST_CAUSE_CDR_HEADER)].matches("^(16|17|18|19|21|0)$")
//		) {
//			return  1;
//		} else {
//			return 0;
//		}
		return 1;
	}
}
