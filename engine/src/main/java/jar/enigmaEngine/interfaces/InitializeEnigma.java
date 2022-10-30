package jar.enigmaEngine.interfaces;

import jar.dto.XmlDTO;
import jar.dto.XmlToServletDTO;
import jar.enigmaEngine.exceptions.*;
import jar.enigmaEngine.impl.EnigmaEngineImpl;
import javafx.util.Pair;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface InitializeEnigma {
    EnigmaEngineImpl getEnigmaEngineFromSource(String source) throws InvalidABCException, InvalidReflectorException, InvalidRotorException, IOException, JAXBException, InvalidMachineException;
    Pair<XmlDTO, XmlToServletDTO> getBriefXMLFromSource(String path, EnigmaEngine newEnigmaEngine) throws JAXBException, IOException, InvalidDecipherException, InvalidBattlefieldException;

}