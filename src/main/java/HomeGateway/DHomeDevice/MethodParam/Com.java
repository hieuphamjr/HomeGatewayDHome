package main.java.HomeGateway.DHomeDevice.MethodParam;

public class Com {
    private final Integer id;
    private Integer idx;
    private final String act;

    public Com(Integer id, Integer idx, String act) {
        this.id = id;
        this.idx = idx;
        this.act = act;
    }

    public Com(Integer id, String act) {
        this.id = id;
        this.act = act;
    }
}
