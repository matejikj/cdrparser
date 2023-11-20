/*
 * Copyright (c) 2023. TTC MARCONI s.r.o.
 * All Rights Reserved.
 *
 * All information contained herein is proprietary and confidential
 * to TTC MARCONI s.r.o. Any use, reproduction, or disclosure
 * without the written permission of TTC MARCONI s.r.o is prohibited.
 */
package filters;

import configs.CdrHeader;

import java.util.List;

public class FailFromTransmitterFilter implements CdrFilter{
	@Override
	public int parse(String[] dataArray, List<String> headerColumns) {
		if (dataArray[headerColumns.indexOf(CdrHeader.DEST_DEVICE_NAME.getCode())].startsWith("SEP")
				&& ("Radio_Controller_1".equals(dataArray[headerColumns.indexOf(CdrHeader.ORIG_DEVICE_NAME.getCode())])
				|| dataArray[headerColumns.indexOf(CdrHeader.ORIG_DEVICE_NAME.getCode())] == null)
				&& Integer.parseInt(dataArray[headerColumns.indexOf(CdrHeader.DURATION.getCode())]) == 0
				&& !dataArray[headerColumns.indexOf(CdrHeader.ORIG_CAUSE_VALUE.getCode())].matches("^(16|17|18|19|21|0)$")
				&& !dataArray[headerColumns.indexOf(CdrHeader.DEST_CAUSE_VALUE.getCode())].matches("^(16|17|18|19|21|0)$")
		) {
			return  1;
		} else {
			return 0;
		}
	}
}
