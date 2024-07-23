package com.todaysroom.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRecentSearchDto(Long id,
                                  @NotBlank(message = "검색어가 없습니다.") String recentSearch) {
}
