package com.seuprojeto.integrationtest.app.controller.dto;

import java.util.List;

public record PaginatedResponseDto<T>(int page, int size, long total, List<T> data) {
}
