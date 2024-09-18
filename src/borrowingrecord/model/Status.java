package borrowingrecord.model;

public enum Status {
    BORROWED("BORROWED"),
    RETURNED("RETURNED");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static String convertToString(Status status) {
        return status.getName();
    }
}
