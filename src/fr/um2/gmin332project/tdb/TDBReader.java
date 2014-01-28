package fr.um2.gmin332project.tdb;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import fr.um2.gmin332project.controller.Config;

public class TDBReader {
	public static final String NL = System.getProperty("line.separator");
	
	/* 
	 * createModel
	 * Création (ou MAJ) du modèle sur disque à partir des fichiers source 
	 */
	public static void updateModel() {
        Dataset ds = TDBFactory.createDataset(Config.tdbDirectory);
        
    	/* Insertion des données issues de fichiers RDF */
        Model model = ds.getNamedModel(Config.modelGeonamesName);   
        FileManager.get().readModel(model, Config.geo_regions);
        FileManager.get().readModel(model, Config.geo_departements);
        FileManager.get().readModel(model, Config.geo_onto);
        
        Iterator<String> graphNames = ds.listNames();
        while (graphNames.hasNext()) {
            String graphName = graphNames.next();       
            model = ds.getNamedModel(graphName);
      		System.out.println("Le graphe \"" + graphName + "\" est de taille " + model.size());
       	} 
	}
	
	/* Transfert du modèle dans un fichier */
	public static Model tdbToModel() {
        Model m = ModelFactory.createDefaultModel();
        
        String ns = Config.tdbPrefix;
		m.setNsPrefix("tdb", ns);
		
		// Classe
		Resource region = m.createResource(ns + "Region");
		Resource departement = m.createResource(ns + "Departement");
		
		// Propriétés
		Property nom = m.createProperty(ns + "nom");
		Property latitude = m.createProperty(ns + "latitude");
		Property longitude = m.createProperty(ns + "longitude");
		Property population = m.createProperty(ns + "population");
        
		Dataset ds = TDBFactory.createDataset(Config.tdbDirectory);
	    Model mGeo = ds.getNamedModel(Config.modelGeonamesName); 

	    // Requête geonames régions
        String q = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
        		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
        		"PREFIX gn: <http://www.geonames.org/ontology#>" + NL + 
        		"PREFIX gps: <http://www.w3.org/2003/01/geo/wgs84_pos#>" + NL + 
        		"SELECT * WHERE {" + NL + 
        		"  ?reg gn:featureCode gn:A.ADM1;" + NL +
        		"       gn:name ?gn_nom_region;" + NL +
        		"       gn:population ?gn_pop_region;" + NL +
        		"       gps:lat ?gn_lat_region;" + NL +
        		"       gps:long ?gn_long_region;" + NL +
        		"       owl:sameAs ?insee_reg }";
        
	    Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, mGeo);
        try {
        	ResultSet results = qexec.execSelect();
        	//ResultSetFormatter.out(System.out, results, query);
        	while (results.hasNext()) {
        		QuerySolution reg = results.next();
        		Resource r = m.createResource(ns + reg.getLiteral("gn_nom_region"));
				m.add(r, RDF.type, region);
				m.add(r, nom, reg.getLiteral("gn_nom_region"));
				m.add(r, latitude, reg.getLiteral("gn_lat_region"));
				m.add(r, longitude, reg.getLiteral("gn_long_region"));
				m.add(r, population, reg.getLiteral("gn_pop_region"));
				//m.add(r, OWL.sameAs, reg.getResource("insee_reg"));
        	}
        }
        finally {
	        qexec.close() ;
        }
        
        // Requête geonames départements
        q = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
        		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
        		"PREFIX gn: <http://www.geonames.org/ontology#>" + NL + 
        		"PREFIX gps: <http://www.w3.org/2003/01/geo/wgs84_pos#>" + NL + 
        		"SELECT * WHERE {" + NL + 
        		"  ?dep gn:featureCode gn:A.ADM2;" + NL +
        		"      gn:name ?gn_nom_dep;" + NL +
        		"      gn:population ?gn_pop_dep;" + NL +
        		"      gps:lat ?gn_lat_dep;" + NL +
        		"      gps:long ?gn_long_dep;" + NL +
        		"      owl:sameAs ?insee_dep }";
        
	    query = QueryFactory.create(q);
        qexec = QueryExecutionFactory.create(query, mGeo);
        try {
        	ResultSet results = qexec.execSelect();
        	//ResultSetFormatter.out(System.out, results, query);
        	while (results.hasNext()) {
        		QuerySolution reg = results.next();
        		Resource r = m.createResource(ns + reg.getLiteral("gn_nom_dep"));
				m.add(r, RDF.type, departement);
				m.add(r, nom, reg.getLiteral("gn_nom_dep"));
				m.add(r, latitude, reg.getLiteral("gn_lat_dep"));
				m.add(r, longitude, reg.getLiteral("gn_long_dep"));
				m.add(r, population, reg.getLiteral("gn_pop_dep"));
				//m.add(r, OWL.sameAs, reg.getResource("insee_dep"));
        	}
        }
        finally {
	        qexec.close() ;
	        ds.close();
        }
        
        try {
			FileOutputStream ost = new FileOutputStream(Config.tdbFile);

			m.write(ost, "RDF/XML-ABBREV");
			//m.write(System.out, "RDF/XML-ABBREV");
		}
			catch (FileNotFoundException e) {
				System.out.println("pb de fichier");
		} 
		return m;
	}
}
