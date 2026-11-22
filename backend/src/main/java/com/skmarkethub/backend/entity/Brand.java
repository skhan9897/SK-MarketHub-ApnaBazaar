package com.skmarkethub.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 180)
    private String slug;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "is_active")
    private Boolean active = true;

    public Brand() {}
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getLogoUrl() { return logoUrl; }
    public Boolean getActive() { return active; }
    public void setName(String v) { name = v; }
    public void setSlug(String v) { slug = v; }
    public void setLogoUrl(String v) { logoUrl = v; }
    public void setActive(Boolean v) { active = v; }
}