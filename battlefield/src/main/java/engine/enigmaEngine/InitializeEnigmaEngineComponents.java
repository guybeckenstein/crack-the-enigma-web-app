package engine.enigmaEngine;

import engine.dto.XmlDTO;
import engine.dto.XmlToServletDTO;
import engine.enigmaEngine.exceptions.*;
import engine.enigmaEngine.impl.InitializeEnigmaFromXML;
import engine.enigmaEngine.interfaces.EnigmaEngine;
import engine.enigmaEngine.interfaces.InitializeEnigma;
import javafx.util.Pair;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class InitializeEnigmaEngineComponents {
    InitializeEnigma enigmaEngineInitializer = null;

    public EnigmaEngine initializeEngine(String path) throws InvalidRotorException, InvalidABCException, InvalidReflectorException, JAXBException, IOException, InvalidMachineException {

        enigmaEngineInitializer = new InitializeEnigmaFromXML();
        return enigmaEngineInitializer.getEnigmaEngineFromSource(path);
    }
    public Pair<XmlDTO, XmlToServletDTO> initializeBriefXML(String path, EnigmaEngine newEnigmaEngine) throws JAXBException, InvalidDecipherException, IOException, InvalidBattlefieldException {
        return enigmaEngineInitializer.getBriefXMLFromSource(path, newEnigmaEngine);
    }
}