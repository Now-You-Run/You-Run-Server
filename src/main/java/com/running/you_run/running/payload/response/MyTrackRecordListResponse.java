package com.running.you_run.running.payload.response;

import com.running.you_run.running.payload.dto.MyTrackRecordListItemDto;
import com.running.you_run.running.payload.dto.TrackInfoDto;

import java.util.List;

public record MyTrackRecordListResponse(
        TrackInfoDto trackInfoDto,
        List<MyTrackRecordListItemDto> trackRecordDto
) {

}
