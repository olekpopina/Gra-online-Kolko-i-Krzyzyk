package kolkoikrzyzyk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class ResourceLoader {
    public static BufferedImage loadImage(String path) {
        return Optional.ofNullable(ResourceLoader.class.getResourceAsStream(path))
                .map(inputStream -> {
                    try {
                        return ImageIO.read(inputStream);
                    } catch (IOException e) {
                        throw new RuntimeException("Input/Output exception occured");
                    }
                })
                .orElseThrow(() -> new RuntimeException("Nie udało się załadować obrazek poniewaz byl rzucony NPE: " + path));
    }
}
