package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Permission\"")
public class Permission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 150)
    private String code;

    @Column(nullable = false, length = 100)
    private String module;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean matchModule(String mod) {
        return this.module != null && this.module.equalsIgnoreCase(mod);
    }
}