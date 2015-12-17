package fr.um2.gmin332project.utils;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import fr.um2.gmin332project.controller.Config;

public class TDBUtils {
	public static final String NL = System.getProperty("line.separator");
	
	public static void main(String[] args) {
		// Exécuter 1x pour remplissage 
		//createModel();
		
	    Dataset ds = TDBFactory.createDataset(Config.tdbDirectory);
	    Model mGeo = ds.getNamedModel(Config.modelGeonamesName); 

	    // Requête geonames
        String q = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
        		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
        		"PREFIX gn: <http://www.geonames.org/ontology#>" + NL + 
        		"PREFIX gps: <http://www.w3.org/2003/01/geo/wgs84_pos#>" + NL + 
        		"SELECT * WHERE {" + NL + 
        		"{  ?reg gn:featureCode gn:A.ADM1;" + NL +
        		"      gn:name ?gn_nom_region;" + NL +
        		"      gn:population ?gn_pop_region;" + NL +
        		"      gps:lat ?gn_lat_region;" + NL +
        		"      gps:long ?gn_long_region" + NL +
        		"} UNION {" + NL +
        		"  ?dep gn:featureCode gn:A.ADM2;" + NL +
        		"      gn:name ?gn_nom_dep;" + NL +
        		"      gn:population ?gn_pop_dep;" + NL +
        		"      gps:lat ?gn_lat_dep;" + NL +
        		"      gps:long ?gn_long_dep }}";
        
	    Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, mGeo);
        try {
        	System.out.println("Requete dans void main");
        	ResultSet results = qexec.execSelect();
	        ResultSetFormatter.out(results);
        }
        finally {
	        qexec.close() ;
	        ds.close();
        }
	}
}
