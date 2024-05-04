package kr._42.seoul;

public class MemoryRepository implements Repository {
    private static final Repository instance = new MemoryRepository();
    
    private MemoryRepository() {}

    public static Repository getInstance() {
        return instance;
    }
}
