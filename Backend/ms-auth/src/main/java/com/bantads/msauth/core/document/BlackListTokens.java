package com.bantads.msauth.core.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "blacklisted_tokens")
public class BlackListTokens {

    @Id
    private String id;

    @Indexed(unique = true)
    private String token;

    @Indexed(expireAfter = "0s")
    private Date expireAt;

    public BlackListTokens(String token, Date expireAt) {
        this.token = token;
        this.expireAt = expireAt;
    }
}
