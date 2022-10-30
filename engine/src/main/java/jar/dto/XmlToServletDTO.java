package jar.dto;

import jar.clients.battlefield.Difficulty;

import java.util.List;
import java.util.Set;

public class XmlToServletDTO {
    private final List<Integer> rotorsFromXML;
    private final List<String> reflectorsFromXML;
    private final Set<Character> abcFromXML;
    private final int numAllies;
    private final Difficulty difficulty;
    private final String battlefieldTitle;

    public XmlToServletDTO(List<Integer> rotorsFromXML, List<String> reflectorsFromXML, Set<Character> abcFromXML,
                           int numAllies, String difficulty, String battlefieldTitle) {
        this.rotorsFromXML = rotorsFromXML;
        this.reflectorsFromXML = reflectorsFromXML;
        this.abcFromXML = abcFromXML;
        this.numAllies = numAllies;
        this.difficulty = Difficulty.valueOf(difficulty);
        this.battlefieldTitle = battlefieldTitle;
    }

    public List<Integer> getRotorsFromXML() { // All rotors IDs in XML
        return this.rotorsFromXML;
    }
    public List<String> getReflectorsFromXML() { // All reflectors IDs in XML
        return this.reflectorsFromXML;
    }
    public Set<Character> getAbcFromXML() { // All ABC characters in XML
        return this.abcFromXML;
    }
    public int getNumAllies() {
        return numAllies;
    }
    public Difficulty getDifficulty() {
        return difficulty;
    }
    public String getBattlefieldTitle() {
        return battlefieldTitle;
    }
}
