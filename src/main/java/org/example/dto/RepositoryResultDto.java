package org.example.dto;

import java.util.List;

public record RepositoryResultDto(String name, String owner, List<BranchResultDto> branches) {}
