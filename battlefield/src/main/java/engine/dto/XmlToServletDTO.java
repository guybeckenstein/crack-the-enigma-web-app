package engine.dto;

import java.util.List;
import java.util.Set;

public class XmlToServletDTO {
    private final List<Integer> rotorsFromXML;
    private final List<String> reflectorsFromXML;
    private final Set<Character> abcFromXML;
    private final Set<String> dictionaryWordsFromXML;
    private final int numAllies;
    private final String difficulty;
    private final String battlefieldTitle;

    public XmlToServletDTO(List<Integer> rotorsFromXML, List<String> reflectorsFromXML, Set<Character> abcFromXML, Set<String> dictionaryWordsFromXML,
                           int numAllies, String difficulty, String battlefieldTitle) {
        this.rotorsFromXML = rotorsFromXML;
        this.reflectorsFromXML = reflectorsFromXML;
        this.dictionaryWordsFromXML = dictionaryWordsFromXML;
        this.abcFromXML = abcFromXML;
        this.numAllies = numAllies;
        this.difficulty = difficulty;
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
    public Set<String> getDictionaryWordsFromXML() {
        return this.dictionaryWordsFromXML;
    }
    public int getNumAllies() {
        return numAllies;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public String getBattlefieldTitle() {
        return battlefieldTitle;
    }
}
