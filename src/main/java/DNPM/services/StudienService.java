package DNPM.services;

import java.util.List;

public interface StudienService {

    List<Studie> findAll();

    List<Studie> findByQuery(String query);

}
