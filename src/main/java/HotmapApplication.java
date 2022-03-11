import com.beust.jcommander.JCommander;

public class HotmapApplication {

    static Arguments args;

    public static void main(String[] argv){
        initParams(argv);
    }

    static void initParams(String[] argv){
        args = new Arguments();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(argv);
    }
}
