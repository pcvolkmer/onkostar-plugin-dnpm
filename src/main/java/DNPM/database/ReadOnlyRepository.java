package DNPM.database;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * Basis-Repository for ReadOnly Spring-Data-JPA Repositories
 * <p>Entity-Klassen müssen in Package <code>de.itc.db.dnpm</code> liegen
 * @param <T> Typ des Entities
 * @param <ID> Typ der ID
 */
@NoRepositoryBean
public interface ReadOnlyRepository<T, ID extends Serializable> extends Repository<T, ID> {

    T findById(ID id);

    List<T> findAll();

}
