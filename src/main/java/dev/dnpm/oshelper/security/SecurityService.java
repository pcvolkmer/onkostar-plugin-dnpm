/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (C) 2023-2026 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.dnpm.oshelper.security;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * Service mit Methoden zum Feststellen von sicherheitsrelevanten Informationen eines Benutzers
 */
@Service
public class SecurityService {

    private final JdbcTemplate jdbcTemplate;

    public SecurityService(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    List<String> getPersonPoolIdsForPermission(Authentication authentication, PermissionType permissionType) {
        var sql = "SELECT p.kennung FROM personenstamm_zugriff " +
                " JOIN usergroup u ON personenstamm_zugriff.benutzergruppe_id = u.id " +
                " JOIN akteur_usergroup au ON u.id = au.usergroup_id " +
                " JOIN akteur a ON au.akteur_id = a.id " +
                " JOIN personenstamm p on personenstamm_zugriff.personenstamm_id = p.id " +
                " WHERE a.login = ? AND a.aktiv AND a.anmelden_moeglich ";

        if (PermissionType.READ_WRITE == permissionType) {
            sql += " AND personenstamm_zugriff.bearbeiten ";
        }

        var userDetails = (UserDetails)authentication.getPrincipal();

        return jdbcTemplate
                .query(sql, new Object[]{userDetails.getUsername()}, (rs, rowNum) -> rs.getString("kennung"));
    }

    List<String> getFormNamesForPermission(Authentication authentication, PermissionType permissionType) {

        var sql = "SELECT df.name FROM formular_usergroup_zugriff " +
                " JOIN data_form df ON formular_usergroup_zugriff.formular_id = df.id " +
                " JOIN usergroup u ON formular_usergroup_zugriff.usergroup_id = u.id " +
                " JOIN akteur_usergroup au ON u.id = au.usergroup_id " +
                " JOIN akteur a on au.akteur_id = a.id " +
                " WHERE a.login = ? AND a.aktiv AND a.anmelden_moeglich ";

        if (PermissionType.READ_WRITE == permissionType) {
            sql += " AND formular_usergroup_zugriff.bearbeiten ";
        }

        var userDetails = (UserDetails)authentication.getPrincipal();

        return jdbcTemplate
                .query(sql, new Object[]{userDetails.getUsername()}, (rs, rowNum) -> rs.getString("name"));
    }

}
