package com.thanglv.vaadin.vm;

import java.io.Serializable;


public class SearchCompanyIndustryVM implements Serializable {
    private String name;

    public SearchCompanyIndustryVM() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
