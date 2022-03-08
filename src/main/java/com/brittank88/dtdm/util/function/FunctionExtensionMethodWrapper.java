package com.brittank88.dtdm.util.function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.FunctionExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * A wrapper implementing {@link FunctionExtension}.
 * Allows a {@link Method} to be used in the creation of a {@link Function}.
 */
class FunctionExtensionMethodWrapper implements FunctionExtension, Cloneable {

    private final @NonNull Method method;
    private final @NonNull List<AbstractMap.SimpleEntry<@NonNull String, @NonNull OptionalDouble>> parameters;

    public FunctionExtensionMethodWrapper(@NonNull Method method) {
        this.method = method;
        this.parameters = Arrays.stream(method.getParameters())
                .map(p -> new AbstractMap.SimpleEntry<>(p.getName(), OptionalDouble.empty()))
                .collect(Collectors.toList());
    }

    @Override
    public int getParametersNumber() {
        return parameters.size();
    }

    @Override
    public void setParameterValue(int parameterIndex, double parameterValue) {
        parameters.get(parameterIndex).setValue(OptionalDouble.of(parameterValue));
    }

    @Override
    public @NonNull String getParameterName(int parameterIndex) {
        return parameters.get(parameterIndex).getKey();
    }

    @Override
    public double calculate() throws RuntimeException {
        if (parameters.stream().anyMatch(p -> p.getValue().isEmpty())) throw new RuntimeException("Missing parameter value.");
        try {
            return (double) method.invoke(null, (Object) parameters.stream().map(AbstractMap.SimpleEntry::getValue).toArray(OptionalDouble[]::new));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NonNull FunctionExtension clone() throws RuntimeException {
        try {
            return (FunctionExtensionMethodWrapper) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
