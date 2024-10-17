package lucors.at;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static java.lang.String.format;

public class Main {
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

    public static BufferedImage compareImages(BufferedImage image1, BufferedImage image2) throws IOException {
        BufferedImage[] images = resize(image1, image2);
        BufferedImage resizedImage1 = images[0];
        BufferedImage resizedImage2 = images[1];

        // Создаем новое изображение для хранения результатов сравнения
        BufferedImage result = new BufferedImage(resizedImage1.getWidth(), resizedImage1.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < resizedImage1.getHeight(); y++) {
            for (int x = 0; x < resizedImage1.getWidth(); x++) {
                result.setRGB(x, y, Color.RED.getRGB());
            }
        }

        // Попиксельно сравниваем два изображения
        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                // Получаем цвета пикселей из обоих изображений
                Color color1 = new Color(resizedImage1.getRGB(x, y));
                Color color2 = new Color(resizedImage2.getRGB(x, y));

                // Сравниваем значения цветов RGB
                if (color1.getRed() != color2.getRed()
                        || color1.getGreen() != color2.getGreen()
                        || color1.getBlue() != color2.getBlue()) {
                    // Если пиксели различаются, отмечаем это красным цветом в результирующем изображении
                    result.setRGB(x, y, Color.RED.getRGB());
                } else {
                    // Если пиксели совпадают, копируем цвет из первого изображения в результирующее изображение
                    result.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        // Загружаем оба изображения
        BufferedImage image1 = ImageIO.read(new File("image1.png"));
        BufferedImage image2 = ImageIO.read(new File("image2.png"));

        // Используем измененные по размеру изображения для сравнения
        BufferedImage result = compareImages(image1, image2);

        // Сохраняем результирующее изображение
        ImageIO.write(result, "png", new File("result.png"));
    }
}