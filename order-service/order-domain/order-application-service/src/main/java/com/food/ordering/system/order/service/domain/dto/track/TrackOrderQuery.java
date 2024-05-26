package com.food.ordering.system.order.service.domain.dto.track;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
@Data
@AllArgsConstructor
public class TrackOrderQuery {

    @NonNull
    private final UUID orderTrackingId;
}
