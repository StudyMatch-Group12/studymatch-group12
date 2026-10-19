package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "cold_start_profile")
public class ColdStartProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "estudante_id", nullable = false, unique = true)
    private Estudante estudante;

    private String interesses;
    private String preferenciaPapel;

    @ElementCollection
    @CollectionTable(name = "cold_start_assessment", joinColumns = @JoinColumn(name = "cold_start_profile_id"))
    @MapKeyColumn(name = "competencia_id")
    @Column(name = "nivel", nullable = false)
    private Map<Long, Integer> niveis = new HashMap<>();

    protected ColdStartProfile() {}

    public ColdStartProfile(Estudante estudante, String interesses, String preferenciaPapel,
                            Map<Long, Integer> niveis) {
        this.estudante = estudante;
        this.interesses = interesses;
        this.preferenciaPapel = preferenciaPapel;
        this.niveis.putAll(niveis);
    }

    public String getInteresses() { return interesses; }
    public String getPreferenciaPapel() { return preferenciaPapel; }
    public Map<Long, Integer> getNiveis() { return niveis; }
}
