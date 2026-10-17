package pt.upt.studymatch.studymatchbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "course_units")
public class CourseUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private int curricularYear;
    private int semester;
    private int ects;

    public CourseUnit() {}

    public CourseUnit(String code, String name, int curricularYear, int semester, int ects) {
        this.code = code;
        this.name = name;
        this.curricularYear = curricularYear;
        this.semester = semester;
        this.ects = ects;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCurricularYear() { return curricularYear; }
    public void setCurricularYear(int curricularYear) { this.curricularYear = curricularYear; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public int getEcts() { return ects; }
    public void setEcts(int ects) { this.ects = ects; }
}