package hu.hotmap;

import hu.hotmap.model.Bands;
import hu.hotmap.model.PixelType;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class NovelAlgorithm {

    double REFLECTANCE_MULT_5 = 2.0000E-05;
    double REFLECTANCE_MULT_6 = 2.0000E-05;
    double REFLECTANCE_MULT_7 = 2.0000E-05;

    double REFLECTANCE_ADD_5 = -0.100000;
    double REFLECTANCE_ADD_6 = -0.100000;
    double REFLECTANCE_ADD_7 = -0.100000;

    public PixelType[][] run(Bands bands) {

        System.out.println("Running novel algorithm:");
        System.out.println("\tCalculating TOA reflectance values.");

        bands.setBand5TOAReflectance(new Double[bands.getRaster().getWidth()][bands.getRaster().getHeight()]);
        bands.setBand6TOAReflectance(new Double[bands.getRaster().getWidth()][bands.getRaster().getHeight()]);
        bands.setBand7TOAReflectance(new Double[bands.getRaster().getWidth()][bands.getRaster().getHeight()]);

        for (int x = 0; x<bands.getRaster().getWidth(); ++x) {
            for (int y = 0; y<bands.getRaster().getHeight(); ++y){
                createTOAReflectance(bands,x,y);
            }
        }
        System.out.println("\tTOA values calculated.\n");

        int hot = 0;
        int candidate = 0;
        var values = new PixelType[bands.getRaster().getWidth()][bands.getRaster().getHeight()];
        System.out.println("\tSearching for alpha and beta values.\n");
        for (int x = 0; x < values.length; ++x) {
            for (int y = 0; y < values[0].length; ++y) {
                if (calculateAlpha(bands.getBand5TOAReflectance()[x][y], bands.getBand6TOAReflectance()[x][y], bands.getBand7TOAReflectance()[x][y])){
                    values[x][y] = PixelType.Hot;
                    hot++;
                }
                else if (calculateBeta(bands.getBand5TOAReflectance()[x][y], bands.getBand6TOAReflectance()[x][y],
                        bands.getRaster().getPixel(x,y)[1].doubleValue(), bands.getRaster().getPixel(x,y)[2].doubleValue())) {
                    values[x][y] = PixelType.Candidate;
                    candidate++;
                }
            }
        }
        System.out.println("\tAlpha and Beta values calculated:");
        System.out.println("\t\tAlpha pixels: " + hot);
        System.out.println("\t\tBeta pixels: " + candidate + "\n");
        if (!HotmapApplication.args.skipClustering) {
            ArrayList<Integer> clustersToKeep = new ArrayList<>();
            AtomicInteger currentCluster = new AtomicInteger(1);
            System.out.println("\t Creating clusters:");
            var cluster = createCluster(values, clustersToKeep, currentCluster);
            System.out.println("\t\tNumber of clusters:" + currentCluster);
            System.out.println("\t\tNumber of non-associated false alarms (clusters):" + (currentCluster.get() - clustersToKeep.size()) + "\n");
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

        clearTOA(bands);

        System.out.println("Novel algorithm finished.\n");

        return values;
    }

    void clearTOA(Bands bands) {
        bands.setBand5TOAReflectance(null);
        bands.setBand6TOAReflectance(null);
        bands.setBand7TOAReflectance(null);
    }

    void createTOAReflectance(Bands bands, int x, int y){
        bands.getBand5TOAReflectance()[x][y] = bands.getRaster().getPixel(x,y)[0].doubleValue() * REFLECTANCE_MULT_5 + REFLECTANCE_ADD_5;
        bands.getBand6TOAReflectance()[x][y] = bands.getRaster().getPixel(x,y)[1].doubleValue() * REFLECTANCE_MULT_6 + REFLECTANCE_ADD_6;
        bands.getBand7TOAReflectance()[x][y] = bands.getRaster().getPixel(x,y)[2].doubleValue() * REFLECTANCE_MULT_7 + REFLECTANCE_ADD_7;
    }

    public boolean calculateAlpha (double band5TOA, double band6TOA, double band7TOA) {
        return (band7TOA/band6TOA >= 1.4) && (band7TOA/band5TOA >= 1.4) && (band7TOA >= 0.15);
    }

    public boolean calculateBeta (double band5TOA, double band6TOA, double band6DN, double band7DN) {
        return ((band6TOA/band5TOA >= 2) && (band6TOA == 0.5) || (band6DN == 65535) || (band7DN >=  65535)) || (band6DN == 0) || (band7DN == 0);
    }
    /* A max vagy a null értéket vesszük túlszaturáltnak:
    When the detectors in a sensor view an object that is too bright, they record a flat value of 255 in the 8-bit data from the Landsat satellites (known as saturation). However, when the object viewed is much brighter than the sensor can handle, a semiconductor effect in the detectors causes an artifact known as Oversaturation.

    In general, Oversaturation causes the detector to cease operating correctly for a short time, returning null values (or occasionally, quick oscillations between 0 and 255). Sometimes, a crosstalk effect occurs, creating brief periods of noise in other non-saturated detectors in the affected band.
    https://www.usgs.gov/landsat-missions/oversaturation#:~:text=When%20the%20detectors%20in%20a,satellites%20(known%20as%20saturation).
    */

    //A magas felbontású képek miatt növelni kell a stack méretét (-Xss1024m)
    public Integer[][] createCluster(PixelType[][] values, ArrayList<Integer> clustersToKeep, AtomicInteger currentCluster) {
        Integer[][] cluster = new Integer[values.length][values[0].length];
        for (int x = 0; x < cluster.length; ++x) {
            for (int y = 0; y < cluster[0].length; ++y) {
                if (values[x][y] != null && cluster[x][y] == null) {
                    dfs(cluster, values, x, y, currentCluster.get(), clustersToKeep);
                    currentCluster.getAndIncrement();
                }
            }
        }
        return cluster;
    }

    private void dfs(Integer[][] cluster, PixelType[][] values, int x,int y, int currentCluster, ArrayList<Integer> clustersToKeep){
        var stack = new Stack<Pair<Integer,Integer>>();

        var starter = new Pair<Integer,Integer>(x,y);
        stack.push(starter);

        while (!stack.isEmpty()) {
            var obj = stack.pop();

            if(obj.getKey() < 0 || obj.getKey() == cluster.length || obj.getValue() < 0 || obj.getValue() == cluster[0].length ||
                    values[obj.getKey()][obj.getValue()] == null || cluster[obj.getKey()][obj.getValue()] != null){
                continue;
            }
            else if (values[obj.getKey()][obj.getValue()] == PixelType.Hot && !clustersToKeep.contains(currentCluster)){
                clustersToKeep.add(currentCluster);
            }
            cluster[obj.getKey()][obj.getValue()] = currentCluster;
            stack.push(new Pair<Integer,Integer>(obj.getKey(), obj.getValue()+1));
            stack.push(new Pair<Integer,Integer>(obj.getKey(), obj.getValue()-1));
            stack.push(new Pair<Integer,Integer>(obj.getKey()+1, obj.getValue()));
            stack.push(new Pair<Integer,Integer>(obj.getKey()-1, obj.getValue()));
        }
//        if(x < 0 || x == cluster.length || y < 0 || y == cluster[0].length || values[x][y] == null || cluster[x][y] != null) return;
//        if (values[x][y] == PixelType.Hot && !clustersToKeep.contains(currentCluster)) clustersToKeep.add(currentCluster);
//        cluster[x][y] = currentCluster;
//        dfs(cluster, values,x-1,y, currentCluster, clustersToKeep);
//        dfs(cluster,values, x+1,y, currentCluster, clustersToKeep);
//        dfs(cluster,values, x,y-1, currentCluster, clustersToKeep);
//        dfs(cluster,values, x,y+1, currentCluster, clustersToKeep);
    }

}
