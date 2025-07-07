package com.running.you_run.user.repository;

import com.running.you_run.user.dto.MyPageDto;

public interface MyPageQueryRepository {
    MyPageDto getMyPageSummary(Long userId);
}
