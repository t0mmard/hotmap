package hu.hotmap;

import hu.hotmap.model.PixelType;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Printer {

    BufferedImage createBufferedImage(PixelType[][] values) {
        System.out.println("Creating image.");
        var img = new BufferedImage(values.length, values[0].length, BufferedImage.TYPE_INT_BGR);
        for (int x = 0; x < img.getWidth(); ++x) {
            for (int y = 0; y < img.getHeight(); ++y) {
                if (values[x][y] == null) {
                    img.setRGB(x,y, Color.BLACK.getRGB());
                }
                else if ( values[x][y] == PixelType.Hot) {
                    img.setRGB(x,y, Color.RED.getRGB());
                }
                else if ( values[x][y] == PixelType.Candidate) {
                    img.setRGB(x,y, Color.YELLOW.getRGB());
                }
            }
        }
        System.out.println("Image created.\n");
        return img;
    }

    void writeImgToFile(BufferedImage img, String filename) {
        try {
            var output = new File("src/main/resources/" + filename + ".jpg");
            output.createNewFile();
            ImageIO.write(img, "jpg", output);
            System.out.println(filename + ".jpg saved.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}