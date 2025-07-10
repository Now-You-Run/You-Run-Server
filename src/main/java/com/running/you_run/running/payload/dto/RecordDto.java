package com.running.you_run.running.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.running.you_run.running.entity.Record;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.running.you_run.running.util.CoordinateConverter;

import java.util.List;

public record RecordDto(
    @JsonIgnoreProperties("path")
    Record record,
    TrackInfoDto trackInfoDto
) {
    public static RecordDto from(Record record,TrackInfoDto trackInfoDto){
        return new RecordDto(record,trackInfoDto);
    }
    @JsonProperty("userPath")
    public List<CoordinateDto> userPath() {
        return CoordinateConverter.convertLineStringToCoordinates(record.getPath());
    }
}
