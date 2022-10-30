package jar.enigmaEngine.interfaces;

import java.io.Serializable;

public interface Rotor extends Serializable {

    Character peekWindow();

    enum Direction {
        LEFT, RIGHT
    }

    int getOutputIndex(int inputIndex, Direction dir);
}