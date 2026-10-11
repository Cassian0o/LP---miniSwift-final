package interpreter.type.composed;

import interpreter.type.Type;
import interpreter.type.TypeException;

public abstract class ComposedType extends Type {

    protected ComposedType(Type.Category classification) {
        super(classification);
    }

    public static ComposedType instance(Type.Category classification, Type ... innerTypes) {
        switch (classification) {
            case Array:
                if (innerTypes.length != 1) throw new TypeException();
                return ArrayType.instance(innerTypes[0]);
            case Dict:
                if (innerTypes.length != 2) throw new TypeException();
                return DictType.instance(innerTypes[0], innerTypes[1]);
            default:
                throw new TypeException();
        }
    }

}
