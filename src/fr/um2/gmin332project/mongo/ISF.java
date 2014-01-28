package fr.um2.gmin332project.mongo;

public class ISF {
	private String codeInsee;
	private String nbreRedevablesISF;
	private String patrimoineMoyen;
	private String impotMoyen;
	private String annee;
	
	/**
	 * @param codeInsee
	 * @param nbreRedevablesISF
	 * @param patrimoineMoy
	 * @param impotMoy
	 * @param annee
	 */
	public ISF(String codeInsee, String nbreRedevablesISF,
			String patrimoineMoy, String impotMoy, String annee) {
		super();
		this.codeInsee = codeInsee;
		this.nbreRedevablesISF = nbreRedevablesISF;
		this.patrimoineMoyen = patrimoineMoy;
		this.impotMoyen = impotMoy;
		this.annee = annee;
	}
	
	@Override
	public String toString() {
		return "ISF [codeInsee=" + codeInsee + ", annee=" + annee
				+ ", nbreRedevablesISF=" + nbreRedevablesISF
				+ ", patrimoineMoy=" + patrimoineMoyen + ", impotMoy=" + impotMoyen
				+ "]";
	}

	public String getCodeInsee() {
		return codeInsee;
	}
	public void setCodeInsee(String codeInsee) {
		this.codeInsee = codeInsee;
	}
	public String getNbreRedevablesISF() {
		return nbreRedevablesISF;
	}
	public void setNbreRedevablesISF(String nbreRedevablesISF) {
		this.nbreRedevablesISF = nbreRedevablesISF;
	}
	public String getPatrimoineMoy() {
		return patrimoineMoyen;
	}
	public void setPatrimoineMoy(String patrimoineMoy) {
		this.patrimoineMoyen = patrimoineMoy;
	}
	public String getImpotMoy() {
		return impotMoyen;
	}
	public void setImpotMoy(String impotMoy) {
		this.impotMoyen = impotMoy;
	}
	public String getAnnee() {
		return annee;
	}
	public void setAnnee(String annee) {
		this.annee = annee;
	}

}
