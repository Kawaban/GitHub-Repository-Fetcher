package org.repositoryservice.dto;

import java.util.List;

public record RepositoryResultDto(String name, String owner, List<BranchResultDto> branches) {}
