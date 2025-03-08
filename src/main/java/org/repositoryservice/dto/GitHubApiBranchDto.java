package org.repositoryservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubApiBranchDto {
    @JsonProperty("name")
    private String name;

    public GitHubApiBranchDto() {
    }

    public GitHubApiBranchDto(String lastCommitSha, String name) {
        this.lastCommitSha = lastCommitSha;
        this.name = name;
    }

    @JsonProperty("commit")
    private void unpackCommitShaFromNestedObject(Object commit) {
        if (commit instanceof java.util.Map) {
            this.lastCommitSha = (String) ((java.util.Map<?, ?>) commit).get("sha");
        }
    }

    private String lastCommitSha;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastCommitSha() {
        return lastCommitSha;
    }

    public void setLastCommitSha(String lastCommitSha) {
        this.lastCommitSha = lastCommitSha;
    }
}
