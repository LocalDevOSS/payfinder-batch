package org.localdevelopers.payfinderbatch.domain;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Builder
@Getter
public class Store {
    @Id
    private ObjectId id;
    private String name;                // 상호명
    private String type;                // 업종
    private String lotNumberAddress;    // 지번주소
    private String roadNameAddress;     // 도로명주소
    @CreatedDate
    private Date createAt;              // 데이터 생성 일자
    @LastModifiedDate
    private Date updateAt;              // 데이터 수정 일자
    private String zipCode;             // 우편번호
    private Double latitude;            // 위도
    private Double longitude;           // 경도
    private String siGunCode;           // 시군코드
    private String siGunName;           // 시군명

    public enum StoreFields {
        NAME("name"),
        TYPE("type"),
        LOT_NUMBER_ADDRESS("lotNumberAddress"),
        ROAD_NAME_ADDRESS("roadNameAddress"),
        CREATE_AT("createAt"),
        ZIP_CODE("zipCode"),
        LATITUDE("latitude"),
        LONGITUDE("longitude"),
        SI_GUN_CODE("siGunCode"),
        SI_GUN_NAME("siGunName")
        ;

        private final String name;
        StoreFields(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
