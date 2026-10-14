package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Tag\"")
public class Tag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void renameTag(String newName) {
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }
    }
}