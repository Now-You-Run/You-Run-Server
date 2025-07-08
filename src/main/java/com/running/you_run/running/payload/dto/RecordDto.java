package com.running.you_run.running.payload.dto;

import com.running.you_run.running.entity.Record;

import java.util.List;

public record RecordDto(
    Record record,
    TrackInfoDto trackInfoDto
) {
    public static RecordDto from(Record record,TrackInfoDto trackInfoDto){
        return new RecordDto(record,trackInfoDto);
    }
}
