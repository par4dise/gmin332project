package fr.um2.gmin332project.mongo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.UnknownHostException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import fr.um2.gmin332project.controller.Config;

public class MongoReader {
	
	/*
	 * Contenu DB Mongo --> modèle RDF
	 */
	public static Model mongoToModel(){
		Model m = ModelFactory.createDefaultModel();
		
		String ns = Config.mongoPrefix;
		m.setNsPrefix("mongo", ns);
		
		// Classe
		Resource commune = m.createResource(ns + "Commune");
		
		// Propriétés
		Property codeInsee = m.createProperty(ns + "code_insee");
		
		Property nbRedevables2002 = m.createProperty(ns + "nb_redevables_isf_2002");
		Property patrimoineMoyen2002 = m.createProperty(ns + "patrimoine_moyen_2002");
		Property impotMoyen2002 = m.createProperty(ns + "impot_moyen_2002");
		
		Property nbRedevables2008= m.createProperty(ns + "nb_redevables_isf_2008");
		Property patrimoineMoyen2008 = m.createProperty(ns + "patrimoine_moyen_2008");
		Property impotMoyen2008 = m.createProperty(ns + "impot_moyen_2008");
		
		Property nbRedevables2010 = m.createProperty(ns + "nb_redevables_isf_2010");
		Property patrimoineMoyen2010 = m.createProperty(ns + "patrimoine_moyen_2010");
		Property impotMoyen2010 = m.createProperty(ns + "impot_moyen_2010");
		
		// Connexion à MongoDB
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient("localhost");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		DB db = mongoClient.getDB(Config.mongoDbName);

		// Parcours de la DB pour remplir le modèle
		try {
			DBCollection impotsTable = db.getCollection(Config.mongoCollectionImpots);
			DBCursor cursor = impotsTable.find();

			DBObject obj;
			while (cursor.hasNext()) {
				obj = cursor.next();
				
				Resource r = m.createResource(ns + obj.get("code_insee").toString());
				m.add(r, RDF.type, commune);
				m.add(r, codeInsee, obj.get("code_insee").toString());
				
				String nb_redev = obj.get("nbre_redevables_isf").toString();
				String imp_moy = obj.get("impot_moy").toString();
				String pat_moy = obj.get("patrimoine_moy").toString();
				int year = Integer.parseInt(obj.get("annee").toString());
				
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Ecriture sur disque
		try {
			FileOutputStream ost = new FileOutputStream(Config.mongoFile);
			
			m.write(ost, "RDF/XML-ABBREV");
		}
			catch (FileNotFoundException e) {
				System.out.println("pb de fichier");
		} 
		return m;
	}

}
