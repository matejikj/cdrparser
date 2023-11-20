/*
 * Copyright (c) 2023. TTC MARCONI s.r.o.
 * All Rights Reserved.
 *
 * All information contained herein is proprietary and confidential
 * to TTC MARCONI s.r.o. Any use, reproduction, or disclosure
 * without the written permission of TTC MARCONI s.r.o is prohibited.
 */
package filters;

import java.util.List;
import java.util.Map;

import static configs.CdrHeader.*;

public class IncomingFromMobileFilter implements CdrFilter {

	@Override
	public int parse(String[] dataArray, List<String> headerColumns) {
		if (dataArray[headerColumns.indexOf(DEST_DEVICE_NAME.getCode())].startsWith("SEP")
				&& (!"Radio_Controller_1".equals(dataArray[headerColumns.indexOf(ORIG_DEVICE_NAME.getCode())])
				|| dataArray[headerColumns.indexOf(ORIG_DEVICE_NAME.getCode())] == null)
				&& Integer.parseInt(dataArray[headerColumns.indexOf(DURATION.getCode())]) > 0
				&& dataArray[headerColumns.indexOf(CALLING_PARTY_NUMBER.getCode())].matches("^(06|07)\\d{8}$")
		) {
			return  1;
		} else {
			return 0;
		}
	}
}
