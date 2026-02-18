package com.bureauveritas.modelparser.model.view;

import java.util.Stack;

public class ShapeNameStack extends Stack<String> {
    public ShapeNameStack() {
        super();
    }

    public void resetShapeNamesStack(String initialShapeName) {
        clear();
        push(initialShapeName);
    }

    public String popPreviousShapeName() {
        pop();
        return pop();
    }
}
