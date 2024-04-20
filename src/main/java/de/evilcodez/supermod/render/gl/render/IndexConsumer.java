package de.evilcodez.supermod.render.gl.render;

public interface IndexConsumer {

    IndexConsumer index(int index);

    IndexConsumer applyBaseOffset();

    default IndexConsumer triangle(int a, int b, int c) {
        return this.index(a).index(b).index(c);
    }

    default IndexConsumer rect(int a, int b, int c, int d) {
        return this.index(a).index(b).index(c).index(c).index(d).index(a);
    }

    default IndexConsumer rect() {
        return this.applyBaseOffset().rect(0, 1, 2, 3).applyBaseOffset();
    }

    default IndexConsumer line(int a, int b) {
        return this.index(a).index(b);
    }
}
