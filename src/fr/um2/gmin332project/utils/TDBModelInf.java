package fr.um2.gmin332project.utils;

import java.util.Iterator;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

public class TDBModelInf {
	public static final String NL = System.getProperty("line.separator");
	
	/* R??pertoire de stockage TDB */
	public static final String tdbDirectory = "data/tdb/tests_inference/";
	
	/* Donn??es Geonames */
	public static final String geo_regions = "data/raw/geonames_regions.rdf";
	public static final String geo_departements = "data/raw/geonames_departements.rdf";  
	public static final String geo_onto = "data/raw/geonames_v3.rdf";
	
	/* Donn??es INSEE */
	public static final String insee_data = "data/raw/cog-2012.ttl";
	public static final String insee_onto = "data/raw/insee-geo-onto.ttl";

	/* Mapping */
	public static final String map_file = "data/raw/mapping_insee_geonames.rdf"; 
	
	/* Noms des mod??les */
	public static final String modelInseeName = "insee";
	public static final String modelGeonamesName = "geonames";
	public static final String modelMappingName = "mapping";
	
	
	/* 
	 * createModel
	 * Cr??ation et peuplement du mod??le 
	 */
	public void createModel() {
        Dataset ds = TDBFactory.createDataset(tdbDirectory);
        
        try {
        	/* Insertion des donn??es issues de fichiers RDF */
            Model model = ds.getNamedModel(modelGeonamesName);   
	        FileManager.get().readModel(model, geo_regions);
	        FileManager.get().readModel(model, geo_departements);
	        FileManager.get().readModel(model, geo_onto);
	        
	        model = ds.getNamedModel(modelInseeName);   
	        FileManager.get().readModel(model, insee_data);
	        FileManager.get().readModel(model, insee_onto);
	        
	        model = ds.getNamedModel(modelMappingName);  
            FileManager.get().readModel(model, map_file);
	        
	        Iterator<String> graphNames = ds.listNames();
	        while (graphNames.hasNext()) {
	            String graphName = graphNames.next();       
	            model = ds.getNamedModel(graphName);
	      		System.out.println("Le graphe \"" + graphName + "\" est de taille " + model.size());
	       	} 
	        
	        //model.write(System.out, "RDF/XML");
        }
        finally {
        	//ds.close();  
        }
		
	}
	
	public void queryGeonames() {
	    Dataset ds = TDBFactory.createDataset(tdbDirectory);
	    Model mGeo = ds.getNamedModel(modelGeonamesName); 
		
		// Requ??te geonames (attention pas de d??partements)
        String q = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
        		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
        		"PREFIX gn: <http://www.geonames.org/ontology#>" + NL + 
        		"SELECT ?nom_dep ?pop ?nom_region WHERE {" + NL + 
        		"  ?d gn:name ?nom_dep;" +
        		"     gn:parentADM1 ?region;" + NL +
        		"     gn:population ?pop." + NL +
        		"  ?region gn:name ?nom_region }" + NL + 
        		"ORDER BY ?nom_region";
        
	    Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, mGeo);
        try {
        	System.out.println("Requete Geonames");
        	ResultSet results = qexec.execSelect();
	        ResultSetFormatter.out(results);
        }
        finally {
	        qexec.close() ;
	        ds.close();
        }
	}
	
	public void queryInsee() {
	    Dataset ds = TDBFactory.createDataset(tdbDirectory);
	    Model mInsee = ds.getNamedModel(modelInseeName); 
		
	   // Requ??te INSEE
       String q = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
        		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
        		"PREFIX geo: <http://rdf.insee.fr/def/geo#>" + NL + 
        		// INSEE
        		"SELECT ?insee_nom_region ?gn_nom_region WHERE { {" + NL + 
        		"  ?s rdf:type geo:Region;" + NL + 
        		"     geo:nom ?insee_nom_region;" +
        		"     geo:codeRegion ?code_region }}" + NL + 
        		"ORDER BY ?code_region";
        
	    Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, mInsee);
        try {
        	System.out.println("=== Requete INSEE ===");
        	ResultSet results = qexec.execSelect();
	        ResultSetFormatter.out(results);
        }
        finally {
	        qexec.close() ;
	        ds.close();
        }
	}
	
	public void queryModels() {
	    Dataset ds = TDBFactory.createDataset(tdbDirectory);
	    Model mInsee = ds.getNamedModel(modelInseeName); 
	    Model mGeo = ds.getNamedModel(modelGeonamesName); 
	    mInsee.add(mGeo);
	    Model mMap = ds.getNamedModel(modelMappingName); 
	    mInsee.add(mMap);
        InfModel inf_m = ModelFactory.createRDFSModel(mInsee);

		
	   // Requ??te 
       /*String q = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
        		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
        		"PREFIX geo: <http://rdf.insee.fr/def/geo#>" + NL + 
        		"PREFIX gn: <http://www.geonames.org/ontology#>" + NL + 
        		// INSEE
        		"SELECT ?insee_nom_region ?gn_nom_region WHERE { {" + NL + 
        		"  ?s rdf:type geo:Region;" + NL + 
        		"     geo:nom ?insee_nom_region;" +
        		"     geo:codeRegion ?code_region }" + NL + 
        		// Geonames
        		"UNION {" + NL + 
        		"   ?dep gn:parentADM1 ?region." + NL + 
        		"  ?region gn:featureCode gn:A.ADM1;" +
        		"          gn:name ?gn_nom_region } ." +
        		"FILTER(SAMETERM(?gn_nom_region, ?insee_nom_region)) }" + NL + 
        		"ORDER BY ?code_region";
       */
	       String q = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
	        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
	        		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
	        		"PREFIX geo: <http://rdf.insee.fr/def/geo#>" + NL + 
	        		"PREFIX gn: <http://www.geonames.org/ontology#>" + NL + 
	        		"SELECT * WHERE {" + NL + 
	        		//"  ?s ?p gn:A.ADM1 ." + NL + 
	        		"  ?s2 owl:sameAs ?d ." + NL + 
	        		"  ?s2 rdf:type geo:Region ." + NL + 
	        		"  OPTIONAL { ?s2 geo:nom ?nom_insee } ." + NL + 
	        		"  OPTIONAL { ?s2 gn:name ?nom_geonames } } ORDER BY ?nom_insee LIMIT 100 ";
        
	    Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, inf_m);
        try {
        	System.out.println("=== Requete 2 ===");
        	ResultSet results = qexec.execSelect();
	        ResultSetFormatter.out(results);
        }
        finally {
	        qexec.close() ;
	        ds.close();
        }
	}
	
	public static void main(String[] args) {
		TDBModelInf tdb = new TDBModelInf();
		tdb.createModel();
		tdb.queryModels();
		/*tdb.queryGeonames();
		tdb.queryInsee();
		*/

	}

}
