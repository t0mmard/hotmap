package hu.hotmap;

import hu.hotmap.model.Bands;
import hu.hotmap.model.PixelType;

public class ASEAlgorithm {

    double RADIANCE_MULT_6 = 1.4968E-03;
    double RADIANCE_MULT_7 = 5.0449E-04;

    double RADIANCE_ADD_6 = -7.48376;
    double RADIANCE_ADD_7 = -2.52243;

    public PixelType[][] run(Bands bands) {
        System.out.println("Running ASE algorithm:");

        System.out.println("\tCalculating TOA values.");

        bands.setBand6TOARadiance(new Double[bands.getRaster().getWidth()][bands.getRaster().getHeight()]);
        bands.setBand7TOARadiance(new Double[bands.getRaster().getWidth()][bands.getRaster().getHeight()]);

        for (int x = 0; x<bands.getRaster().getWidth(); ++x) {
            for (int y = 0; y<bands.getRaster().getHeight(); ++y){
                createTOARadiance(bands,x,y);
            }
        }
        System.out.println("\tTOA values calculated.\n");

        var values = new PixelType[bands.getRaster().getWidth()][bands.getRaster().getHeight()];

        System.out.println("\tSearching for hot pixels.\n");
        for (int x = 0; x < values.length; ++x) {
            for (int y = 0; y < values[0].length; ++y) {
                if (calculateHot(bands.getBand6TOARadiance()[x][y],
                        bands.getBand7TOARadiance()[x][y])){
                    values[x][y] = PixelType.Hot;
                }
            }
        }

        clearTOA(bands);

        System.out.println("ASE algorithm finished.\n");

        return values;
    }

    public boolean calculateHot (double band6TOA, double band7TOA) {
        return ((band7TOA - band6TOA) / 0.6) > 0;
    }

    void createTOARadiance(Bands bands, int x, int y){
        bands.getBand6TOARadiance()[x][y] =  bands.getRaster().getPixel(x,y)[1].doubleValue() * RADIANCE_MULT_6 + RADIANCE_ADD_6;
        bands.getBand7TOARadiance()[x][y] =  bands.getRaster().getPixel(x,y)[2].doubleValue() * RADIANCE_MULT_7 + RADIANCE_ADD_7;
    }

    void clearTOA(Bands bands) {
        bands.setBand6TOARadiance(null);
        bands.setBand7TOARadiance(null);
    }
}
