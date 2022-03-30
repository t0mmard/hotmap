package hu.hotmap.model;

import lombok.Data;
import mil.nga.tiff.Rasters;

@Data
public class Bands {
    private Rasters band5Raster;
    private Rasters band6Raster;
    private Rasters band7Raster;

    private Double[][] band5TOA;
    private Double[][] band6TOA;
    private Double[][] band7TOA;

    private double lowerSaturationB6;
    private double upperSaturationB6;

    private double lowerSaturationB7;
    private double upperSaturationB7;
}
