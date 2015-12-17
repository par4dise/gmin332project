package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import fr.um2.gmin332project.controller.Config;
import fr.um2.gmin332project.d2rq.D2RQAdapter;
import fr.um2.gmin332project.model.Commune;
import fr.um2.gmin332project.model.ISF;
import fr.um2.gmin332project.tests.MongoTest;

/*
 * 1ère approche :
 * Requêtes renvoyant des listes de POJOs, convertis ensuite en modèles RDF
 * Avantage : modèle dynamique (selon requête) plus léger
 * Inconvénient : couche intermédiaire de traitement
 */
public class QueryWithParameters {
	
	/*
	 * POJO ISF --> modèle RDF
	 */
	private static Model listISFToRDFModel(List<ISF> l) {
		Model m = ModelFactory.createDefaultModel();
		
		String ns = Config.projectNs + "mongo#";
		m.setNsPrefix("mongo", ns);
		
		// Classe
		Resource codeInsee = m.createResource(ns + "code_insee");
		
		// Propri??t??s
		Property nbRedevables2002 = m.createProperty(ns + "nb_redevables_isf_2002");
		Property patrimoineMoyen2002 = m.createProperty(ns + "patrimoine_moyen_2002");
		Property impotMoyen2002 = m.createProperty(ns + "impot_moyen_2002");
		
		Property nbRedevables2008= m.createProperty(ns + "nb_redevables_isf_2008");
		Property patrimoineMoyen2008 = m.createProperty(ns + "patrimoine_moyen_2008");
		Property impotMoyen2008 = m.createProperty(ns + "impot_moyen_2008");
		
		Property nbRedevables2010 = m.createProperty(ns + "nb_redevables_isf_2010");
		Property patrimoineMoyen2010 = m.createProperty(ns + "patrimoine_moyen_2010");
		Property impotMoyen2010 = m.createProperty(ns + "impot_moyen_2010");
		
		Resource r;
		for (ISF i: l) {
			System.out.println(i.toString());
			
			r = m.createResource(ns + i.getCodeInsee());
			m.add(r, RDF.type, codeInsee);
			
			String nb_redev = i.getNbreRedevablesISF();
			String imp_moy = i.getImpotMoy();
			String pat_moy = i.getPatrimoineMoy();
			int year = Integer.parseInt(i.getAnnee());
			
			if (year == 2002) {
				m.add(r, nbRedevables2002, nb_redev);
				m.add(r, patrimoineMoyen2002, pat_moy);
				m.add(r, impotMoyen2002, imp_moy);					
			} else if (year == 2008) {
				m.add(r, nbRedevables2008, nb_redev);
				m.add(r, patrimoineMoyen2008, pat_moy);
				m.add(r, impotMoyen2008, imp_moy);	
			} else if (year == 2010) {
				m.add(r, nbRedevables2010, nb_redev);
				m.add(r, patrimoineMoyen2010, pat_moy);
				m.add(r, impotMoyen2010, imp_moy);							
			}
		}
		
		try {
			FileOutputStream ost = new FileOutputStream(Config.mongoFile);
			
			m.write(ost, "RDF/XML-ABBREV" );
		}
			catch (FileNotFoundException e) {
				System.out.println("pb de fichier");
		} 
		
		return m;
	}

	/*
	 * Liste de POJOs "Commune" --> modèle RDF
	 */
	private static Model listCommunesToRDFModel(List<Object> communes) {
		Model m = ModelFactory.createDefaultModel();
		
		String ns = Config.projectNs + "d2rq#";
		m.setNsPrefix("d2rq", ns);
		
		// Classe
		Resource codeInsee = m.createResource(ns + "code_insee");
		
		// Propri??t??s
		Property nom = m.createProperty(ns + "nom_commune");
		Property codeDep = m.createProperty(ns + "code_departement");
		
		Resource r;
		for (Object c: communes) {
			System.out.println(c.toString());
			
			r = m.createResource(ns + ((Commune) c).getCodeInsee());
			m.add(r, RDF.type, codeInsee);
			m.add(r, nom, ((Commune) c).getNom());		
			m.add(r, codeDep, ((Commune) c).getCodeDep());				
		}
		
		try {
			FileOutputStream ost = new FileOutputStream(Config.d2rqFile);
			
			m.write(ost, "RDF/XML-ABBREV");
		}
			catch (FileNotFoundException e) {
				System.out.println("pb de fichier");
		} 
		
		return m;
	}
	
	public static void main(String[] args) {
		// Variables d'entr??e
		int deptCode = 34;
		int nbResultats = 5;
		
		System.out.println("==={ 1. Mongo }===========================");
		
		// 1. Mongo --> liste ordonnée ISF dans le département choisi
		MongoTest mm = new MongoTest();
		Set<String> codesInsee = new HashSet<String>();
		try {
			List<ISF> impots = new ArrayList<ISF>();
			impots = mm.queryByRange("code_insee", 
					deptCode * 1000 - 1, 		// range
					deptCode * 1000 + 1000, 
					0,							// année = 0 --> renvoie tout
					"patrimoine_moyen", 		// tri
					MongoTest.ORDER_DESC);
			
			// Liste pour requ??te SPARQL
			for (ISF i: impots) {
				codesInsee.add(i.getCodeInsee());
			}
			
			// Transfromation en mod??le RDF --> fichier "mongo.rdf"
			Model mMongo = listISFToRDFModel(impots);
			
			mMongo.write(System.out, "N-TRIPLES");
		} finally {
			mm.closeDb();
		}
		
		
		System.out.println("\r\n==={ 2. D2RQ }=============================");
		
		// 2. D2RQ --> récupération noms des communes concernées d'aprés leur code INSEE
		D2RQAdapter mD2rq = new D2RQAdapter();
		List<Object> communes = new ArrayList<Object>();

		communes = mD2rq.queryByFieldValues("code_insee", codesInsee);
        //communes = mD2rq.queryCommunesDunDepartement("34");
		System.out.println(">>>");
		for (Object o: communes) {
			Commune c = (Commune) o;
			System.out.println(c.getNom() + "\t" + c.getCodeInsee());
		}
		
		Model mD2RQ = listCommunesToRDFModel(communes);
		mD2RQ.write(System.out, "RDF/XML-ABBREV");

	}
}
