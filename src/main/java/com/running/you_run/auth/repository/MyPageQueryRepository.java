package com.running.you_run.auth.repository;

import com.running.you_run.auth.dto.MyPageSummaryDto;

public interface MyPageQueryRepository {
    MyPageSummaryDto getMyPageSummary(Long userId);
}
