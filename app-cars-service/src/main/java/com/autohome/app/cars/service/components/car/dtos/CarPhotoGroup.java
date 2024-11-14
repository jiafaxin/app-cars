package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class CarPhotoGroup {
    int specId;
    int showId;
    int count;
    Set<Integer> colorSet;
    List<CarPhotoDto> items;
}
