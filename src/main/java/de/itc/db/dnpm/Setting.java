package de.itc.db.dnpm;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Immutable
@Table(name = "einstellung")
public class Setting {
    @Id
    private Long id;

    private String name;

    @Column(name = "wert")
    private String value;

    protected Setting() {
        // No content
    }

    public Setting(Long id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
