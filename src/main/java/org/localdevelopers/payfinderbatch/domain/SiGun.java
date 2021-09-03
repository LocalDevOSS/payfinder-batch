package org.localdevelopers.payfinderbatch.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("sigun_info")
@Getter
public class SiGun {
    @Id
    private ObjectId id;
    @Field(name="SIGUN_CD")
    private String code;
    @Field(name="SIGNU_NM")
    private String name;
}
