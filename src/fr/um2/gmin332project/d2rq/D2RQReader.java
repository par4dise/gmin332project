package fr.um2.gmin332project.d2rq;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.d2rq.jena.ModelD2RQ;
import fr.um2.gmin332project.common.Config;

public class D2RQReader {
    public static final String NL = System.getProperty("line.separator");

	/* Transfert des données dans un fichier */
	public static Model d2rqToModel() {
        // Modèle pour requêter
		Model m = new ModelD2RQ(Config.d2rqMapFile);
        // Modèle de sortie
		Model mOut = ModelFactory.createDefaultModel();
        
        String ns = Config.d2rqPrefix;
		m.setNsPrefix("d2rq", ns);
		mOut.setNsPrefix("d2rq", ns);
		
		// Classes
		Resource commune = mOut.createResource(ns + "Commune");
		Resource departement = mOut.createResource(ns + "Departement");
		
		// Propriétés
		Property nom = mOut.createProperty(ns + "nom_commune");
		Property codeRegion = mOut.createProperty(ns + "code_region");
		Property codeInsee = mOut.createProperty(ns + "code_insee");
		Property codeDepartement = mOut.createProperty(ns + "code_departement");
		Property nomDepartement = mOut.createProperty(ns + "nom_departement");
        
	    // Requête INSEE communes
        String q = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
        		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
        		"PREFIX gdc: <" + Config.d2rqPrefix + ">" + NL + 
        		"SELECT ?code_insee ?nom_commune ?code_dep WHERE {" + NL +
                "  ?commune gdc:com_nom ?nom_commune;" + NL +
                "           gdc:com_cdep ?code_dep;" + NL +
                "           gdc:com_insee ?code_insee ." + NL +
                "  FILTER((?code_dep = \"34\") || (?code_dep = \"30\") || (?code_dep = \"11\") || (?code_dep = \"66\")" +
                " || (?code_dep = \"48\") || (?code_dep = \"44\") || (?code_dep = \"49\") || (?code_dep = \"53\")" +
                " || (?code_dep = \"40\") || (?code_dep = \"47\") || (?code_dep = \"24\") || (?code_dep = \"53\")" +
                " || (?code_dep = \"72\") || (?code_dep = \"85\") || (?code_dep = \"64\")) }";
        
        System.out.println(q);
	    Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, m);
        try {
        	ResultSet results = qexec.execSelect();
        	//ResultSetFormatter.out(System.out, results, query);
        	while (results.hasNext()) {
        		QuerySolution reg = results.next();
        		Resource r = mOut.createResource(ns + reg.getLiteral("code_insee"));
        		mOut.add(r, RDF.type, commune);
        		mOut.add(r, codeInsee, reg.getLiteral("code_insee"));
        		mOut.add(r, codeDepartement, reg.getLiteral("code_dep"));
        		mOut.add(r, nom, reg.getLiteral("nom_commune"));
        	}
        }
        finally {
	        qexec.close() ;
        }
        
        // Requête INSEE départements
        q = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
        		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
        		"PREFIX gdc: <" + Config.d2rqPrefix + ">" + NL + 
        		"SELECT ?nom_dep ?code_dep ?code_reg WHERE {" + NL +
                "  ?dep gdc:dep_nom ?nom_dep;" + NL +
                "       gdc:dep_region ?code_reg;" + NL +
                "       gdc:dep_code ?code_dep }"; 
        
	    query = QueryFactory.create(q);
        qexec = QueryExecutionFactory.create(query, m);
        try {
        	ResultSet results = qexec.execSelect();
        	//ResultSetFormatter.out(System.out, results, query);
        	while (results.hasNext()) {
        		QuerySolution reg = results.next();
        		Resource r = mOut.createResource(ns + reg.getLiteral("code_dep"));
        		mOut.add(r, RDF.type, departement);
        		mOut.add(r, nomDepartement, reg.getLiteral("nom_dep"));
        		mOut.add(r, codeDepartement, reg.getLiteral("code_dep"));
				mOut.add(r, codeRegion, reg.getLiteral("code_reg"));
        	}
        }
        finally {
	        qexec.close() ;
        }
        
        try {
			FileOutputStream ost = new FileOutputStream(Config.d2rqFileBuffer);

			mOut.write(ost, "RDF/XML-ABBREV");
			mOut.write(System.out, "RDF/XML-ABBREV");
		}
			catch (FileNotFoundException e) {
				System.out.println("pb de fichier");
		} 
        
		return mOut;
	}
	
	public static void main(String[] args) {
		d2rqToModel();
	}

}
