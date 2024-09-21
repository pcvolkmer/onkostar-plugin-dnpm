package dev.dnpm.services;

import dev.dnpm.dto.Studie;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Standardimplementierung zum Ermitteln von Studien
 *
 * @since 0.0.2
 */
public class DefaultStudienService implements StudienService {

    private final JdbcTemplate jdbcTemplate;

    public DefaultStudienService(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Studie> findAll() {
        var sql = "SELECT pcc.name, pcv.version_number, TRIM(studie.studien_nummer) AS studien_nummer, studie.startdatum, studie.endedatum, pcve.code, pcve.shortdesc, pcve.description, studie.aktiv FROM studie "
                + "LEFT JOIN category_entry ce ON ce.version_entry_id = studie.property_version_entry "
                + "LEFT JOIN property_catalogue_category pcc ON pcc.id = ce.category_id "
                + "JOIN property_catalogue_version_entry pcve ON pcve.id = studie.property_version_entry "
                + "JOIN property_catalogue_version pcv ON pcv.id = pcve.property_version_id "
                + "JOIN property_catalogue pc ON pc.id = pcv.datacatalog_id "
                + "WHERE pc.name = 'OS.Studien'"
                + "ORDER BY TRIM(studie.studien_nummer)";

        return this.jdbcTemplate.query(sql, (resultSet, i) -> new Studie(
                resultSet.getString("name"),
                resultSet.getInt("version_number"),
                resultSet.getString("code"),
                resultSet.getString("studien_nummer"),
                resultSet.getString("shortdesc"),
                resultSet.getString("description"),
                resultSet.getBoolean("aktiv")
        ));
    }

    @Override
    public List<Studie> findByQuery(String query) {
        var sql = "SELECT pcc.name, pcv.version_number, TRIM(studie.studien_nummer) AS studien_nummer, studie.startdatum, studie.endedatum, pcve.code, pcve.shortdesc, pcve.description, studie.aktiv FROM studie "
                + "LEFT JOIN category_entry ce ON ce.version_entry_id = studie.property_version_entry "
                + "LEFT JOIN property_catalogue_category pcc ON pcc.id = ce.category_id "
                + "JOIN property_catalogue_version_entry pcve ON pcve.id = studie.property_version_entry "
                + "JOIN property_catalogue_version pcv ON pcv.id = pcve.property_version_id "
                + "JOIN property_catalogue pc ON pc.id = pcv.datacatalog_id "
                + "WHERE pc.name = 'OS.Studien' AND (pcve.shortdesc LIKE ? OR pcve.description LIKE ? OR studie.studien_nummer LIKE ?)"
                + "ORDER BY TRIM(studie.studien_nummer)";

        var like = String.format("%%%s%%", query);

        return this.jdbcTemplate.query(sql, new Object[]{like, like, like}, (resultSet, i) -> new Studie(
                resultSet.getString("name"),
                resultSet.getInt("version_number"),
                resultSet.getString("code"),
                resultSet.getString("studien_nummer"),
                resultSet.getString("shortdesc"),
                resultSet.getString("description"),
                resultSet.getBoolean("aktiv")
        ));
    }

    @Override
    public List<Studie> findActive() {
        return findAll().stream().filter(Studie::isActive).collect(Collectors.toList());
    }

    @Override
    public List<Studie> findActiveByQuery(String query) {
        return findByQuery(query).stream().filter(Studie::isActive).collect(Collectors.toList());
    }
}
