package com.running.you_run.auth.repository;

import com.running.you_run.auth.dto.MyPageDto;

public interface MyPageQueryRepository {
    MyPageDto getMyPageSummary(Long userId);
}
