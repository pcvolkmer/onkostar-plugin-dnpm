package DNPM.services;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

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
        var sql = "SELECT pcc.name, property_catalogue_version.version_number, studie.studien_nummer, pcve.code, pcve.shortdesc, pcve.description FROM property_catalogue "
        + "JOIN property_catalogue_version ON property_catalogue.id = property_catalogue_version.datacatalog_id "
        + "JOIN property_catalogue_version_entry pcve ON property_catalogue_version.id = pcve.property_version_id "
        + "JOIN property_catalogue_category pcc on property_catalogue_version.id = pcc.version_id "
        + "LEFT JOIN studie ON pcve.id = studie.property_version_entry AND studie.aktiv "
        + "WHERE property_catalogue.name = 'OS.Studien';";

        return this.jdbcTemplate.query(sql, (resultSet, i) -> new Studie(
                resultSet.getString("name"),
                resultSet.getInt("version_number"),
                resultSet.getString("studien_nummer"),
                resultSet.getString("code"),
                resultSet.getString("shortdesc"),
                resultSet.getString("description")
        ));
    }

    @Override
    public List<Studie> findByQuery(String query) {
        var sql = "SELECT pcc.name, property_catalogue_version.version_number, studie.studien_nummer, pcve.code, pcve.shortdesc, pcve.description FROM property_catalogue "
                + "JOIN property_catalogue_version ON property_catalogue.id = property_catalogue_version.datacatalog_id "
                + "JOIN property_catalogue_version_entry pcve ON property_catalogue_version.id = pcve.property_version_id "
                + "JOIN property_catalogue_category pcc on property_catalogue_version.id = pcc.version_id "
                + "LEFT JOIN studie ON pcve.id = studie.property_version_entry AND studie.aktiv "
                + "WHERE property_catalogue.name = 'OS.Studien' AND (pcve.shortdesc LIKE ? OR pcve.description LIKE ? OR studie.studien_nummer LIKE ?);";

        var like = String.format("%%%s%%", query);

        return this.jdbcTemplate.query(sql, new Object[]{like, like, like}, (resultSet, i) -> new Studie(
                resultSet.getString("name"),
                resultSet.getInt("version_number"),
                resultSet.getString("studien_nummer"),
                resultSet.getString("code"),
                resultSet.getString("shortdesc"),
                resultSet.getString("description")
        ));
    }
}
