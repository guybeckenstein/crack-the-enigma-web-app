package jar.enigmaEngine.interfaces;

import jar.decryptionManager.WordsDictionary;
import jar.dto.ConfigurationDTO;
import jar.dto.EngineDTO;
import jar.enigmaEngine.exceptions.InvalidCharactersException;
import jar.enigmaEngine.exceptions.InvalidPlugBoardException;
import jar.enigmaEngine.exceptions.InvalidReflectorException;
import jar.enigmaEngine.exceptions.InvalidRotorException;

import java.io.Serializable;

public interface EnigmaEngine extends Serializable {
    String processMessage(String input) throws InvalidCharactersException;
    EngineDTO getEngineDTO();
    ConfigurationDTO getConfigurationDTO();
    void setEngineConfiguration(ConfigurationDTO configurationDTO) throws InvalidCharactersException, InvalidRotorException, InvalidReflectorException, InvalidPlugBoardException;
    WordsDictionary getWordsDictionary();
    void setWordsDictionary(WordsDictionary wordsDictionary);
}