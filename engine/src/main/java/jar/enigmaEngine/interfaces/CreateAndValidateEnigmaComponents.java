package jar.enigmaEngine.interfaces;

import jar.enigmaEngine.exceptions.InvalidABCException;
import jar.enigmaEngine.exceptions.InvalidReflectorException;
import jar.enigmaEngine.exceptions.InvalidRotorException;
import jar.enigmaEngine.impl.ReflectorImpl;
import jar.enigmaEngine.impl.RotorImpl;

import java.util.List;
import java.util.Map;

public interface CreateAndValidateEnigmaComponents {
    void ValidateABC(String abc) throws InvalidABCException;
    Rotor createRotor(int id, int notch, List<Character> rightSide, List<Character> leftSide) throws InvalidRotorException;
    Reflector createReflector(List<Integer> input, List<Integer> output, Reflector.ReflectorID id) throws InvalidReflectorException;
    void validateRotorsIDs(Map<Integer, RotorImpl> rotors) throws InvalidRotorException;
    void validateReflectorsIDs(Map<Reflector.ReflectorID, ReflectorImpl> reflectors) throws InvalidReflectorException;

}