package proiectmap.socialmap.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}