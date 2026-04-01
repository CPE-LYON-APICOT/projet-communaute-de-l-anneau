package fr.cpe;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'exemple — remplacez par vos vrais tests.
 */
class AppTest {

    @Test
    void ballInitialisesCorrectly() {
        Ball ball = new Ball(10, 20, 3, -2, Color.RED);
        assertEquals(10, ball.x);
        assertEquals(20, ball.y);
        assertEquals(3, ball.dx);
        assertEquals(-2, ball.dy);
        assertEquals(Color.RED, ball.getColor());
    }

    @Test
    void ballColorCanBeChanged() {
        Ball ball = new Ball(0, 0, 0, 0, Color.RED);
        ball.setColor(Color.BLUE);
        assertEquals(Color.BLUE, ball.getColor());
    }
}
