package model;

public class testModel {

    private int test_id;
    private String test_name,test_description;

    public testModel(Integer id, String name, String description){
        this.test_id = id;
        this.test_name = name;
        this.test_description = description;

    }

    public Integer getTest_id() {
        return test_id;
    }

    public String getTest_name() {
        return test_name;
    }

    public String getTest_description() {
        return test_description;
    }

}
