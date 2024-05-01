package kr._42.seoul;

public interface Repository {
    static Repository getInstance() {
        throw new UnsupportedOperationException(
                "This method must be overridden in the implementing class");
    }

}
