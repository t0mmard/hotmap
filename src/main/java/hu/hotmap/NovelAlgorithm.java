package hu.hotmap;

import hu.hotmap.model.Bands;
import hu.hotmap.model.PixelType;

import java.util.ArrayList;

public class NovelAlgorithm {
    public PixelType[][] run(Bands bands) {
        System.out.println("Running novel algorithm:");
        int hot = 0;
        int candidate = 0;
        var values = new PixelType[bands.getBand5Raster().getWidth()][bands.getBand5Raster().getHeight()];
        System.out.println("\tSearching for alpha and beta values.\n");
        for (int x = 0; x < values.length; ++x) {
            for (int y = 0; y < values[0].length; ++y) {
                if (calculateAlpha(bands.getBand5TOA()[x][y], bands.getBand6TOA()[x][y], bands.getBand7TOA()[x][y])){
                    values[x][y] = PixelType.Hot;
                    hot++;
                }
                else if (calculateBeta(bands.getBand5TOA()[x][y], bands.getBand6TOA()[x][y],
                        bands.getBand6Raster().getPixel(x,y)[0].doubleValue(), bands.getBand7Raster().getPixel(x,y)[0].doubleValue())) {
                    values[x][y] = PixelType.Candidate;
                    candidate++;
                }
            }
        }
        System.out.println("\tAlpha and Beta values calculated:");
        System.out.println("\t\tAlpha pixels: " + hot);
        System.out.println("\t\tBeta pixels: " + candidate + "\n");
        if (!HotmapApplication.args.skipClustering) {
            Integer[][] cluster = new Integer[values.length][values[0].length];
            ArrayList<Integer> clustersToKeep = new ArrayList<>();
            Integer currentCluster = 0;
            System.out.println("\t Creating clusters:");
            for (int x = 0; x < cluster.length; ++x) {
                for (int y = 0; y < cluster[0].length; ++y) {
                    if (values[x][y] != null) {
                        if (x == 0 && y == 0) {
                            cluster[x][y] = ++currentCluster;
                        } else if (x == 0 && y < cluster[0].length - 1) {
                            if (cluster[x][y - 1] != null) {
                                cluster[x][y] = cluster[x][y - 1];
                            } else {
                                cluster[x][y] = ++currentCluster;
                            }
                        } else if (x == 0 && y == cluster[0].length - 1) {
                            if (cluster[x][y-1] != null) {
                                cluster[x][y] = cluster[x][y-1];
                            } else {
                                cluster[x][y] = ++currentCluster;
                            }
                        } else if (x > 0 && y == 0 && x < cluster.length -1) {
                            if (cluster[x - 1][y] != null) {
                                cluster[x][y] = cluster[x - 1][y];
                            } else {
                                cluster[x][y] = ++currentCluster;
                            }
                        } else if (y == cluster[0].length - 1 && x < cluster.length - 1) {
                            if (cluster[x-1][y-1] != null){
                                cluster[x][y] = cluster[x-1][y-1];
                            } else if (cluster[x - 1][y] != null) {
                                cluster[x][y] = cluster[x - 1][y];
                            } else if (cluster[x][y - 1] != null) {
                                cluster[x][y] = cluster[x][y - 1];
                            } else {
                                cluster[x][y] = ++currentCluster;
                            }
                        } else if (y == 0 && x == cluster.length - 1) {
                            if (cluster[x - 1][y] != null) {
                                cluster[x][y] = cluster[x - 1][y];
                            } else {
                                cluster[x][y] = ++currentCluster;
                            }
                        } else if (x == cluster.length - 1 && y < cluster[0].length - 1) {
                            if (cluster[x-1][y-1] != null){
                                cluster[x][y] = cluster[x-1][y-1];
                            }
                            else if (cluster[x - 1][y] != null) {
                                cluster[x][y] = cluster[x - 1][y];
                            }else if (cluster[x - 1][y + 1] != null) {
                                cluster[x][y] = cluster[x - 1][y + 1];
                            } else if (cluster[x][y - 1] != null) {
                                cluster[x][y] = cluster[x][y - 1];
                            } else {
                                cluster[x][y] = ++currentCluster;
                            }
                        } else if (y == cluster[0].length - 1 && x == cluster.length - 1) {
                            if (cluster[x-1][y-1] != null) {
                                cluster[x][y] = cluster[x-1][y-1];
                            }
                            else if (cluster[x - 1][y] != null) {
                                cluster[x][y] = cluster[x - 1][y];
                            } else if (cluster[x][y - 1] != null) {
                                cluster[x][y] = cluster[x][y - 1];
                            } else {
                                cluster[x][y] = ++currentCluster;
                            }
                        } else if (x > 0 && y > 0 && x < cluster.length - 1 && y < cluster[0].length - 1) {
                            if (cluster[x-1][y-1] != null){
                                cluster[x][y] = cluster[x-1][y-1];
                            } else if (cluster[x - 1][y] != null) {
                                cluster[x][y] = cluster[x - 1][y];
                            } else if (cluster[x - 1][y + 1] != null) {
                                cluster[x][y] = cluster[x - 1][y + 1];
                            } else if (cluster[x][y - 1] != null) {
                                cluster[x][y] = cluster[x][y - 1];
                            } else {
                                cluster[x][y] = ++currentCluster;
                            }
                        }

                        //keep cluster if it has alpha value
                        if (values[x][y] == PixelType.Hot && !clustersToKeep.contains(currentCluster)) {
                            clustersToKeep.add(currentCluster);
                        }
                    }
                }
            }
            System.out.println("\t\tNumber of clusters:" + currentCluster);
            System.out.println("\t\tNumber of non-associated false alarms (clusters):" + (currentCluster - clustersToKeep.size()) + "\n");

            System.out.println("\t Demoting and promoting pixels:");
            int demotions = 0;
            int promotions = 0;
            for (int x = 0; x < cluster.length; ++x) {
                for (int y = 0; y < cluster[0].length; ++y) {
                    if (!clustersToKeep.contains(cluster[x][y]) && values[x][y] != null) {
                        values[x][y] = null;
                        demotions++;
                    } else if (values[x][y] == PixelType.Candidate) {
                        values[x][y] = PixelType.Hot;
                        promotions++;
                    }
                }
            }
            System.out.println("\t\tPixels promoted: " + promotions);
            System.out.println("\t\tPixels demoted: " + demotions + "\n");
        } else {
            System.out.println("Clustering skipped.\n");
        }

        System.out.println("Novel algorithm finished.\n");

        return values;
    }

    public boolean calculateAlpha (double band5TOA, double band6TOA, double band7TOA) {
        return (band7TOA/band6TOA >= 1.4) && (band7TOA/band5TOA >= 1.4) && (band7TOA >= 0.15);
    }

    public boolean calculateBeta (double band5TOA, double band6TOA, double band6DN, double band7DN) {
        return ((band6TOA/band5TOA >= 2) && (band6TOA == 0.5) || (band6DN == 65535) || (band7DN >=  65535) || (band6DN == 0) || (band7DN == 0));
    }
    /* A max vagy a null értéket vesszük túlszaturáltnak:
    When the detectors in a sensor view an object that is too bright, they record a flat value of 255 in the 8-bit data from the Landsat satellites (known as saturation). However, when the object viewed is much brighter than the sensor can handle, a semiconductor effect in the detectors causes an artifact known as Oversaturation.

    In general, Oversaturation causes the detector to cease operating correctly for a short time, returning null values (or occasionally, quick oscillations between 0 and 255). Sometimes, a crosstalk effect occurs, creating brief periods of noise in other non-saturated detectors in the affected band.
    https://www.usgs.gov/landsat-missions/oversaturation#:~:text=When%20the%20detectors%20in%20a,satellites%20(known%20as%20saturation).
    */

}
