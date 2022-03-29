package hu.hotmap.model;

import com.beust.jcommander.Parameter;

public class Arguments {
    @Parameter(required = true, arity = 1, names = {"-b5", "-band5"}, description = "band5 tiff image to be processed")
    public String b5;

    @Parameter(required = true, arity = 1, names = {"-b6", "-band6"}, description = "band6 tiff image to be processed")
    public String b6;

    @Parameter(required = true, arity = 1, names = {"-b7", "-band7"}, description = "band7 tiff image to be processed")
    public String b7;

//    @Parameter(required = true, arity = 1, names = {"-m", "-mtl"}, description = "mtl file to calculate TOA")
//    public String mtl; TODO? a reflectance correction értékek konstansok?

    @Parameter(arity = 1, names = {"-a", "-algorithm"}, description = "hotmap algorithm to use")
    public AlgorithmType algorithmType = AlgorithmType.NOVEL;

    @Parameter(arity = 1, names = {"-o", "-output"}, description = "output file")
    public String outputFile = "default";

    @Parameter(names = {"-sc", "-skipClustering"}, description = "Skip clustering (candidates will show up as yellow)")
    public Boolean skipClustering = false;
}
