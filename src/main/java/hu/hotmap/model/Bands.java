package hu.hotmap.model;

import lombok.Data;
import mil.nga.tiff.Rasters;

@Data
public class Bands {
    private Rasters raster;

    private Double[][] band5TOAReflectance;
    private Double[][] band6TOAReflectance;
    private Double[][] band7TOAReflectance;

    private Double[][] band6TOARadiance;
    private Double[][] band7TOARadiance;
}
