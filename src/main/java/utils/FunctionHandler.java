package utils;


import java.util.List;

@FunctionalInterface
public interface FunctionHandler {
    String handle(List<String> children);
}