/*
 * Copyright (c) 2023. TTC MARCONI s.r.o.
 * All Rights Reserved.
 *
 * All information contained herein is proprietary and confidential
 * to TTC MARCONI s.r.o. Any use, reproduction, or disclosure
 * without the written permission of TTC MARCONI s.r.o is prohibited.
 */
package types;

public class Rgw {

    private String ip;
    private String rgw;

    public Rgw(String ip, String rgw) {
        this.ip = ip;
        this.rgw = rgw;
    }

    public String getIp() {
        return ip;
    }

    public String getRgw() {
        return rgw;
    }

    @Override
    public String toString() {
        return "IP: " + ip + ", RGW: " + rgw;
    }
}

