package hu.hotmap;

import com.beust.ah.A;
import com.beust.jcommander.JCommander;
import hu.hotmap.model.Arguments;

public class HotmapApplication {

    static Arguments args;

    public static void main(String[] argv) throws Exception{
        initParams(argv);
        System.out.println("Starting application:");
        if (!args.ase && !args.novel) {
            System.err.println("No algorithm selected.");
            return;
        }
        System.out.println("\tProcessing image: " + args.img);
        if (args.novel) System.out.println("\tNovel algorigthm selected");
        if (args.ase) System.out.println("\tASE algorigthm selected");

        System.out.println("\n");

        var imageLoader = new ImageLoader();

        var bands = imageLoader.loadBands();

        NovelAlgorithm novel = new NovelAlgorithm();
        ASEAlgorithm ase = new ASEAlgorithm();

        Printer printer = new Printer();

        if (args.novel) {
            var values = novel.run(bands);
            var img = printer.createBufferedImage(values, bands);
            printer.writeImgToFile(img, args.outputFile + "_novel");
        }
        if (args.ase) {
            var values = ase.run(bands);var img = printer.createBufferedImage(values, bands);
            printer.writeImgToFile(img, args.outputFile + "_ase");
        }


    }

    static void initParams(String[] argv){
        args = new Arguments();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(argv);
    }

}
