package ui.interfaces;

import engine.dto.XmlDTO;
import engine.enigmaEngine.exceptions.*;
import engine.enigmaEngine.interfaces.EnigmaEngine;
import ui.historyAndStatistics.MachineHistoryAndStatistics;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Set;

public interface Input {

    String getCurrentMachineStateAsString();
    XmlDTO getXmlDTO();
    int getMessageCounter();
    MachineHistoryAndStatistics getMachineHistoryStates();
    EnigmaEngine getEngine();
    void readMachineFromXMLFile(String path) throws JAXBException, InvalidRotorException, IOException, InvalidABCException, InvalidReflectorException, InvalidDecipherException, InvalidMachineException;
    void initializeEnigmaCodeManually(String rotors, String startingPositions, String plugBoardPairs, String reflectorID) throws InvalidCharactersException, InvalidRotorException, InvalidReflectorException, InvalidPlugBoardException; // Changed to boolean. false - if player exits this option in the middle, true if he added all input
    void initializeEnigmaCodeAutomatically();
    String getMessageAndProcessIt(String messageInput, boolean bool) throws InvalidCharactersException;
    void resetMachine();
    String getMachineStatisticsAndHistory();
    Set<String> getWordsDictionary();
}