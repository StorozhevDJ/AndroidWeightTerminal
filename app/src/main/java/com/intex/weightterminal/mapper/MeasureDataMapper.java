package com.intex.weightterminal.mapper;

import com.intex.weightterminal.dto.BluetoothWeightMeasureResponse;
import com.intex.weightterminal.models.WeightModel;

public class MeasureDataMapper {

    public static WeightModel convertToEntity(BluetoothWeightMeasureResponse dto) {
        if (dto == null) return null;
        WeightModel weightModel = new WeightModel();
        weightModel.setWeightTotal((float) dto.getWeightTotal() / 100);
        weightModel.setVolume(dto.getVolume());
        return weightModel;
    }
}
