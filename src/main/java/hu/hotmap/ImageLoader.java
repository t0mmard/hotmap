package hu.hotmap;


import hu.hotmap.model.Bands;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;

import java.util.*;

public class ImageLoader {

    Bands loadBands() throws Exception {
        System.out.println("Loading bands:");
        Bands bands = new Bands();

        var img = getClass().getResourceAsStream("/" + HotmapApplication.args.img);

        System.out.println("\tLoading Image raster.");
        TIFFImage tiffImage = TiffReader.readTiff(img);
        List<FileDirectory> directories = tiffImage.getFileDirectories();
        bands.setRaster(directories.get(0).readRasters());
        System.out.println("\tImage raster loaded.\n");

        return bands;
    }

}
