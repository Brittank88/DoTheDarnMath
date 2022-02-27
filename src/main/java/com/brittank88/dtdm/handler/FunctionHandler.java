package com.brittank88.dtdm.handler;

import net.minecraft.command.CommandException;
import org.mariuszgromada.math.mxparser.Function;

import java.util.ArrayList;
import java.util.List;

public abstract class FunctionHandler {

    public static final List<Function> USER_FUNCTIONS = new ArrayList<>();

    public static int addFunction(String name, String expression) throws CommandException {

        // TODO: Check if function already exists (either user-defined or as a default function)
        // TODO: Warn if user-defined function is being overridden.

        USER_FUNCTIONS.add(new Function(name + '=' + expression));

        return 1;
    }
}
