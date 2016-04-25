package jaque.noun;

public abstract class Noun {
    public abstract void write(StringBuilder b);

    public final String toString() {
        StringBuilder b = new StringBuilder();
        write(b);
        return b.toString();
    }
}
