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

	ORIG_CAUSE_CDR_HEADER("origCause_value"),
	ORIG_CALLED_CDR_HEADER("originalCalledPartyNumber"),
	FINAL_CALLED_CDR_HEADER("finalCalledPartyNumber"),
	DEST_CAUSE_CDR_HEADER("destCause_value"),
	ORIG_CALLED_PARTITION_CDR_HEADER("originalCalledPartyNumberPartition"),
	DURATION_CDR_HEADER("duration"),
	ORIG_DEVICE_CDR_HEADER("origDeviceName"),
	DEST_DEVICE_CDR_HEADER("destDeviceName"),
	ORIG_IP_V6_CDR_HEADER("origIpv4v6Addr"),
	DEST_IP_V4_CDR_HEADER("destIpv4v6Addr"),
	ORIG_IP_CDR_HEADER("origIpAddr"),
	CALLING_PARTY_CDR_HEADER("callingPartyNumber"),
	DEST_IP_CDR_HEADER("destIpAddr"),
	TIME_CONNECT_CDR_HEADER("dateTimeConnect"),
	TIME_DISCONNECT_CDR_HEADER("dateTimeDisconnect"),

	TERMINAL_CDR_HEADER("terminál"),
	TERMINAL_IP_CDR_HEADER("terminál-ip"),
	HOSTNAME_CDR_HEADER("hostName"),
	TYPE_CDR_HEADER("typ"),
	LOCALITY_CDR_HEADER("lokalita"),
	AREA_CDR_HEADER("oblast"),
	DAY_CDR_HEADER("den"),
	INCOMING_TERMINAL_FROM_MOBILE("příchozí volání z mobilu"),
	OUTGOING_TERMINAL_TO_MOBILE("odchozí volání na mobil"),
	INCOMING_TERMINAL_FROM_TRANSMITTER("příchozí volání z vysílačky"),
	OUTGOING_TERMINAL_TO_TRANSMITTER("odchozí volání na vysílačku"),
	INCOMING_TERMINAL_FROM_OTHERS("příchozí volání z pevné linky"),
	OUTGOING_TERMINAL_TO_OTHERS("odchozí volání na pevnou linku"),
	MISSED_FROM_MOBILE("nepřijaté hovory z mobilu"),
	MISSED_ON_MOBILE("nevyzvednuté hovory mobilem"),
	MISSED_FROM_TRANSMITTER("nepřijaté hovory z vysílačky"),
	MISSED_ON_TRANSMITTER("nevyzvednuté hovory vysílačkou"),
	MISSED_FROM_OTHERS("nepřijaté hovory z pevné linky"),
	MISSED_ON_OTHERS("nevyzvednuté hovory pevnou linkou"),
	FAIL_TO_MOBILE("neúspěšné volání na mobil"),
	FAIL_FROM_MOBILE("neúspěšné volání z mobilu"),
	FAIL_TO_TRANSMITTER("neúspěšné volání na vysílačku"),
	FAIL_FROM_TRANSMITTER("neúspěšné volání z vysílačky"),
	FAIL_TO_LANDLINE("neúspěšné volání na pevnou linku"),
	FAIL_FROM_LANDLINE("neúspěšné volání z pevné linky");

	private final String description;

	CdrHeader(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
