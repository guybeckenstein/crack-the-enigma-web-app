package jar.clients.battlefield;

import jar.common.Combinations;
import jar.dto.EngineDTO;

public enum Difficulty {
    Easy,
    Medium,
    Hard,
    Insane;

    public static long translateDifficultyLevelToTasks(Difficulty difficulty, EngineDTO engineDTO, int numTotalReflectors, int abcSize) {
        switch (difficulty) {
            case Easy:
                return (long) Math.pow(abcSize, engineDTO.getSelectedRotors().size());
            case Medium:
                return (long) Math.pow(abcSize, engineDTO.getSelectedRotors().size()) * numTotalReflectors;
            case Hard:
                return (long) Math.pow(abcSize, engineDTO.getSelectedRotors().size()) * numTotalReflectors
                        * factorial(engineDTO.getSelectedRotors().size());
            case Insane:
                return (long) Math.pow(abcSize, engineDTO.getSelectedRotors().size()) * numTotalReflectors
                        * factorial(engineDTO.getSelectedRotors().size()) *
                        Combinations.generateAllCombinationsReturnNumber(engineDTO.getTotalNumberOfRotors(), engineDTO.getSelectedRotors().size());
            default:
                return 0;
        }
    }

    public static int factorial(int size) {
        int result = 1;
        for (int i = 1; i <= size; i++) {
            result *= i;
        }
        return result;
    }
}