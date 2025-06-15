import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HippodromeTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void constructor_WhenNameIsNull_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Hippodrome(null));
        assertEquals("Horses cannot be null.", exception.getMessage());
    }

    @Test
    void constructor_WhenListIsEmpty_ThrowsIllegalArgumentException() {
        List<Horse> horses = new ArrayList<>();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->new Hippodrome(horses));
        assertEquals("Horses cannot be empty.", exception.getMessage());
    }

    // Тест для метода getHorses()
    @Test
    void getHorses_ShouldReturnSameListAsPassedToConstructor() {
        // Создаем список из 30 разных лошадей
        List<Horse> expectedHorses = IntStream.range(0, 30)
                .mapToObj(i -> new Horse("Horse" + i, 1.0 + i, 10.0 + i))
                .collect(Collectors.toList());

        Hippodrome hippodrome = new Hippodrome(expectedHorses);
        List<Horse> actualHorses = hippodrome.getHorses();

        // Проверяем что списки содержат одинаковые объекты в том же порядке
        assertEquals(expectedHorses, actualHorses, "Списки лошадей должны быть идентичны");
        assertIterableEquals(expectedHorses, actualHorses, "Порядок лошадей должен сохраняться");
    }

    // Тест для метода move()
    @Test
    void move_ShouldInvokeMoveOnAllHorses() {
        // Создаем список из 50 моков лошадей
        List<Horse> mockHorses = IntStream.range(0, 50)
                .mapToObj(i -> mock(Horse.class))
                .collect(Collectors.toList());

        Hippodrome hippodrome = new Hippodrome(mockHorses);
        hippodrome.move();

        // Проверяем что метод move() был вызван у каждой лошади
        mockHorses.forEach(horse -> verify(horse).move());
    }

    // Тест для метода getWinner()
    @Test
    void getWinner_ShouldReturnHorseWithMaxDistance() {
        // Создаем тестовых лошадей с разными дистанциями
        Horse horse1 = new Horse("Horse1", 1.0, 10.0);
        Horse horse2 = new Horse("Horse2", 2.0, 20.0);
        Horse horse3 = new Horse("Horse3", 3.0, 30.0);
        Horse horse4 = new Horse("Horse4", 4.0, 15.0);

        Hippodrome hippodrome = new Hippodrome(List.of(horse1, horse2, horse3, horse4));
        Horse winner = hippodrome.getWinner();

        // Проверяем что победитель - лошадь с максимальной дистанцией
        assertEquals(horse3, winner, "Победитель должен быть лошадь с наибольшей дистанцией");
    }

    // Дополнительный тест для проверки когда несколько лошадей с одинаковой максимальной дистанцией
    @Test
    void getWinner_WhenMultipleHorsesWithSameMaxDistance_ShouldReturnFirstOne() {
        Horse horse1 = new Horse("Horse1", 1.0, 10.0);
        Horse horse2 = new Horse("Horse2", 2.0, 20.0);
        Horse horse3 = new Horse("Horse3", 3.0, 20.0); // Такая же дистанция как у horse2

        Hippodrome hippodrome = new Hippodrome(List.of(horse1, horse2, horse3));
        Horse winner = hippodrome.getWinner();

        // Должна вернуться первая лошадь с максимальной дистанцией (horse2)
        assertEquals(horse2, winner);
    }
}