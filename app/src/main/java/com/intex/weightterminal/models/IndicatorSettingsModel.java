package com.intex.weightterminal.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class IndicatorSettingsModel {
    private String dataType;
    private int density;
    private String units;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        this.density = density;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndicatorSettingsModel that = (IndicatorSettingsModel) o;
        return density == that.density &&
                Objects.equals(dataType, that.dataType) &&
                Objects.equals(units, that.units);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(dataType, density, units);
    }
}
