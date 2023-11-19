/*
 * Copyright (c) 2023. TTC MARCONI s.r.o.
 * All Rights Reserved.
 *
 * All information contained herein is proprietary and confidential
 * to TTC MARCONI s.r.o. Any use, reproduction, or disclosure
 * without the written permission of TTC MARCONI s.r.o is prohibited.
 */
package types;

public class ExitCode {

    // Private fields for the properties
    private String exitCode;
    private String message;
    private String success;

    public ExitCode(String exitCode, String message, String success) {
        this.exitCode = exitCode;
        this.message = message;
        this.success = success;
    }

    public String getExitCode() {
        return exitCode;
    }

    public String getMessage() {
        return message;
    }

    public String getSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "Exit Code: " + exitCode + ", Message: " + message + ", Success: " + success;
    }
}

