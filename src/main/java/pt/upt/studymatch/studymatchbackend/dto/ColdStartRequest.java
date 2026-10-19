package pt.upt.studymatch.studymatchbackend.dto;

import java.util.Map;

public class ColdStartRequest {
    private String interesses;
    private String preferenciaPapel;
    private Map<Long, Integer> niveis;

    public String getInteresses() { return interesses; }
    public void setInteresses(String interesses) { this.interesses = interesses; }
    public String getPreferenciaPapel() { return preferenciaPapel; }
    public void setPreferenciaPapel(String preferenciaPapel) { this.preferenciaPapel = preferenciaPapel; }
    public Map<Long, Integer> getNiveis() { return niveis; }
    public void setNiveis(Map<Long, Integer> niveis) { this.niveis = niveis; }
}
