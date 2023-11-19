/*
 * Copyright (c) 2023. TTC MARCONI s.r.o.
 * All Rights Reserved.
 *
 * All information contained herein is proprietary and confidential
 * to TTC MARCONI s.r.o. Any use, reproduction, or disclosure
 * without the written permission of TTC MARCONI s.r.o is prohibited.
 */
package configs;

public enum RgwHeader implements Header {
	FROM_HEADER("Z čísla"),
	TO_HEADER("Na číslo"),
	AREA_HEADER("Oblast"),
	DIRECTION_HEADER("Směr volání"),
	RGW_HEADER_HEADER("Radio-gateway ip"),
	CONNECTED_TIME_HEADER("Začátek hovoru"),
	CALL_END_TIME_HEADER("Konec hovoru"),
	EXIT_CODE_HEADER("Exit code"),
	RGW_NAME_HEADER("Radio-gateway jméno"),
	MESSAGE_HEADER("Výstup"),
	USPECH_HEADER("Spojený hovor"),
	DURATION_HEADER("Délka hovoru"),
	DAY_HEADER("Den v týdnu"),
	FROM("from"),
	TO("to"),
	AREA("area"),
	DIRECTION("direction"),
	RGW_HEADER("rgw"),
	CONNECTED_TIME("connectedTime"),
	CALL_END_TIME("callEndTime"),
	EXIT_CODE("exitCode");

	private final String description;

	RgwHeader(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
