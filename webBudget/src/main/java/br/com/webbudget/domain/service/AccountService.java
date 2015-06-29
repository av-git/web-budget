/*
 * Copyright (C) 2015 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.domain.service;

import br.com.webbudget.domain.security.Grant;
import br.com.webbudget.domain.security.Group;
import br.com.webbudget.domain.security.GroupMembership;
import br.com.webbudget.domain.security.Role;
import br.com.webbudget.domain.security.User;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.picketlink.idm.IdentityManagementException;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.Account;
import org.picketlink.idm.query.IdentityQueryBuilder;
import org.picketlink.idm.query.RelationshipQuery;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 2.0.0
 * @since 1.0.0, 06/10/2013
 */
@ApplicationScoped
public class AccountService {

    @Inject
    private IdentityManager identityManager;
    @Inject
    private RelationshipManager relationshipManager;
    
    /**
     * 
     * @param username
     * @return 
     */
    public User findUserByUsername(String username) {
       
        final IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();
        
        List<User> users = queryBuilder.createIdentityQuery(User.class)
                .where(queryBuilder.equal(User.USER_NAME, username)).getResultList();

        if (users.isEmpty()) {
            return null;
        } else if (users.size() == 1) {
            return users.get(0);
        } else {
            throw new IdentityManagementException("account.error.duplicated-usernames");
        }
    }

    /**
     * 
     * @param authorization
     * @return 
     */
    public Role findRoleByName(String authorization) {
        
        final IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();

        final List<Role> roles = queryBuilder.createIdentityQuery(Role.class)
            .where(queryBuilder.equal(Role.AUTHORIZATION, authorization)).getResultList();

        if (roles.isEmpty()) {
            return null;
        } else if (roles.size() == 1) {
            return roles.get(0);
        } else {
            throw new IdentityManagementException("account.error.duplicated-roles");
        }
    }

    /**
     * 
     * @param groupName
     * @return 
     */
    public Group findGroupByName(String groupName) {

        final IdentityQueryBuilder queryBuilder = this.identityManager.getQueryBuilder();

        final List<Group> groups = queryBuilder.createIdentityQuery(Group.class)
            .where(queryBuilder.equal(Group.NAME, groupName)).getResultList();

        if (groups.isEmpty()) {
            return null;
        } else if (groups.size() == 1) {
            return groups.get(0);
        } else {
            throw new IdentityManagementException("account.error.duplicated-groups");
        }
    }
    
    /**
     * 
     * @param user
     * @return 
     */
    public List<Group> listUserGroups(User user) {

        final RelationshipQuery<GroupMembership> query = 
                this.relationshipManager.createRelationshipQuery(GroupMembership.class);

        query.setParameter(GroupMembership.MEMBER, user);
        
        final List<Group> groups = new ArrayList<>();
        
        for (GroupMembership membership : query.getResultList()) {
            groups.add(membership.getGroup());
        }
        
        return groups;
    }

    /**
     * 
     * @param member
     * @param group
     * @return 
     */
    public boolean isMember(User member, Group group) {

        final RelationshipQuery<GroupMembership> query = 
                this.relationshipManager.createRelationshipQuery(GroupMembership.class);

        query.setParameter(GroupMembership.MEMBER, member);

        final List<GroupMembership> memberships = query.getResultList();

        for (GroupMembership membership : memberships) {
            if (membership.getGroup().getId().equals(group.getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * @param group 
     * @param account
     */
    public void addToGroup(Group group, Account account) {
        this.relationshipManager.add(new GroupMembership(group, account));
    }

    /**
     * 
     * @param group
     * @param account 
     */
    public void removeFromGroup(Group group, Account account) {
        
        final RelationshipQuery<GroupMembership> query = 
                this.relationshipManager.createRelationshipQuery(GroupMembership.class);

        query.setParameter(GroupMembership.GROUP, group);
        query.setParameter(GroupMembership.MEMBER, account);

        for (GroupMembership membership : query.getResultList()) {
            this.relationshipManager.remove(membership);
        }
    }

    /**
     * 
     * @param user
     * @param role
     * @return 
     */
    public boolean userHasRole(User user, Role role) {
       
        final List<Group> groups = this.listUserGroups(user);
        
        boolean hasRole = false;
        
        for (Group group : groups) {
            if (this.groupHasRole(group, role)) {
                hasRole = true;
                break;
            }
        }

        return hasRole;
    }
    
    /**
     * 
     * @param group
     * @param role
     * @return 
     */
    public boolean groupHasRole(Group group, Role role) {
        
        final RelationshipQuery<Grant> query = this.relationshipManager.createRelationshipQuery(Grant.class);

        query.setParameter(Grant.ASSIGNEE, group);
        query.setParameter(Grant.ROLE, role);

        for (Grant grant : query.getResultList()) {
            if (grant.getAssignee().getId().equals(group.getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * @param role
     * @param group 
     */
    public void grantToGroup(Role role, Group group) {
        this.relationshipManager.add(new Grant(role, group));
    }

    /**
     * 
     * @param role
     * @param group 
     */
    public void revokeGroupGrant(Role role, Group group) {
        
        final RelationshipQuery<Grant> query = 
                this.relationshipManager.createRelationshipQuery(Grant.class);

        query.setParameter(Grant.ASSIGNEE, group);
        query.setParameter(Grant.ROLE, role);

        for (Grant grant : query.getResultList()) {
            this.relationshipManager.remove(grant);
        }
    }
}
