package com.desolitech.tag.infrastructure.interfaces;

import an.awesome.pipelinr.Command;
import com.google.common.reflect.TypeToken;

public interface Validator<C extends Command<R>, R> {
    boolean validate(C command);

    default boolean matches(C command) {
        TypeToken<C> typeToken = new TypeToken<C>(getClass()) {
        };

        return typeToken.isSupertypeOf(command.getClass());
    }
}
