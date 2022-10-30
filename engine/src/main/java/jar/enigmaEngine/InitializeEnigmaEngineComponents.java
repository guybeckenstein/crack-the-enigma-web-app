package jar.enigmaEngine;

import jar.dto.XmlDTO;
import jar.dto.XmlToServletDTO;
import jar.enigmaEngine.exceptions.*;
import jar.enigmaEngine.impl.EnigmaEngineImpl;
import jar.enigmaEngine.impl.InitializeEnigmaFromXML;
import jar.enigmaEngine.interfaces.EnigmaEngine;
import jar.enigmaEngine.interfaces.InitializeEnigma;
import javafx.util.Pair;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class InitializeEnigmaEngineComponents {
    InitializeEnigma enigmaEngineInitializer = null;

    public EnigmaEngineImpl initializeEngine(String path) throws InvalidRotorException, InvalidABCException, InvalidReflectorException, JAXBException, IOException, InvalidMachineException {

        enigmaEngineInitializer = new InitializeEnigmaFromXML();
        return enigmaEngineInitializer.getEnigmaEngineFromSource(path);
    }
    public Pair<XmlDTO, XmlToServletDTO> initializeBriefXML(String path, EnigmaEngine newEnigmaEngine) throws JAXBException, InvalidDecipherException, IOException, InvalidBattlefieldException {
        return enigmaEngineInitializer.getBriefXMLFromSource(path, newEnigmaEngine);
    }
}