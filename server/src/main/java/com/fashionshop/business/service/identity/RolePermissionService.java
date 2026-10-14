package com.fashionshop.business.service.identity;

import com.fashionshop.business.entity.Permission;
import com.fashionshop.business.entity.Role;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ConflictException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.identity.PermissionDAO;
import com.fashionshop.dataaccess.dao.identity.RoleDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.presentation.dto.identity.CreatePermissionDTO;
import com.fashionshop.presentation.dto.identity.CreateRoleDTO;
import com.fashionshop.presentation.dto.identity.UpdatePermissionDTO;
import com.fashionshop.presentation.dto.identity.UpdateRoleDTO;

import java.util.*;

public class RolePermissionService {

    private final RoleDAO roleDAO;
    private final PermissionDAO permissionDAO;
    private final UserDAO userDAO;

    public RolePermissionService() {
        this(new RoleDAO(), new PermissionDAO(), new UserDAO());
    }

    public RolePermissionService(RoleDAO roleDAO, PermissionDAO permissionDAO, UserDAO userDAO) {
        this.roleDAO = roleDAO;
        this.permissionDAO = permissionDAO;
        this.userDAO = userDAO;
    }

    // ==========================================
    // Role Operations
    // ==========================================

    public List<Map<String, Object>> getAllRoles(String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        List<Role> roles = roleDAO.findAllWithPermissions();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Role r : roles) {
            result.add(toRoleMap(r));
        }
        return result;
    }

    public Map<String, Object> getRoleById(Integer roleId, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (roleId == null) {
            throw new NotFoundException("Vai trò không tồn tại");
        }
        Role role = roleDAO.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new NotFoundException("Vai trò không tồn tại"));
        return toRoleMap(role);
    }

    public Map<String, Object> createRole(CreateRoleDTO dto, Integer createdById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);

        String code = dto.getCode();
        if (roleDAO.existsByCode(code)) {
            throw new ConflictException("Mã vai trò đã tồn tại");
        }

        Role role = new Role();
        role.setName(dto.getName());
        role.setCode(code);
        role.setIsActive(true);
        role.setCreatedById(createdById);

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            List<Permission> perms = permissionDAO.findByIds(dto.getPermissionIds());
            role.setPermissions(new HashSet<>(perms));
        }

        Role saved = roleDAO.save(role);
        return toRoleMap(saved);
    }

    public Map<String, Object> updateRole(Integer roleId, UpdateRoleDTO dto, Integer updatedById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (roleId == null) {
            throw new NotFoundException("Vai trò không tồn tại");
        }

        Role role = roleDAO.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new NotFoundException("Vai trò không tồn tại"));

        if (dto.getName() != null) {
            role.setName(dto.getName());
        }
        if (dto.getIsActive() != null) {
            role.setIsActive(dto.getIsActive());
        }
        if (dto.getPermissionIds() != null) {
            List<Permission> perms = permissionDAO.findByIds(dto.getPermissionIds());
            role.setPermissions(new HashSet<>(perms));
        }
        role.setUpdatedById(updatedById);

        Role updated = roleDAO.update(role);
        return toRoleMap(updated);
    }

    public Map<String, Object> softDeleteRole(Integer roleId, Integer deletedById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (roleId == null) {
            throw new NotFoundException("Vai trò không tồn tại");
        }

        Role role = roleDAO.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Vai trò không tồn tại"));

        String code = role.getCode();
        if ("ADMIN".equalsIgnoreCase(code) || "CUSTOMER".equalsIgnoreCase(code)) {
            throw new BadRequestException("Không được xóa role hệ thống");
        }

        long userCount = userDAO.countByRoleId(roleId);
        if (userCount > 0) {
            throw new BadRequestException("Không thể xóa vai trò đang có người dùng sử dụng");
        }

        roleDAO.softDelete(roleId, deletedById);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Xóa vai trò thành công");
        return response;
    }

    // ==========================================
    // Permission Operations
    // ==========================================

    public List<Map<String, Object>> getAllPermissions(String module, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        List<Permission> permissions = permissionDAO.findByModule(module);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Permission p : permissions) {
            result.add(toPermissionMap(p));
        }
        return result;
    }

    public Map<String, Object> getPermissionById(Integer permissionId, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (permissionId == null) {
            throw new NotFoundException("Permission không tồn tại");
        }

        Permission permission = permissionDAO.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission không tồn tại"));
        return toPermissionMap(permission);
    }

    public Map<String, Object> createPermission(CreatePermissionDTO dto, Integer createdById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);

        String code = dto.getCode();
        if (permissionDAO.existsByCode(code)) {
            throw new ConflictException("Mã permission đã tồn tại");
        }

        Permission permission = new Permission();
        permission.setCode(code);
        permission.setModule(dto.getModule());
        permission.setDescription(dto.getDescription());
        permission.setCreatedById(createdById);

        Permission saved = permissionDAO.save(permission);
        return toPermissionMap(saved);
    }

    public Map<String, Object> updatePermission(Integer permissionId, UpdatePermissionDTO dto, Integer updatedById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (permissionId == null) {
            throw new NotFoundException("Permission không tồn tại");
        }

        Permission permission = permissionDAO.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission không tồn tại"));

        if (dto.getModule() != null) {
            permission.setModule(dto.getModule());
        }
        if (dto.getDescription() != null) {
            permission.setDescription(dto.getDescription());
        }
        permission.setUpdatedById(updatedById);

        Permission updated = permissionDAO.update(permission);
        return toPermissionMap(updated);
    }

    public Map<String, Object> softDeletePermission(Integer permissionId, Integer deletedById, String currentRoleCode) {
        ensureAdmin(currentRoleCode);
        if (permissionId == null) {
            throw new NotFoundException("Permission không tồn tại");
        }

        permissionDAO.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission không tồn tại"));

        permissionDAO.softDelete(permissionId, deletedById);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Xóa quyền hạn thành công");
        return response;
    }

    // ==========================================
    // Helpers
    // ==========================================

    private void ensureAdmin(String roleCode) {
        if (!"ADMIN".equalsIgnoreCase(roleCode)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
    }

    private Map<String, Object> toRoleMap(Role role) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", role.getId());
        map.put("code", role.getCode());
        map.put("name", role.getName());
        map.put("isActive", role.getIsActive());

        List<Map<String, Object>> permList = new ArrayList<>();
        if (role.getPermissions() != null) {
            for (Permission p : role.getPermissions()) {
                permList.add(toPermissionMap(p));
            }
        }
        map.put("permissions", permList);
        map.put("createdAt", role.getCreatedAt());
        map.put("updatedAt", role.getUpdatedAt());
        return map;
    }

    private Map<String, Object> toPermissionMap(Permission p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", p.getId());
        map.put("code", p.getCode());
        map.put("module", p.getModule());
        map.put("description", p.getDescription());
        map.put("createdAt", p.getCreatedAt());
        map.put("updatedAt", p.getUpdatedAt());
        return map;
    }
}
