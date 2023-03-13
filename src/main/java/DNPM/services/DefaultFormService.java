package DNPM.services;

import DNPM.exceptions.FormException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class DefaultFormService implements FormService {

    private final JdbcTemplate jdbcTemplate;

    public DefaultFormService(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int getMainFormProcedureId(int procedureId) throws FormException {
        var sql = "SELECT hauptprozedur_id FROM prozedur WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (resultSet, i) -> resultSet.getInt("hauptprozedur_id"), procedureId);
        } catch (Exception e) {
            throw new FormException(String.format("No main form found for subform with ID '%d'", procedureId));
        }
    }

    @Override
    public List<Integer> getSubFormProcedureIds(int procedureId) {
        var sql = "SELECT id FROM prozedur WHERE hauptprozedur_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, procedureId);
    }
}
