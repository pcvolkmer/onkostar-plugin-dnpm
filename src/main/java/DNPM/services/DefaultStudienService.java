package DNPM.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class DefaultStudienService implements StudienService {

    private final JdbcTemplate jdbcTemplate;

    public DefaultStudienService(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Studie> findAll() {
        var sql = "SELECT property_catalogue_version.version_number, pcve.code, pcve.shortdesc, pcve.description FROM property_catalogue "
        + "JOIN property_catalogue_version ON property_catalogue.id = property_catalogue_version.datacatalog_id "
        + "JOIN property_catalogue_version_entry pcve ON property_catalogue_version.id = pcve.property_version_id "
        + "JOIN studie ON studie.property_version_entry = pcve.id "
        + "WHERE property_catalogue.name = 'OS.Studien' "
        + "AND studie.aktiv;";

        return this.jdbcTemplate.query(sql, (resultSet, i) -> new Studie(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getInt(0)
        ));
    }

    @Override
    public List<Studie> findByQuery(String query) {
        var sql = "SELECT property_catalogue_version.version_number, pcve.code, pcve.shortdesc, pcve.description FROM property_catalogue "
                + "JOIN property_catalogue_version ON property_catalogue.id = property_catalogue_version.datacatalog_id "
                + "JOIN property_catalogue_version_entry pcve ON property_catalogue_version.id = pcve.property_version_id "
                + "JOIN studie ON studie.property_version_entry = pcve.id "
                + "WHERE property_catalogue.name = 'OS.Studien' "
                + "AND studie.aktiv "
                + "AND (pcve.shortdesc LIKE ? "
                + "OR pcve.description LIKE ?);";

        var like = String.format("%%%s%%", query);

        return this.jdbcTemplate.query(sql, new Object[]{like, like}, (resultSet, i) -> new Studie(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getInt(0)
        ));
    }
}
