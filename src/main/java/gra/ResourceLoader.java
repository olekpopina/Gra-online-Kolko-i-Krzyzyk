package gra;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ResourceLoader {
    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream(path)));
        } catch (IOException e) {
            throw new RuntimeException("Cannot load image: " + path, e);
        }
    }
}

