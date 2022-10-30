package decryptionManager;

import jar.common.rawData.Candidate;
import jar.dto.ConfigurationDTO;
import jar.enigmaEngine.exceptions.InvalidCharactersException;
import jar.enigmaEngine.exceptions.InvalidPlugBoardException;
import jar.enigmaEngine.exceptions.InvalidReflectorException;
import jar.enigmaEngine.exceptions.InvalidRotorException;
import jar.enigmaEngine.interfaces.EnigmaEngine;
import javafx.util.Pair;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class ExecutorServiceTask implements Runnable {
    private final EnigmaEngine enigmaEngine;
    private final ConfigurationDTO configurationDTO;
    private final String encryptedMessage;
    private final int taskSize; // Num of increases
    private final List<Pair<Candidate, Double>> candidatesPairs;
    private final List<String> originalEncryptionInput;

    private final DecryptionManagerTask dmTask;

    private final DecimalFormat df;

    public ExecutorServiceTask(EnigmaEngine enigmaEngine, ConfigurationDTO configurationDTO, String encryptedMessage,
                               int taskSize, List<Pair<Candidate, Double>> candidatesPairs, List<String> originalEncryptionInput,
                               DecryptionManagerTask dmTask) {
        this.enigmaEngine = enigmaEngine;
        this.configurationDTO = configurationDTO;
        this.encryptedMessage = encryptedMessage;
        this.taskSize = taskSize;
        this.candidatesPairs = candidatesPairs;
        this.originalEncryptionInput = originalEncryptionInput;

        this.dmTask = dmTask;

        df = new DecimalFormat("#.#####");
    }

    @Override
    public void run() {
        ConfigurationDTO originalTask = configurationDTO.deepClone();
        try {
            for (int i = 0; i < taskSize; i++) {
                enigmaEngine.setEngineConfiguration(configurationDTO);

                long start = System.nanoTime();
                String decipheredOutput = enigmaEngine.processMessage(encryptedMessage);
                List<String> outputWords = Arrays.asList(decipheredOutput.split(" "));

                if (enigmaEngine.getWordsDictionary().isConfigurationForCandidacy(outputWords)) {
                    long end = System.nanoTime();
                    double timeTook = (double) (end - start) / 1_000_000_000;
                    String candidateElapsedTime = df.format(timeTook);
                    Candidate candidateDetails = new Candidate(configurationDTO.toString(), originalTask.toString(),
                            outputWords.toString(), candidateElapsedTime, dmTask.getAlliesUsername());
                    candidatesPairs.add(new Pair<>(candidateDetails, timeTook));
                    if (outputWords.equals(originalEncryptionInput)) { // If we found the contest winner
                        dmTask.setStopDM(true);
                        dmTask.sendServerYouWon();
                    }
                }

                configurationDTO.incrementStartingPositions();
            }
        } catch (InvalidCharactersException | InvalidRotorException | InvalidReflectorException | InvalidPlugBoardException e) {
            e.printStackTrace();
        }
    }
}
