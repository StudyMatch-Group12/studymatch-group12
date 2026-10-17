package pt.upt.studymatch.studymatchbackend.dto;

public class EnrollmentRequestDTO {

    private Long courseUnitId;
    private String academicYear;
    private Double grade;

    public EnrollmentRequestDTO() {}

    public EnrollmentRequestDTO(Long courseUnitId, String academicYear, Double grade) {
        this.courseUnitId = courseUnitId;
        this.academicYear = academicYear;
        this.grade = grade;
    }

    public Long getCourseUnitId() {
        return courseUnitId;
    }

    public void setCourseUnitId(Long courseUnitId) {
        this.courseUnitId = courseUnitId;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
}