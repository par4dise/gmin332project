package fr.um2.gmin332project.neo4j;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.kernel.Traversal;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.RDF;

import fr.um2.gmin332project.controller.Config;

public class Neo4JReader {
	
	private static enum RelTypes implements RelationshipType
	{
	    DEPARTEMENT, VOISIN, REGION, EST
	}
	
	private static long popNodeId=0;
	private static final String neo4j_DBPath="data/neo4j/";
	public static GraphDatabaseService graphdbservice;
	

	/**
	 * Building graph
	 * Entities: Country > Region > Departement
	 */
	public static void createNodeSpace() {
		graphdbservice = new GraphDatabaseFactory().newEmbeddedDatabase(neo4j_DBPath);
		Transaction tx = graphdbservice.beginTx();
		try {
			// Modelling "France" = type "country"
			Node France = graphdbservice.createNode(); // Graph entry point = country
			popNodeId = France.getId();			
			Node pays = graphdbservice.createNode(); 
			pays.setProperty("name", "pays"); 
			France.createRelationshipTo(pays, RelTypes.EST);
			
			// Creating Region #1
			Node LanguedocRoussillon = graphdbservice.createNode(); 
			LanguedocRoussillon.setProperty("code", "91");
			LanguedocRoussillon.setProperty("name", "Languedoc-Roussillon");
			
			// Region #1 Dept #1
			Node lozere = graphdbservice.createNode(); 
			lozere.setProperty("name", "Lozère");
			lozere.setProperty("code", "48"); 
			lozere.setProperty("pop", "77 085");
			
			// Region #1 Dept #2
			Node herault = graphdbservice.createNode();
			herault.setProperty("name", "Hérault");
			herault.setProperty("code", "34");
			herault.setProperty("pop", "1 090 052"); 
			
			// Region #1 Dept #3
			Node gard = graphdbservice.createNode();
			gard.setProperty("name", "Gard"); 
			gard.setProperty("code", "30"); 
			gard.setProperty("pop", "733 747"); 
			
			// Region #1 Dept #4
			Node aude = graphdbservice.createNode();
			aude.setProperty("name", "Aude");
			aude.setProperty("code", "11");
			aude.setProperty("pop", "366 604"); 
			
			// Region #1 Dept #5
			Node pyrennesorientales= graphdbservice.createNode();
			pyrennesorientales.setProperty("name", "Pyrénées-Orientales");
			pyrennesorientales.setProperty("code", "66");
			pyrennesorientales.setProperty("pop", "459 798");
			
			// Creating relationships
			LanguedocRoussillon.createRelationshipTo(lozere, RelTypes.DEPARTEMENT);
			lozere.createRelationshipTo(herault, RelTypes.VOISIN);
			herault.createRelationshipTo(gard, RelTypes.VOISIN);
			gard.createRelationshipTo(aude, RelTypes.VOISIN);
			aude.createRelationshipTo(pyrennesorientales, RelTypes.VOISIN);
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			
			// Creating Region #2 and its depts
			Node paysdelaloire = graphdbservice.createNode(); 
			paysdelaloire.setProperty("name", "Pays de la Loire"); 
			paysdelaloire.setProperty("code", "52"); 
			
			Node maineetloire = graphdbservice.createNode(); 
			maineetloire.setProperty("name", "Maine-et-Loire"); 
			maineetloire.setProperty("code", "49"); 
			maineetloire.setProperty("pop", "800 424"); 
			
			Node vendee = graphdbservice.createNode();
			vendee.setProperty("name", "Vendée");
			vendee.setProperty("code", "85");
			vendee.setProperty("pop", "657 326");
	
			Node sarthe = graphdbservice.createNode();
			sarthe.setProperty("name", "Sarthe");
			sarthe.setProperty("code", "72");
			sarthe.setProperty("pop", "569 029");
		
			Node mayenne = graphdbservice.createNode();
			mayenne.setProperty("name", "Mayenne");
			mayenne.setProperty("code", "53");
			mayenne.setProperty("pop", "309 168");
			
			Node loireatlantique = graphdbservice.createNode();
			loireatlantique.setProperty("name", "Loire-Atlantique");
			loireatlantique.setProperty("code", "44");
			loireatlantique.setProperty("pop", "1 322 404");
			
			// Creating relationships
			paysdelaloire.createRelationshipTo(maineetloire, RelTypes.DEPARTEMENT);
			maineetloire.createRelationshipTo(vendee, RelTypes.VOISIN);
			vendee.createRelationshipTo(sarthe, RelTypes.VOISIN);
			sarthe.createRelationshipTo(mayenne, RelTypes.VOISIN);
			mayenne.createRelationshipTo(loireatlantique, RelTypes.VOISIN);
		
		//////////////////////////////////////////////////////////////////////////////////////////
			
			Node aquitaine = graphdbservice.createNode();
			aquitaine.setProperty("name", "Aquitaine");
			aquitaine.setProperty("code", "72");
			
			Node gironde = graphdbservice.createNode();
			gironde.setProperty("name", "Gironde"); 
			gironde.setProperty("code", "33"); 
			gironde.setProperty("pop", "1 491 170"); 
		
			aquitaine.createRelationshipTo(gironde, RelTypes.DEPARTEMENT);
			Node lotetgaronne = graphdbservice.createNode();
			lotetgaronne.setProperty("name", "Lot-et-Garonne");
			lotetgaronne.setProperty("code", "47");
			lotetgaronne.setProperty("pop", "333 569"); 
			gironde.createRelationshipTo(lotetgaronne, RelTypes.VOISIN);
			
			Node pyreneesatlantiques = graphdbservice.createNode(); // cr??ation d'un autre noeud
			pyreneesatlantiques.setProperty("name", "Pyrénées-Atlantique"); 
			pyreneesatlantiques.setProperty("code", "64"); 
			pyreneesatlantiques.setProperty("pop", "662 236"); 
			lotetgaronne.createRelationshipTo(pyreneesatlantiques, RelTypes.VOISIN);
			Node landes = graphdbservice.createNode();
			landes.setProperty("name", "Landes");
			landes.setProperty("code", "40");
			landes.setProperty("pop", "19");
			pyreneesatlantiques.createRelationshipTo(landes, RelTypes.VOISIN);
			Node dordogne = graphdbservice.createNode();
			dordogne.setProperty("name", "Dordogne");
			dordogne.setProperty("code", "24");
			dordogne.setProperty("pop", "20");
			landes.createRelationshipTo(dordogne, RelTypes.VOISIN);
			pays.createRelationshipTo(LanguedocRoussillon, RelTypes.REGION);
			pays.createRelationshipTo(paysdelaloire, RelTypes.REGION);
			pays.createRelationshipTo(aquitaine, RelTypes.REGION);	
			
			//////////////////////////////////////////////////////////////////////////
			
			tx.success(); // 
		}
		 catch(Exception e){
			System.out.println(e.getMessage());
			tx.finish();
		}
	}
	
    
	private static Node getFirstNode() {
		return graphdbservice.getNodeById(popNodeId)
			.getSingleRelationship( RelTypes.EST, Direction.OUTGOING)
			.getEndNode();
	}
	
	private static Traverser getVoisins(Node firstNode) {
		TraversalDescription td = Traversal.description()
			.breadthFirst()
			.relationships(RelTypes.REGION, Direction.OUTGOING)
			.evaluator(Evaluators.excludeStartPosition())
			.uniqueness(Uniqueness.NODE_GLOBAL);
		return td.traverse(firstNode);
	}
	
//////////////////////////////////////////////////
	
	public static Model printNeoVoisins() {
		Model m = ModelFactory.createOntologyModel();
		
		String stat = Config.neoPrefix;
		m.setNsPrefix("neo", stat);
		Resource pays = m.createResource(stat+"pays");
		Property caracterisepar = m.createProperty(stat+"caracterisepar");
		
		Resource Pays = m.createResource(stat+"pays");
		m.add(Pays, RDF.type, pays);
		
		Node firstNode = getFirstNode();
		Node contient;
		String output = firstNode.getProperty("name") + "voisins :" +
		System.getProperty("line.separator");
		Traverser voisinsTraverser = getVoisins(firstNode); // noeuds traverses
		int nbOfFriends = 0; // compteur de voisins
		for (Path voisinPath : voisinsTraverser) {
			
			output = "Région " + 
					voisinPath.length() + " => " +
					voisinPath.endNode().getProperty("name") +
					System.getProperty("line.separator");
			//System.out.println(output);
			Resource reg = m.createResource(stat+voisinPath.endNode().getProperty("name"));
			reg.addProperty(DC_11.title, voisinPath.endNode().getProperty("name").toString());
			m.add(Pays, caracterisepar, reg);
			 
			long id=0;
			contient=voisinPath.endNode();
			id=contient.getId();			 
	
			printNeoVoisinsappartenant(id,m,reg);
			nbOfFriends++;
				
		}
		//m.write(System.out, "RDF/XML");
		 
		try {       
			FileOutputStream outStream = new FileOutputStream(Config.neo4jFile);
		
			//exporte  dans un fichier
			m.write(outStream, "RDF/XML");
			outStream.close();
		}
		catch (FileNotFoundException e) {System.out.println("File not found");}
		catch (IOException e) {System.out.println("IO problem");}
		
		return m;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	public static String printNeoVoisinsappartenant(long i, Model m, Resource r) {
		String stat = Config.neoPrefix;
		m.setNsPrefix("neo", stat);
		Resource region = m.createResource(stat+"region");
		Resource departement = m.createResource(stat+"departement");
		Property departements = m.createProperty(stat+"departements");
		//Property code = m.createProperty(stat+"code");
		Property pop = m.createProperty(stat+"pop");
		Property name = m.createProperty(stat+"name");
		
		m.add(r, RDF.type, region);
		
		Node firstNode = getFirstNodeAppartenant(i);
		Node appartenant;
		
		String output = firstNode.getProperty("name") + "  voisins :" + System.getProperty("line.separator");
		
		Resource dep = m.createResource(stat + firstNode.getProperty("code"));
		m.add(dep, RDF.type, departement);
		m.add(dep, name, firstNode.getProperty("name").toString());
		m.add(dep, pop, firstNode.getProperty("pop").toString());
		//dep.addProperty(name, firstNode.getProperty("name").toString());
		//dep.addProperty(code , firstNode.getProperty("code").toString());
		//dep.addProperty(pop , firstNode.getProperty("pop").toString());
		m.add(r, departements, dep);
		
		Traverser voisinsTraverser = getVoisinsappartenant(firstNode); // noeuds traverses
		int nbOfFriends = 0; // compteur de voisins
		for (Path voisinPath : voisinsTraverser) {	
			output += "Département " + voisinPath.length() + " => "
			+ voisinPath.endNode().getProperty( "name" ) +
			System.getProperty("line.separator");
			
			Resource departs = m.createResource(stat+voisinPath.endNode().getProperty("code"));
			departs.addProperty(name, voisinPath.endNode().getProperty("name").toString());
			departs.addProperty(pop, voisinPath.endNode().getProperty("pop").toString());
			m.add(departs, RDF.type, departement);
			m.add(r, departements, departs);
			nbOfFriends++;
		}
		output += "Nombre de departements voisins est " + nbOfFriends;
		
		return output;
	}
	
////////////////////////////////////////////////////////////////////////////////////////////

	private static Node getFirstNodeAppartenant(long i) {
		return graphdbservice.getNodeById(i)
			.getSingleRelationship( RelTypes.DEPARTEMENT, Direction.OUTGOING)
			.getEndNode();
	}

	private static Traverser getVoisinsappartenant(Node firstNode) {
		TraversalDescription td = Traversal.description()
			.breadthFirst()
			.relationships(RelTypes.VOISIN, Direction.OUTGOING)
			.evaluator(Evaluators.excludeStartPosition())
			.uniqueness(Uniqueness.NODE_GLOBAL);
		return td.traverse(firstNode);
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		neoToModel();		
	}


	public static Model neoToModel() {
		Neo4JReader.createNodeSpace();
		
		Model m = Neo4JReader.printNeoVoisins();
		return m;
	}

}