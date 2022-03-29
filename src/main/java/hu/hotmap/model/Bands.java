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
}
