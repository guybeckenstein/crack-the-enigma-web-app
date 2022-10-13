package engine.enigmaEngine.interfaces;

import engine.dto.XmlDTO;
import engine.enigmaEngine.exceptions.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface InitializeEnigma {
    EnigmaEngine getEnigmaEngineFromSource(String source) throws InvalidABCException, InvalidReflectorException, InvalidRotorException, IOException, JAXBException, InvalidMachineException;
    XmlDTO getBriefXMLFromSource(String path, EnigmaEngine newEnigmaEngine) throws JAXBException, IOException, InvalidDecipherException;

}