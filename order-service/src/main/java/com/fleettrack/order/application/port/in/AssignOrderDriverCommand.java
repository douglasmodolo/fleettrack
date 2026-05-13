package com.fleettrack.order.application.port.in;

import lombok.Value;

import java.util.UUID;

@Value
public class AssignOrderDriverCommand {
    UUID id;
    UUID driverId;
}
