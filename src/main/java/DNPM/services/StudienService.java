package DNPM.services;

import DNPM.dto.Studie;

import java.util.List;

public interface StudienService {

    /**
     * Übergibt eine Liste mit allen Studien
     *
     * @return Liste mit allen Studien
     */
    List<Studie> findAll();

    /**
     * Übergibt eine Liste mit Studien, deren (Kurz-)Beschreibung oder Studiennummer den übergebenen Wert enthalten
     *
     * @param query Wert der enthalten sein muss
     * @return Gefilterte Liste mit Studien
     */
    List<Studie> findByQuery(String query);

}
