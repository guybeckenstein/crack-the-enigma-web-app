package jar.common.rawData.configuration;

import javafx.scene.control.ComboBox;

import java.util.Set;

public class RotorsRawData {
    private final ComboBox<Character> abcCharacters;
    public RotorsRawData(Set<Character> abc) {
        abcCharacters = new ComboBox<>();
        abcCharacters.getItems().addAll(abc);
    }

    public ComboBox<Character> getAbcCharacters() {
        return abcCharacters;
    }
}
