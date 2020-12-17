package com.ljl.steamsearch.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "property")
public class PropertyConfig {
    private String indexStorePath;

    public String getIndexStorePath() {
        return indexStorePath;
    }

    public void setIndexStorePath(String indexStorePath) {
        this.indexStorePath = indexStorePath;
    }
}
