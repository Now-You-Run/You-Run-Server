package com.running.you_run.running.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.running.you_run.running.entity.Record;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.running.you_run.running.util.CoordinateConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record RecordDto(
        @JsonIgnoreProperties("path")
        Record record,
        TrackInfoDto trackInfoDto
) {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static RecordDto from(Record record,TrackInfoDto trackInfoDto){
        return new RecordDto(record,trackInfoDto);
    }
    @JsonProperty("userPath")
    public List<CoordinateDto> userPath() {
        return CoordinateConverter.convertLineStringToCoordinates(record.getPath());
    }
    @JsonProperty("rawUserPath")
    public List<CoordinateDto> rawUserPath() {
        try {
            return MAPPER.readValue(
                    record.getRawPathJson(),
                    new TypeReference<List<CoordinateDto>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("raw_path_json 파싱 실패", e);
        }
    }
}
