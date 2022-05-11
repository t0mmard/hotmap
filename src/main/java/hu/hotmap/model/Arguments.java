package hu.hotmap.model;

import com.beust.jcommander.Parameter;

public class Arguments {
    @Parameter(required = true, arity = 1, names = {"-i", "-img"}, description = "tiff image to be processed")
    public String img;

    @Parameter(arity = 1, names = {"-o", "-output"}, description = "output file")
    public String outputFile = "default";

    @Parameter(names = {"-sc", "-skipClustering"}, description = "Skip clustering (candidates will show up as yellow)")
    public Boolean skipClustering = false;

    @Parameter(names = {"-bg", "-showBackground"}, description = "Skip clustering (candidates will show up as yellow)")
    public Boolean showBackground = false;

    @Parameter(names = {"-n", "-novel"}, description = "Run novel algorithm")
    public Boolean novel = false;

    @Parameter(names = {"-a", "-ase"}, description = "Run ASE algorithm")
    public Boolean ase = false;
}
