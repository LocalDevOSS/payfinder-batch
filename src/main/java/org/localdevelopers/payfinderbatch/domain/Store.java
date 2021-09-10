package org.localdevelopers.payfinderbatch.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document
@Builder
@Getter
public class Store {
    @Id
    private ObjectId id;
    @NonNull
    private String name;                // 상호명
    private String type;                // 업종
    private String lotNumberAddress;    // 지번주소
    @NonNull
    private String roadNameAddress;     // 도로명주소
    private Date createAt;              // 데이터 기준 일자
    private String zipCode;             // 우편번호
    private Double latitude;            // 위도
    private Double longitude;           // 경도
    private String siGunCode;           // 시군코드
    private String siGunName;           // 시군명
    private String imageUrl;            // 대표 이미지 주소

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return name.equals(store.name) && roadNameAddress.equals(store.roadNameAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roadNameAddress);
    }

    public void update(Store oldStore) {    // 후처리된 필드값을 복사
        this.id = oldStore.id;
        this.imageUrl = oldStore.imageUrl;
        if (latitude == null)
            latitude = oldStore.latitude;
        if (longitude == null)
            longitude = oldStore.longitude;
    }

    public boolean requireUpdate(Store oldStore) {
        return !Objects.equals(createAt, oldStore.createAt);
    }
}
