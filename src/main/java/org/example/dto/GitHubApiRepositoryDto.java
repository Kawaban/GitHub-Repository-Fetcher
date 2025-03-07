package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubApiRepositoryDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("owner")
    private void unpackOwnerNameFromNestedObject(Object owner) {
        if (owner instanceof java.util.Map) {
            this.ownerLogin = (String) ((java.util.Map<?, ?>) owner).get("login");
        }
    }

    private String ownerLogin;

    @JsonProperty("fork")
    private Boolean fork;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public Boolean isFork() {
        return fork;
    }

    public void setFork(Boolean fork) {
        this.fork = fork;
    }
}
