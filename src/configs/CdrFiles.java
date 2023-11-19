/*
 * Copyright (c) 2023. TTC MARCONI s.r.o.
 * All Rights Reserved.
 *
 * All information contained herein is proprietary and confidential
 * to TTC MARCONI s.r.o. Any use, reproduction, or disclosure
 * without the written permission of TTC MARCONI s.r.o is prohibited.
 */
package configs;

public enum CdrFiles {
	AREAS("area"),
	EXIT_CODES("exitCode"),
	RGW("rgw"),
	DIRECTIONS("direction"),
	TERMINALS("terminals"),
	COLUMNS("rgw_columns");

	private final String description;

	CdrFiles(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}

