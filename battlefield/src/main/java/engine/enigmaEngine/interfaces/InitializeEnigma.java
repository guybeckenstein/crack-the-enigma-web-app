package engine.enigmaEngine.interfaces;

import engine.dto.XmlDTO;
import engine.dto.XmlToServletDTO;
import engine.enigmaEngine.exceptions.*;
import javafx.util.Pair;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface InitializeEnigma {
    EnigmaEngine getEnigmaEngineFromSource(String source) throws InvalidABCException, InvalidReflectorException, InvalidRotorException, IOException, JAXBException, InvalidMachineException;
    Pair<XmlDTO, XmlToServletDTO> getBriefXMLFromSource(String path, EnigmaEngine newEnigmaEngine) throws JAXBException, IOException, InvalidDecipherException, InvalidBattlefieldException;

}