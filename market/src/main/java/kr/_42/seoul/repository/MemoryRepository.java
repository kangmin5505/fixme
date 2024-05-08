package kr._42.seoul.repository;

public class MemoryRepository implements Repository {
    private static final Repository instance = new MemoryRepository();
    
    private MemoryRepository() {}

    public static Repository getInstance() {
        return instance;
    }
}
