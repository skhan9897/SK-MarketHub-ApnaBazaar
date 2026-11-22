package com.sk.markethub.model;

public class Brand {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private Boolean active;

    public Brand() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getLogoUrl() { return logoUrl; }
    public Boolean getActive() { return active; }
}
