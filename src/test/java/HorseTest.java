import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HorseTest {

    private Horse horse;

    @BeforeEach
    void setUp() {
        horse = new Horse("mustang", 42d, 122d);
    }

    // 1. Проверка на null в имени
    @Test
    void constructor_WhenNameIsNull_ThrowsIllegalArgumentException() {
        // Проверяем, что передача null в качестве имени вызывает исключение
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Horse(null, 10.0, 100.0)
        );

        // Дополнительно можно проверить сообщение исключения
        assertEquals("Name cannot be null.", exception.getMessage());
    }

    // 2-3. Проверка на пустую строку или пробельные символы в имени (параметризованный тест)
    @ParameterizedTest(name = "[{index}] Testing with blank name: \"{0}\"")
    @ValueSource(strings = {"EMPTY", "SPACE", "TAB", "NEWLINE", "MIXED_WHITESPACE"})
    void constructor_WhenNameIsBlank_ThrowsIllegalArgumentException(String name) {
        String actualName = switch (name) {
            case "EMPTY" -> "";
            case "SPACE" -> " ";
            case "TAB" -> "\t";
            case "NEWLINE" -> "\n";
            case "MIXED_WHITESPACE" -> " \t\n";
            default -> name;
        };

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Horse(actualName, 10.0, 100.0)
        );
        assertEquals("Name cannot be blank.", exception.getMessage());
    }

    // 4. Проверка на отрицательную скорость
    @Test
    void constructor_WhenNameIsWhitespace_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Horse("mustang", -10.0, 100.0)
        );
        assertEquals("Speed cannot be negative.", exception.getMessage());
    }

    // 5. Проверка на отрицательную дистанцию
    @Test
    void constructor_WhenDistanceIsNegative_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Horse("mustang", 10.0, -100.0)
        );
        assertEquals("Distance cannot be negative.", exception.getMessage());
    }

    @ParameterizedTest(name = "[{index}] All incorrect: \"{3}\"")
    @MethodSource("invalidConstructorArguments")
    void constructor_WithInvalidArguments_ThrowsException(String name, double speed, double distance, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Horse(name, speed, distance)
        );
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> invalidConstructorArguments() {
        return Stream.of(
                Arguments.of(null, 1.0, 1.0, "Name cannot be null."),
                Arguments.of("", 1.0, 1.0, "Name cannot be blank."),
                Arguments.of(" ", 1.0, 1.0, "Name cannot be blank."),
                Arguments.of("ValidName", -1.0, 1.0, "Speed cannot be negative."),
                Arguments.of("ValidName", 1.0, -1.0, "Distance cannot be negative.")
        );
    }

    // 6. Проверка вызова getRandomDouble с правильными параметрами
    @Test
    void move_ShouldCallGetRandomDoubleWithCorrectParameters() {
        try (MockedStatic<Horse> mockedStatic = Mockito.mockStatic(Horse.class)) {
            mockedStatic.when(() -> Horse.getRandomDouble(0.2, 0.9)).thenReturn(0.5);
            Horse horse = new Horse("mustang", 10.0, 100.0);
            horse.move();

            mockedStatic.verify(() -> Horse.getRandomDouble(0.2, 0.9));
        }
    }

    // 7. Проверка расчета новой дистанции (параметризованный тест)
    @ParameterizedTest
    @CsvSource({
            "10.0, 100.0, 0.5, 105.0",    // speed=10, distance=100, random=0.5 → 100 + 10*0.5 = 105
            "5.0, 50.0, 0.3, 51.5",        // speed=5, distance=50, random=0.3 → 50 + 5*0.3 = 51.5
            "20.0, 0.0, 0.9, 18.0",       // speed=20, distance=0, random=0.9 → 0 + 20*0.9 = 18
            "15.0, 200.0, 0.2, 203.0"     // speed=15, distance=200, random=0.2 → 200 + 15*0.2 = 203
    })
    void move_ShouldCorrectlyCalculateNewDistance(
            double speed, double initialDistance, double mockRandom, double expectedDistance) {
        try (MockedStatic<Horse> mockedStatic = Mockito.mockStatic(Horse.class)) {
            mockedStatic.when(() -> Horse.getRandomDouble(0.2, 0.9)).thenReturn(mockRandom);

            Horse horse = new Horse("mustang", speed, initialDistance);
            horse.move();
            assertEquals(expectedDistance, horse.getDistance(), 0.0001);
        }
    }

    @Test
    void getName() {
        assertEquals("mustang", horse.getName());
    }

    @Test
    void getSpeed() {
        assertEquals(42d, horse.getSpeed(), 0.1);
    }

    @Test
    void getDistance() {
        Horse horse2Param = new Horse("mustang", 10d);

        assertEquals(122d, horse.getDistance(), 0.1);
        assertEquals(0d, horse2Param.getDistance(), 0.1);
    }

}