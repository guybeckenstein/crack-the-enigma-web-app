package engine.dto;

import java.util.List;
import java.util.Set;

public class XmlDTO {
    private final List<Integer> rotorsFromXML;
    private final List<String> reflectorsFromXML;
    private final String excludedCharacters;

    public XmlDTO(List<Integer> rotorsFromXML, List<String> reflectorsFromXML, String excludedCharacters) {
        this.rotorsFromXML = rotorsFromXML;
        this.reflectorsFromXML = reflectorsFromXML;
        this.excludedCharacters = excludedCharacters;
    }

    public List<Integer> getRotorsFromXML() { // All rotors IDs in XML
        return this.rotorsFromXML;
    }
    public List<String> getReflectorsFromXML() { // All reflectors IDs in XML
        return this.reflectorsFromXML;
    }
    public String getExcludedCharacters() {
        return excludedCharacters;
    }
}
