package com.intex.weightterminal.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class WeightModel {
    private float weightTotal;
    private float volume;

    public float getWeightTotal() {
        return weightTotal;
    }

    public void setWeightTotal(float weightTotal) {
        this.weightTotal = weightTotal;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeightModel that = (WeightModel) o;
        return Float.compare(that.weightTotal, weightTotal) == 0 &&
                Float.compare(that.volume, volume) == 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(weightTotal, volume);
    }
}
