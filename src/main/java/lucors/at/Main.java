package lucors.at;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Parameters are required.\nExample: image1.png image2.png result.png");
            return;
        }
        BufferedImage image1 = ImageIO.read(new File(args[0]));
        BufferedImage image2 = ImageIO.read(new File(args[1]));
        ImageComparator.ImageCompareResult result = ImageComparator.compareImages(image1, image2);
        System.out.println(result.toString());

        String resultFilename = "result.png";
        if (args.length > 2) {
            resultFilename = args[2];
        }
        String extension = "png";
        int i = resultFilename.lastIndexOf('.');
        if (i > 0) extension = resultFilename.substring(i+1);
        // Сохраняем результирующее изображение
        ImageIO.write(result.getMask(), extension, new File(resultFilename));
    }
}