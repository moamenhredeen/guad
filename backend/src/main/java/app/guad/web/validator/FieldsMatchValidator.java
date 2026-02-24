package app.guad.web.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Objects;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {

            BeanWrapper wrapper = new BeanWrapperImpl(value);
            Object first = wrapper.getPropertyValue(firstFieldName);
            Object second = wrapper.getPropertyValue(secondFieldName);

            boolean matches = Objects.equals(first, second);

            if (!matches) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(secondFieldName)
                        .addConstraintViolation();
            }

            return matches;
        } catch (Exception e) {
            return false;
        }
    }
}
