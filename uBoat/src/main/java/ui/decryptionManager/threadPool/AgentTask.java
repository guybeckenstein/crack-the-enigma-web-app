package ui.decryptionManager.threadPool;

import engine.dto.ConfigurationDTO;
import engine.enigmaEngine.exceptions.InvalidCharactersException;
import engine.enigmaEngine.exceptions.InvalidPlugBoardException;
import engine.enigmaEngine.exceptions.InvalidReflectorException;
import engine.enigmaEngine.exceptions.InvalidRotorException;
import engine.enigmaEngine.interfaces.EnigmaEngine;
import javafx.util.Pair;
import ui.decryptionManager.Candidate;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class AgentTask implements Runnable {
    private final EnigmaEngine enigmaEngine;
    private final ConfigurationDTO configurationDTO;
    private final String userOutput;
    private final int missionSize; // Num of increases
    private final DecimalFormat df;

    private final List<Pair<Candidate, Double>> candidatesPairs;

    public AgentTask(EnigmaEngine enigmaEngine, ConfigurationDTO configurationDTO, String userOutput, int missionSize, List<Pair<Candidate, Double>> candidatesPairs) {
        this.enigmaEngine = enigmaEngine;
        this.configurationDTO = configurationDTO;
        this.userOutput = userOutput;
        this.missionSize = missionSize;
        df = new DecimalFormat("#.#####");
        this.candidatesPairs = candidatesPairs;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < missionSize; i++) {
                enigmaEngine.setEngineConfiguration(configurationDTO);

                long start = System.nanoTime();
                String decipheredOutput = enigmaEngine.processMessage(userOutput);
                List<String> outputWords = Arrays.asList(decipheredOutput.split(" "));

                if (enigmaEngine.getWordsDictionary().isConfigurationForCandidacy(outputWords)) {
                    long end = System.nanoTime();
                    double timeTook = (double) (end - start) / 1_000_000_000;
                    String candidateElapsedTime = df.format(timeTook);
                    Candidate candidateDetails = new Candidate(configurationDTO.toString(), outputWords.toString(), candidateElapsedTime, Thread.currentThread().getName());
                    candidatesPairs.add(new Pair<>(candidateDetails, timeTook));
                }

                configurationDTO.incrementStartingPositions();
            }
        } catch (InvalidCharactersException | InvalidRotorException | InvalidReflectorException | InvalidPlugBoardException e) {
            e.printStackTrace();
        }
    }
}
