package lucors.at;

import lombok.Builder;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static java.lang.String.format;

public class ImageComparator {

    @Getter
    @Builder
    public static class ImageCompareResult {
        private BufferedImage mask;
        private long diffPixelsCount;
        private double diffPixelsPercentage;

        public boolean asBoolean() {
            return diffPixelsCount > 0;
        }

        @Override
        public String toString() {
            return format("Result: %b\nPixels diffs count: %s\nPixels diffs percentage: %3.3f\n",
                    asBoolean(), diffPixelsCount, diffPixelsPercentage * 100);
        }
    }

    private static final boolean DEBUG = Objects.equals(System.getenv("DEBUG"), "true");

    public static BufferedImage[] resize(BufferedImage... images) throws IOException {
        if (images.length < 2) return images;

        int maxWidth = 0;
        int maxHeight = 0;
        for (BufferedImage image : images) {
            if (image.getWidth() > maxWidth) maxWidth = image.getWidth();
            if (image.getHeight() > maxHeight) maxHeight = image.getHeight();
        }

        BufferedImage[] result = new BufferedImage[images.length];
        for (int i = 0; i < images.length; i++) {
            BufferedImage resizedImage = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g1 = resizedImage.createGraphics();
            g1.drawImage(images[i], 0, 0, images[i].getWidth(), images[i].getHeight(), null);
            g1.dispose();
            result[i] = resizedImage;

            if (DEBUG) {
                ImageIO.write(resizedImage, "png", new File(format("resizedImage%d.png", i)));
            }
        }
        return result;
    }

    public static ImageCompareResult compareImages(BufferedImage image1, BufferedImage image2) throws IOException {
        BufferedImage[] images = resize(image1, image2);
        BufferedImage resizedImage1 = images[0];
        BufferedImage resizedImage2 = images[1];

        BufferedImage mask = new BufferedImage(
                resizedImage1.getWidth(),
                resizedImage1.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < resizedImage1.getHeight(); y++) {
            for (int x = 0; x < resizedImage1.getWidth(); x++) {
                mask.setRGB(x, y, Color.RED.getRGB());
            }
        }

        double pixels = image1.getHeight() * image1.getWidth();
        double count = resizedImage1.getHeight() * resizedImage1.getWidth();
        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                Color color1 = new Color(resizedImage1.getRGB(x, y));
                Color color2 = new Color(resizedImage2.getRGB(x, y));

                if (color1.getRed() != color2.getRed()
                        || color1.getGreen() != color2.getGreen()
                        || color1.getBlue() != color2.getBlue()) {
                    mask.setRGB(x, y, Color.RED.getRGB());
                } else {
                    mask.setRGB(x, y, Color.BLACK.getRGB());
                    count--;
                }
            }
        }

        return ImageCompareResult.builder()
                .diffPixelsCount((long) count)
                .diffPixelsPercentage((100 / (pixels / count)) / 100)
                .mask(mask)
                .build();
    }
}
