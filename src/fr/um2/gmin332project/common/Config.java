package fr.um2.gmin332project.common;

/*
 * Constantes utilisées par l'application
 */
public class Config {
	// Namespace du projet
	public static final String projectNs = "http://www.gmin332.org/";
    
	/* Fichiers modèles */
	public static final String mongoFile = "model/mongo.rdf";
	//public static final String d2rqFile = "model/d2rq.rdf";
	public static final String d2rqFile = "model/d2rq_light.rdf";
	public static final String d2rqFileBuffer = "model/d2rq_temp.rdf";
	public static final String neo4jFile = "model/neo4j.rdf";
	public static final String tdbFile = "model/tdb.rdf";
	public static final String mappingFile = "model/mapping_d2rq_mongo.rdf";
	
	/* Répertoire de stockage TDB */
	public static final String tdbDirectory = "data/tdb/";
	
	/* Config DB Mongo */
	public static final String mongoDbName = "gmin332project";
	public static final String mongoCollectionImpots = "impots";
	
	/* Données Geonames */
	public static final String geo_regions = "data/raw/geonames_regions.rdf";
	public static final String geo_departements = "data/raw/geonames_departements.rdf";  
	public static final String geo_onto = "data/raw/geonames_v3.rdf";
	
	/* Noms des modèles Geonames */
	public static final String modelGeonamesName = "geonames";
	
	/* Mapping D2RQ */
	public static final String d2rqMapFile = "file:data/d2rqmapping.n3";
	
	/* Préfixes */
	public static final String mongoPrefix = projectNs + "mongo#";
	public static final String neoPrefix = projectNs + "neo#";
	public static final String tdbPrefix = projectNs + "tdb#";
	public static final String d2rqPrefix = projectNs + "d2rq#";
}
