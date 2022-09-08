package main.ru.student_repository.json;

@FunctionalInterface
public interface StrategyParseObject<T> {
    T parse();
}

