package com.motocare.authservice.security;

import com.motocare.authservice.dto.Role;

public class UserPrincipal {

    private Long userId;
    private Role role;
    private Long workshopId;

    public UserPrincipal(Long userId, Role role, Long workshopId) {
        this.userId = userId;
        this.role = role;
        this.workshopId = workshopId;
    }

    public UserPrincipal() {

    }

    public Long getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public Long getWorkshopId() {
        return workshopId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setWorkshopId(Long workshopId) {
        this.workshopId = workshopId;
    }
}