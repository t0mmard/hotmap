package hu.hotmap;

import com.beust.jcommander.JCommander;
import hu.hotmap.model.Arguments;

public class HotmapApplication {

    static Arguments args;

    public static void main(String[] argv) throws Exception{
        initParams(argv);
        System.out.println("Starting application:");
        System.out.println("\tband5: " + args.b5 + ", band6: " + args.b6 + ", band7: " + args.b7 + "\n");
        var imageLoader = new ImageLoader();

        var bands = imageLoader.loadBands();

        NovelAlgorithm algorithm = new NovelAlgorithm();
        var values = algorithm.run(bands);

        Printer printer = new Printer();
        var img = printer.createBufferedImage(values, bands);
        printer.writeImgToFile(img, args.outputFile);
    }

    static void initParams(String[] argv){
        args = new Arguments();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(argv);
    }

}
