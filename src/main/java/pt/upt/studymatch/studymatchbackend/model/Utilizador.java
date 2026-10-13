package pt.upt.studymatch.studymatchbackend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "utilizador")
@Inheritance(strategy = InheritanceType.JOINED) // Estratégia para separar Utilizador, Estudante e Docente em tabelas diferentes
public abstract class Utilizador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    protected Utilizador() {}

    public Utilizador(String nome, String email, String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}