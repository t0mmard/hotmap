package hu.hotmap;


import hu.hotmap.model.Bands;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;

import java.util.*;

public class ImageLoader {

    double REFLECTANCE_MULT_5 = 2.0000E-05;
    double REFLECTANCE_MULT_6 = 2.0000E-05;
    double REFLECTANCE_MULT_7 = 2.0000E-05;

    double REFLECTANCE_ADD_5 = -0.100000;
    double REFLECTANCE_ADD_6 = -0.100000;
    double REFLECTANCE_ADD_7 = -0.100000;

    Bands loadBands() throws Exception {
        System.out.println("Loading bands:");
        Bands bands = new Bands();

        var b5stream = getClass().getResourceAsStream("/" + HotmapApplication.args.b5);
        var b6stream = getClass().getResourceAsStream("/" + HotmapApplication.args.b6);
        var b7stream = getClass().getResourceAsStream("/" + HotmapApplication.args.b7);

        System.out.println("\tLoading Band5 raster.");
        TIFFImage tiffImage = TiffReader.readTiff(b5stream);
        List<FileDirectory> directories = tiffImage.getFileDirectories();
        bands.setBand5Raster(directories.get(0).readRasters());
        System.out.println("\tBand5 raster loaded.\n");

        System.out.println("\tLoading Band6 raster.");
        tiffImage = TiffReader.readTiff(b6stream);
        directories = tiffImage.getFileDirectories();
        bands.setBand6Raster(directories.get(0).readRasters());
        System.out.println("\tBand6 raster loaded.\n");

        System.out.println("\tLoading Band7 raster.");
        tiffImage = TiffReader.readTiff(b7stream);
        directories = tiffImage.getFileDirectories();
        bands.setBand7Raster(directories.get(0).readRasters());
        System.out.println("\tBand7 raster loaded.\n");

        bands.setBand5TOA(new Double[bands.getBand5Raster().getWidth()][bands.getBand5Raster().getHeight()]);
        bands.setBand6TOA(new Double[bands.getBand6Raster().getWidth()][bands.getBand6Raster().getHeight()]);
        bands.setBand7TOA(new Double[bands.getBand7Raster().getWidth()][bands.getBand7Raster().getHeight()]);

        System.out.println("\tCalculating TOA values.");
        for (int x = 0; x<bands.getBand5Raster().getWidth(); ++x) {
            for (int y = 0; y<bands.getBand5Raster().getHeight(); ++y){
                createTOA(bands,x,y);
            }
        }
        System.out.println("\tTOA values calculated.\n");
        return bands;
    }

    void createTOA(Bands bands, int x, int y){
        bands.getBand5TOA()[x][y] = bands.getBand5Raster().getPixel(x,y)[0].doubleValue() * REFLECTANCE_MULT_5 + REFLECTANCE_ADD_5;
        bands.getBand6TOA()[x][y] =  bands.getBand6Raster().getPixel(x,y)[0].doubleValue() * REFLECTANCE_MULT_6 + REFLECTANCE_ADD_6;
        bands.getBand7TOA()[x][y] =  bands.getBand7Raster().getPixel(x,y)[0].doubleValue() * REFLECTANCE_MULT_7 + REFLECTANCE_ADD_7;
    }

    /*void getPercentiles(Bands bands) {
        ArrayList<Double> flatArray = new ArrayList<>();
        for (Double[] array : bands.getBand6TOA()) {
            flatArray.addAll(Arrays.asList(array));
        }
        Collections.sort(flatArray);
        Collections.reverse(flatArray);
        bands.setUpperSaturationB6(flatArray.get((int) Math.round(1 / 1000.0 * (flatArray.size() - 1))));

        flatArray = new ArrayList<>();
        for (Double[] array : bands.getBand7TOA()) {
            flatArray.addAll(Arrays.asList(array));
        }
        Collections.sort(flatArray);
        Collections.reverse(flatArray);
        bands.setUpperSaturationB7(flatArray.get((int) Math.round(1 / 1000.0 * (flatArray.size() - 1))));

    }*/

}
