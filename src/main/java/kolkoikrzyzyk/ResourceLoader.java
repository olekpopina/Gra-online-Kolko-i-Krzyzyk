package kolkoikrzyzyk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ResourceLoader {
    public static BufferedImage loadImage(String path) {
        /*try {
            return ImageIO.read(Objects.requireNonNull(ResourceLoader.class.getResourceAsStream(path)));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Nie udało się załadować obrazek: " + path, e);
        }*/
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
