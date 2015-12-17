package fr.um2.gmin332project.tests;

import org.neo4j.graphdb.GraphDatabaseService;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import fr.um2.gmin332project.controller.Config;
import fr.um2.gmin332project.neo4j.Neo4JReader;

public class Neo4jTest {
	public static final String NL = System.getProperty("line.separator") ;
	public static GraphDatabaseService graphdbservice;

	public static void main(String[] args) {

		//GetAll noeuds = new GetAll();
		Model m = ModelFactory.createOntologyModel();
		Neo4JReader.createNodeSpace();
		
		m = Neo4JReader.printNeoVoisins();
	    
		String sparqlQueryString = "PREFIX rdf: <"+RDF.getURI()+">" + NL + 
			"PREFIX neo: <"+ Config.neoPrefix +">" + NL + 
			"SELECT * WHERE {" +
			"  ?dep rdf:type neo:departement ." + NL +
			"  ?dep neo:pop ?c;" + NL +
			"       neo:name ?nom }";
		
	    System.out.println(sparqlQueryString);    
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, m) ;
        try { 
        	ResultSet results = qexec.execSelect() ;
        	ResultSetFormatter.out(results) ;
        } finally {
        	qexec.close() ;
        }
	}
}


