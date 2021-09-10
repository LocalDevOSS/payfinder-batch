package org.localdevelopers.payfinderbatch.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Date;

@Getter
public class StoreItem {
    @JsonProperty("CMPNM_NM")
    private String name;                // 상호명

    @JsonProperty("INDUTYPE_NM")
    private String type;                // 업종

    @JsonProperty("REFINE_LOTNO_ADDR")
    private String lotNumberAddress;    // 지번주소

    @JsonProperty("REFINE_ROADNM_ADDR")
    private String roadNameAddress;     // 도로명주소

    @JsonProperty("DATA_STD_DE")
    @JsonFormat(pattern = "yyyy/mm/dd")
    private Date createAt;              // 데이터 생성 일자

    @JsonProperty("REFINE_ZIP_CD")
    private String zipCode;             // 우편번호

    @JsonProperty("REFINE_WGS84_LAT")
    private Double latitude;            // 위도

    @JsonProperty("REFINE_WGS84_LOGT")
    private Double longitude;           // 경도

    @JsonProperty("SIGUN_CD")
    private String siGunCode;           // 시군코드

    @JsonProperty("SIGUN_NM")
    private String siGunName;           // 시군명

    public boolean isValid() {
        return (name != null && roadNameAddress != null);
    }
}
