package interfaces;

import jar.dto.XmlDTO;
import jar.dto.XmlToServletDTO;
import jar.enigmaEngine.exceptions.*;
import jar.enigmaEngine.interfaces.EnigmaEngine;
import historyAndStatistics.MachineHistoryAndStatistics;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Set;

public interface Input {

    String getCurrentMachineStateAsString();
    XmlDTO getXmlDTO();
    XmlToServletDTO getXmlToServletDTO();
    MachineHistoryAndStatistics getMachineHistoryStates();
    EnigmaEngine getEngine();
    void createMachineFromXMLFile(String path) throws JAXBException, InvalidRotorException, IOException, InvalidABCException, InvalidReflectorException, InvalidDecipherException, InvalidMachineException, InvalidBattlefieldException;
    void initializeEnigmaCodeManually(String rotors, String startingPositions, String plugBoardPairs, String reflectorID) throws InvalidCharactersException, InvalidRotorException, InvalidReflectorException, InvalidPlugBoardException; // Changed to boolean. false - if player exits this option in the middle, true if he added all input
    void initializeEnigmaCodeAutomatically();
    String getMessageAndProcessIt(String messageInput, boolean bool) throws InvalidCharactersException;
    void resetMachine();
    Set<String> getWordsDictionary();
}