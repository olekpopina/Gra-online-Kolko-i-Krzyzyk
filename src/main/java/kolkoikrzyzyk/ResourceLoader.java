package kolkoikrzyzyk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

/**
 * @brief Klasa odpowiedzialna za ładowanie zasobów graficznych (obrazów) z plików.
 *
 * @details Klasa zapewnia metodę do ładowania obrazków z plików znajdujących się w zasobach
 * aplikacji. Używa klasy {@link ImageIO} do wczytania obrazów w formacie {@link BufferedImage}.
 * W przypadku niepowodzenia wczytania pliku, generuje odpowiedni wyjątek.
 */
public class ResourceLoader {

    /**
     * @brief Ładuje obrazek z pliku umieszczonego w zasobach aplikacji.
     *
     * @param path Ścieżka do pliku z obrazem w zasobach aplikacji.
     *
     * @return {@link BufferedImage} Obrazek wczytany z pliku.
     *
     * @throws RuntimeException Jeśli nie uda się załadować obrazka lub wystąpi błąd wejścia/wyjścia.
     *
     * @details Metoda próbuje wczytać obrazek z zasobów aplikacji przy pomocy
     * {@link ImageIO#read}. Jeśli wczytanie obrazka nie powiedzie się, metoda zgłasza wyjątek
     * {@link RuntimeException}. Jeśli plik zasobu nie zostanie znaleziony, również zostanie zgłoszony
     * wyjątek wskazujący na błąd ładowania zasobu.
     */
    public static BufferedImage loadImage(String path) {
        return Optional.ofNullable(ResourceLoader.class.getResourceAsStream(path))
                .map(inputStream -> {
                    try {
                        return ImageIO.read(inputStream); // Próba wczytania obrazka
                    } catch (IOException e) {
                        throw new RuntimeException("Input/Output exception occured", e); // Obsługa błędów wejścia/wyjścia
                    }
                })
                .orElseThrow(() -> new RuntimeException("Nie udało się załadować obrazek poniewaz byl rzucony NPE: " + path));
        // Obsługuje sytuację braku zasobu lub NullPointerException
    }
}
