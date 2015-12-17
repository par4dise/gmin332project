package fr.um2.gmin332project.model;

public class Commune {
	private String nom;
	private String codeInsee;
	private String codeDep;
	
	/**
	 * @param nom
	 * @param codeInsee
	 * @param codeDep
	 */
	public Commune(String nom, String codeInsee, String codeDep) {
		super();
		this.nom = nom;
		this.codeInsee = codeInsee;
		this.codeDep = codeDep;
	}
	 
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodeInsee() {
		return codeInsee;
	}
	public void setCodeInsee(String codeInsee) {
		this.codeInsee = codeInsee;
	}
	public String getCodeDep() {
		return codeDep;
	}
	public void setCodeDep(String codeDep) {
		this.codeDep = codeDep;
	}
	
	

}
