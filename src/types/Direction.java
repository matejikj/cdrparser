/*
 * Copyright (c) 2023. TTC MARCONI s.r.o.
 * All Rights Reserved.
 *
 * All information contained herein is proprietary and confidential
 * to TTC MARCONI s.r.o. Any use, reproduction, or disclosure
 * without the written permission of TTC MARCONI s.r.o is prohibited.
 */
package types;

public class Direction {

    private String direction;
    private String name;

    public Direction(String direction, String name) {
        this.direction = direction;
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Direction: " + direction + ", Name: " + name;
    }
}
