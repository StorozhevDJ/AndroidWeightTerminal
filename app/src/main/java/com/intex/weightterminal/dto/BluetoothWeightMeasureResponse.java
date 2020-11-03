package com.intex.weightterminal.dto;

public class BluetoothWeightMeasureResponse {
    private String dataType;
    private int weightTotal;
    private int volume;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getWeightTotal() {
        return weightTotal;
    }

    public void setWeightTotal(int weightTotal) {
        this.weightTotal = weightTotal;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
