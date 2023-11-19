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
import java.util.Map;

public interface CdrFilter {
	int parse(String[] dataArray, List<String> headerColumns);
}
