package fr.um2.gmin332project.controller;

import org.openjena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;

import fr.um2.gmin332project.mongo.MongoReader;
import fr.um2.gmin332project.neo4j.Neo4JReader;
import fr.um2.gmin332project.tdb.TDBReader;

public class Main {
    public static final String NL = System.getProperty("line.separator");

	public static void queryWithReaders() {
		// Chargement données MongoDB
		Model m = MongoReader.mongoToModel();
		
		// Chargement données D2RQ
		//Modèle dynamique trop lourd à charger --> fichier "light"
		//Model m2 = D2RQReader.d2rqToModel();
		Model m2 = FileManager.get().loadModel(Config.d2rqFile);
		
		// Chargement données TDB
		Model m3 = TDBReader.tdbToModel();
		
		// Chargement données Neo4J
		Model m4 = Neo4JReader.neoToModel();
				
		// Chargement du mapping
		Model mMap = FileManager.get().loadModel(Config.mappingFile);
		
		// Agrégation de tous les modèles
		Model globalModel = ModelFactory.createDefaultModel();
		globalModel.add(m);
		globalModel.add(m2);
		globalModel.add(m3);
		globalModel.add(m4);
		globalModel.add(mMap);
		
		// Modèle inféré avec raisonneur
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        InfModel inf_m = ModelFactory.createInfModel(reasoner, globalModel);
        
        ////// CHOIX DE LA REQUETE ///////
        //String Q = Queries.Qinference;
        //String Q = Queries.Qd2rqneo;
        //String Q = Queries.Qd2rqtdb;
        String Q = Queries.Qd2rqneotdb;
        //String Q = Queries.Qpatrimoine;
        //String Q = Queries.Qinference;
        //////////////////////////////////
        
        System.out.println(Q);
        Query query = QueryFactory.create(Q) ;
        System.out.println();
        query.serialize(new IndentedWriter(System.out, true)) ;
        System.out.println("");
       
        QueryExecution qexec = QueryExecutionFactory.create(query, inf_m) ;
        try {             
             ResultSet rs = qexec.execSelect() ;
             ResultSetFormatter.out(System.out, rs, query);
        }
        finally {
        	qexec.close() ;
        }    	
	}
	
	public static void main(String[] args) {
		queryWithReaders();
	}

}
