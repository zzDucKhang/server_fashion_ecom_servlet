package com.fashionshop.business.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"Role\"")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(
            name = "\"_RolePermissions\"",
            joinColumns = @JoinColumn(name = "\"A\""),
            inverseJoinColumns = @JoinColumn(name = "\"B\"")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }

    // Trong Role.java
    public void setActive(Boolean status) {
        this.isActive = status;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public Boolean hasPermission(String permissionCode) {
        if (permissionCode == null) return false;
        return this.permissions.stream()
                .anyMatch(p -> permissionCode.equalsIgnoreCase(p.getCode()));
    }
}