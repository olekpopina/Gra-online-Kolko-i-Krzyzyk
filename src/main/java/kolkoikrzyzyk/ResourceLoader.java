package kolkoikrzyzyk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ResourceLoader {
    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(Objects.requireNonNull(ResourceLoader.class.getResourceAsStream(path)));
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося завантажити зображення: " + path, e);
        }
    }
}
