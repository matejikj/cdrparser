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
	FROM("from", "Z čísla"),
	TO("to", "Na číslo"),
	AREA("area", "Oblast"),
	DIRECTION("direction", "Směr volání"),
	RGW_HEADER("rgw", "Radio-gateway ip"),
	CONNECTED_TIME("connectedTime", "Začátek hovoru"),
	CALL_END_TIME("callEndTime", "Konec hovoru"),
	EXIT_CODE("exitCode", "Exit code"),
	RGW_NAME("rgw-name", "Radio-gateway jméno"),
	MESSAGE("message", "Výstup"),
	SUCCESS("success", "Spojený hovor"),
	DURATION("duration", "Délka hovoru"),
	DAY("day", "Den v týdnu");

	private final String code;
	private final String translation;

	RgwHeader(String code, String translation) {
		this.code = code;
		this.translation = translation;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getTranslation() {
		return translation;
	}
}
