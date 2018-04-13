package net.acesinc.data.json.generator.types;

public class Country extends TypeHandler {
    public static final String TYPE_NAME = "country";
    public static final String TYPE_DISPLAY_NAME = "Country";

    private String[] nameList = { "US", "India", "China", "England"};

    @Override
    public String getNextRandomValue() {
        return nameList[getRand().nextInt(0, nameList.length - 1)];
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }
}
