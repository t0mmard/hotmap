import com.beust.jcommander.Parameter;

public class Arguments {
    @Parameter(required = true, arity = 1, names = {"-f", "-filename"}, description = "tiff image to be processed")
    String inputFile;

    @Parameter(required = false, arity = 1, names = {"-a", "-algorithm"}, description = "hotmap algorithm to use")
    AlgorithmType algorithmType = AlgorithmType.NOVEL;

    @Parameter(required = false, arity = 1, names = {"-o", "-output"}, description = "output file")
    String outputFile = "default.jpg"; // TODO kitalálni a legjobb kiterjesztést a célra.
}
