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
	

	public static void createNodeSpace() {
		graphdbservice = new GraphDatabaseFactory().newEmbeddedDatabase(neo4j_DBPath);
		Transaction tx = graphdbservice.beginTx();
		try {
			Node France= graphdbservice.createNode(); // cr??ation du point d'acc??s au graphe
			popNodeId = France.getId();
			
			Node pays = graphdbservice.createNode(); // cr??ation du 2??me point 
		
			pays.setProperty("name", "pays"); 
			France.createRelationshipTo(pays, RelTypes.EST);
			
			Node LanguedocRoussillon = graphdbservice.createNode(); // cr??ation la branche de r??gion 91
			//matrixNodeId = psg.getId();
			LanguedocRoussillon.setProperty("code", "91");
			LanguedocRoussillon.setProperty("name", "Languedoc-Roussillon");// attribution du premier id
			Node lozere = graphdbservice.createNode(); // cr??ation d'un autre noeud
			lozere.setProperty("name", "Lozère");
			lozere.setProperty("code", "48"); // property(code)
			lozere.setProperty("pop", "77 085"); // property(nombre de population)
			// cr??ation d'une relation entre l'entr??e du graphe et la r??gion LanguedocRoussillon
			LanguedocRoussillon.createRelationshipTo(lozere, RelTypes.DEPARTEMENT);
			Node herault = graphdbservice.createNode();
			herault.setProperty("name", "Hérault");
			herault.setProperty("code", "34");
			herault.setProperty("pop", "1 090 052"); 
			lozere.createRelationshipTo(herault, RelTypes.VOISIN);
			
			Node gard = graphdbservice.createNode(); // cr??ation d'un autre noeud
			gard.setProperty("name", "Gard"); 
			gard.setProperty("code", "30"); 
			gard.setProperty("pop", "733 747"); 
			herault.createRelationshipTo(gard, RelTypes.VOISIN);
			Node aude = graphdbservice.createNode();
			aude.setProperty("name", "Aude");
			aude.setProperty("code", "11");
			aude.setProperty("pop", "366 604");
			gard.createRelationshipTo(aude, RelTypes.VOISIN);
			
			Node pyrennesorientales= graphdbservice.createNode();
			pyrennesorientales.setProperty("name", "Pyrénées-Orientales");
			pyrennesorientales.setProperty("code", "66");
			pyrennesorientales.setProperty("pop", "459 798");
			aude.createRelationshipTo(pyrennesorientales, RelTypes.VOISIN);
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			
			Node paysdelaloire = graphdbservice.createNode(); // cr??ation de la branche r??gion 52
			paysdelaloire.setProperty("name", "Pays de la Loire"); 
			paysdelaloire.setProperty("code", "52"); 
			Node maineetloire = graphdbservice.createNode(); // cr??ation d'un autre noeud
			maineetloire.setProperty("name", "Maine-et-Loire"); 
			maineetloire.setProperty("code", "49"); 
			maineetloire.setProperty("pop", "800 424"); 
			// cr??ation d'une relation entre l'entr??e du graphe et maineetloire
			paysdelaloire.createRelationshipTo(maineetloire, RelTypes.DEPARTEMENT);
			Node vendee = graphdbservice.createNode();
			vendee.setProperty("name", "Vendée");
			vendee.setProperty("code", "85");
			vendee.setProperty("pop", "657 326");
			maineetloire.createRelationshipTo(vendee, RelTypes.VOISIN);
	
			Node sarthe = graphdbservice.createNode();
			sarthe.setProperty("name", "Sarthe");
			sarthe.setProperty("code", "72");
			sarthe.setProperty("pop", "569 029");
			vendee.createRelationshipTo(sarthe, RelTypes.VOISIN);
		
			Node mayenne = graphdbservice.createNode();
			mayenne.setProperty("name", "Mayenne");
			mayenne.setProperty("code", "53");
			mayenne.setProperty("pop", "309 168");
			sarthe.createRelationshipTo(mayenne, RelTypes.VOISIN);
			
			Node loireatlantique = graphdbservice.createNode();
			loireatlantique.setProperty("name", "Loire-Atlantique");
			loireatlantique.setProperty("code", "44");
			loireatlantique.setProperty("pop", "1 322 404");
			mayenne.createRelationshipTo(loireatlantique, RelTypes.VOISIN);
		
		//////////////////////////////////////////////////////////////////////////////////////////
			
			Node aquitaine = graphdbservice.createNode(); // cr??ation d'une autre r??gion
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
			reg.addProperty(DC.title, voisinPath.endNode().getProperty("name").toString());
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