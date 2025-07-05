package com.running.you_run.running.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoGeoService {
    @Value("${kakao.api.key}")
    private String KAKAO_API_KEY;
    private static final String KAKAO_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x={lng}&y={lat}";

    public String getCityDistrict(double latitude, double longitude) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_URL,
                HttpMethod.GET,
                entity,
                String.class,
                longitude, latitude
        );

        // JSON 파싱
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");
            if (documents.isArray() && documents.size() > 0) {
                JsonNode address = documents.get(0).path("address");
                // 시, 구 추출
                String city = address.path("region_1depth_name").asText(); // 시/도
                String district = address.path("region_2depth_name").asText(); // 구/군
                return city + " " + district;
            }
        } catch (Exception e){
            return "";
        }
        return "";
    }
}
