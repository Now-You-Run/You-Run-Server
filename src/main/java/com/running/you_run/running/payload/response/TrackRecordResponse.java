package com.running.you_run.running.payload.response;

import com.running.you_run.running.payload.dto.TrackInfoDto;
import com.running.you_run.running.payload.dto.TrackRecordDto;

import java.util.List;

public record TrackRecordResponse(
        TrackInfoDto trackInfoDto,
        List<TrackRecordDto> trackRecordDto
) {

}
