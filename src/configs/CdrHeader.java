/*
 * Copyright (c) 2023. TTC MARCONI s.r.o.
 * All Rights Reserved.
 *
 * All information contained herein is proprietary and confidential
 * to TTC MARCONI s.r.o. Any use, reproduction, or disclosure
 * without the written permission of TTC MARCONI s.r.o is prohibited.
 */
package configs;

public enum CdrHeader implements Header {

	ORIG_CALLED_PARTY_NUMBER("originalCalledPartyNumber", "Z čísla"),
	CALLING_PARTY_NUMBER("callingPartyNumber", "Na číslo"),
	FINAL_CALLED_PARTY_NUMBER("finalCalledPartyNumber", "Na číslo finální"),
	ORIG_IP_ADDR("origIpAddr", "IP adresa Z"),
	DEST_IP_ADDR("destIpAddr", "IP adresa Na"),
	ORIG_CAUSE_VALUE("origCause_value", "Ukončení Z (cause value)"),
	DEST_CAUSE_VALUE("destCause_value", "Ukončení Na"),
	DATETIME_CONNECT("dateTimeConnect", "Začátek hovoru"),
	DATETIME_DISCONNECT("dateTimeDisconnect", "Konec hovoru"),
	DURATION("duration", "Délka hovoru"),
	ORIG_DEVICE_NAME("origDeviceName", "Zařízení Z"),
	DEST_DEVICE_NAME("destDeviceName", "Zařízení Na"),
	TERMINAL("terminal", "Terminál"),
	TERMINAL_IP("terminal_ip", "IP terminál"),
	HOSTNAME("hostname", "HostName"),
	DAY("day", "Den v týdnu"),

	INCOMING_TERMINAL_FROM_MOBILE("incoming_terminal_from_mobile", "příchozí volání z mobilu"),
	OUTGOING_TERMINAL_TO_MOBILE("outgoing_terminal_to_mobile", "odchozí volání na mobil"),
	INCOMING_TERMINAL_FROM_TRANSMITTER("incoming_terminal_from_transmitter", "příchozí volání z vysílačky"),
	OUTGOING_TERMINAL_TO_TRANSMITTER("outgoing_terminal_to_transmitter", "odchozí volání na vysílačku"),
	INCOMING_TERMINAL_FROM_OTHERS("incoming_terminal_from_others", "příchozí volání z pevné linky"),
	OUTGOING_TERMINAL_TO_OTHERS("outgoing_terminal_to_others", "odchozí volání na pevnou linku"),
	MISSED_FROM_MOBILE("missed_from_mobile", "nepřijaté hovory z mobilu"),
	MISSED_ON_MOBILE("missed_on_mobile", "nevyzvednuté hovory mobilem"),
	MISSED_FROM_TRANSMITTER("missed_from_transmitter", "nepřijaté hovory z vysílačky"),
	MISSED_ON_TRANSMITTER("missed_on_transmitter", "nevyzvednuté hovory vysílačkou"),
	MISSED_FROM_OTHERS("missed_from_others", "nepřijaté hovory z pevné linky"),
	MISSED_ON_OTHERS("missed_on_others", "nevyzvednuté hovory pevnou linkou"),
	FAIL_TO_MOBILE("fail_to_mobile", "neúspěšné volání na mobil"),
	FAIL_FROM_MOBILE("fail_from_mobile", "neúspěšné volání z mobilu"),
	FAIL_TO_TRANSMITTER("fail_to_transmitter", "neúspěšné volání na vysílačku"),
	FAIL_FROM_TRANSMITTER("fail_from_transmitter", "neúspěšné volání z vysílačky"),
	FAIL_TO_LANDLINE("fail_to_landline", "neúspěšné volání na pevnou linku"),
	FAIL_FROM_LANDLINE("fail_from_landline", "neúspěšné volání z pevné linky");

	private final String code;
	private final String translation;

	CdrHeader(String code, String translation) {
		this.code = code;
		this.translation = translation;
	}

	public String getCode() {
		return code;
	}
	public String getTranslation() {
		return translation;
	}
}
