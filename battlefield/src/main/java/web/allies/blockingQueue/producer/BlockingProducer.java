package web.allies.blockingQueue.producer;

import jar.clients.battlefield.Difficulty;
import jar.common.Combinations;
import jar.common.Permutations;
import jar.dto.ConfigurationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class BlockingProducer extends AbstractProducer {
    private ConfigurationDTO initialMachineCodeTask;
    public BlockingProducer(BlockingQueue<ConfigurationDTO> queue, int missionSize, long total, Difficulty difficulty,
                            int totalReflectors, int totalRotors, ConfigurationDTO initialMachineCodeTask) {
        super(queue, missionSize, total, difficulty, totalReflectors, totalRotors);
        this.initialMachineCodeTask = initialMachineCodeTask;
    }

    @Override
    public void run() {
        try {
            System.out.println("Thread " + Thread.currentThread().getName() + " is about to initialize the blocking queue.");
            incrementStartingPositions();
            if (difficulty.equals(Difficulty.Easy)) {
                easyDifficulty();
            } else if (difficulty.equals(Difficulty.Medium)) {
                mediumDifficulty();
            } else if (difficulty.equals(Difficulty.Hard)) {
                hardDifficulty();
            } else if (difficulty.equals(Difficulty.Insane)) {
                impossibleDifficulty();
            } else { // Log
                System.out.println("ERROR: Invalid difficulty level entered!");
            }
            System.out.println("Thread " + Thread.currentThread().getName() + " has ended, blocking queue has all values.");
        } catch (InterruptedException e) {
            System.out.println("Was interrupted!");
        }

    }
    private void easyDifficulty() throws InterruptedException {
        for (long incrementStartingPositions = 0; incrementStartingPositions < totalTasks; incrementStartingPositions++) {
            queue.put(initialMachineCodeTask);
            incrementStartingPositions();
            tasksGeneratedCounter.incrementAndGet();
        }
    }

    private void incrementStartingPositions() {
        initialMachineCodeTask = initialMachineCodeTask.deepClone();
        for (long i = 0; i < missionSize; i++) {
            initialMachineCodeTask.incrementStartingPositions();
        }
    }

    private void mediumDifficulty() throws InterruptedException {
        for (long incrementSelectedReflector = 0; incrementSelectedReflector < totalReflectors; incrementSelectedReflector++) {
            incrementSelectedReflector();
            for (long incrementStartingPositions = 0; incrementStartingPositions < totalTasks / totalReflectors; incrementStartingPositions++) {
                queue.put(initialMachineCodeTask);
                incrementStartingPositions();
                tasksGeneratedCounter.incrementAndGet();
            }
        }
    }

    private void incrementSelectedReflector() {
        initialMachineCodeTask = initialMachineCodeTask.deepClone();
        initialMachineCodeTask.incrementSelectedReflector(totalReflectors);
    }

    private void hardDifficulty() throws InterruptedException {
        int allRotorsPermutationsSize = Difficulty.factorial(initialMachineCodeTask.getRotorsIDInorder().size());
        long limit = (totalTasks / totalReflectors) / allRotorsPermutationsSize;

        hardAndImpossibleDifficulties(allRotorsPermutationsSize, limit);
    }

    private void hardAndImpossibleDifficulties(int allRotorsPermutationsSize, long limit) throws InterruptedException {
        List<List<Integer>> allRotorsPermutations = Permutations.getAllPermutationsIterative(initialMachineCodeTask.getRotorsIDInorder(), new ArrayList<>());
        for (int rotorsCurrentPermutationIndex = 0; rotorsCurrentPermutationIndex < allRotorsPermutationsSize; rotorsCurrentPermutationIndex++) {
            updateRotorsPermutation(allRotorsPermutations, rotorsCurrentPermutationIndex); //, logger);
            for (long incrementSelectedReflector = 0; incrementSelectedReflector < totalReflectors; incrementSelectedReflector++) {
                incrementSelectedReflector();
                for (long incrementStartingPositions = 0; incrementStartingPositions < limit; incrementStartingPositions++) {
                    queue.put(initialMachineCodeTask);
                    incrementStartingPositions();
                    tasksGeneratedCounter.incrementAndGet();
                }
            }
        }
    }

    private void updateRotorsPermutation(List<List<Integer>> allRotorsPermutations, int idx) {

        initialMachineCodeTask = initialMachineCodeTask.deepClone();
        initialMachineCodeTask.setRotorsIDInorder(allRotorsPermutations.get(idx));
    }

    private void impossibleDifficulty() throws InterruptedException {
        List<List<Integer>> allRotorsCombinations = Combinations.generateAllCombinations(totalRotors, initialMachineCodeTask.getRotorsIDInorder().size());
        int allRotorsCombinationsSize = allRotorsCombinations.size();
        int allRotorsPermutationsSize = Difficulty.factorial(initialMachineCodeTask.getRotorsIDInorder().size());
        long limit = ((totalTasks / totalReflectors) / allRotorsPermutationsSize) / allRotorsCombinationsSize;

        for (int currentCombinationIndex = 0; currentCombinationIndex < allRotorsCombinationsSize; currentCombinationIndex++) {
            updateRotorsCombination(allRotorsCombinations, currentCombinationIndex);
            hardAndImpossibleDifficulties(allRotorsPermutationsSize, limit);
        }
    }

    private void updateRotorsCombination(List<List<Integer>> allRotorsCombinations, int idx) {

        initialMachineCodeTask = initialMachineCodeTask.deepClone();
        initialMachineCodeTask.setRotorsIDInorder(allRotorsCombinations.get(idx));
    }
}