package engine.enigmaEngine.interfaces;

import engine.enigmaEngine.exceptions.InvalidABCException;
import engine.enigmaEngine.exceptions.InvalidReflectorException;
import engine.enigmaEngine.exceptions.InvalidRotorException;

import java.util.List;
import java.util.Map;

public interface CreateAndValidateEnigmaComponents {
    void ValidateABC(String abc) throws InvalidABCException;
    Rotor createRotor(int id, int notch, List<Character> rightSide, List<Character> leftSide) throws InvalidRotorException;
    Reflector createReflector(List<Integer> input, List<Integer> output, Reflector.ReflectorID id) throws InvalidReflectorException;
    void validateRotorsIDs(Map<Integer, Rotor> rotors) throws InvalidRotorException;
    void validateReflectorsIDs(Map<Reflector.ReflectorID, Reflector> reflectors) throws InvalidReflectorException;

}