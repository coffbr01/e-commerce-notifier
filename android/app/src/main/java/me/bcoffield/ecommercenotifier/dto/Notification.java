package me.bcoffield.ecommercenotifier.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {
    private long id;
    private String title;
    private String url;
    private String imageUrl;
    private long timestamp;
}
