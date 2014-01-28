package fr.um2.gmin332project.d2rq;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openjena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.d2rq.jena.ModelD2RQ;
import fr.um2.gmin332project.controller.Config;

/*
 * Classe permettant d'interagir avec D2RQ en Java (POJO Commune en particulier)
 * Requêtes paramétrées, ...
 * Non utilisée pour la manipulation de modèles RDF
 */
public class D2RQAdapter {
    public static final String NL = System.getProperty("line.separator");
   
    private Model d2rqModel;
   
	public D2RQAdapter() {
		super();
		d2rqModel = new ModelD2RQ(Config.d2rqMapFile); 
	}

	public void queryTest() {
		String prfx1 = "PREFIX gdc: <" + Config.projectNs + "d2rq#>";
        String prfx2 = "PREFIX rdf: <" + RDF.getURI() + ">" ;
       
        // requete SPARQL
        String Q = prfx1 + NL + prfx2 + NL +  
          		 "SELECT ?code_insee ?nom_commune ?code_dep ?nom_dep WHERE {" +
                 "  ?commune gdc:com_nom ?nom_commune;" +
                 "           gdc:com_dep ?code_dep;" +
                 "           gdc:com_insee ?code_insee ." +
                 "  ?code_dep gdc:dep_nom ?nom_dep" +
        	     "  FILTER(regex(?nom_dep, \"H??rault\", \"i\")) " +
                // Limite pour la dur??e d'ex??cution
          		"} LIMIT 100";  
        
        Query query = QueryFactory.create(Q) ;
        //query.serialize(new IndentedWriter(System.out, true)) ;
        System.out.println("");
      
        QueryExecution qexec = QueryExecutionFactory.create(query, d2rqModel) ;

        System.out.println("Q1 : communes de l'H??rault") ;
        System.out.println("==================================");
        try {             
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out(results);
        }
        finally {
             qexec.close() ;
        }    	
		
	}
	
	/* Liste de communes d'un département */
	public List<Commune> queryCommunesDunDepartement(String codeDep) {
		String prfx1 = "PREFIX gdc: <" + Config.projectNs + "d2rq#>";
        String prfx2 = "PREFIX rdf: <" + RDF.getURI() + ">" ;

        // requete SPARQL
        String Q = prfx1 + NL + prfx2 + NL +   
       		 "SELECT ?code_insee ?nom_commune ?code_dep WHERE {" +
                "  ?commune gdc:com_nom ?nom_commune;" +
                "           gdc:com_cdep ?code_dep;" +
                "           gdc:com_insee ?code_insee" +
       	     	"  FILTER(regex(?code_dep, \"" + codeDep + "\", \"i\")) " +
          		"}";  
        
        Query query = QueryFactory.create(Q) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, d2rqModel) ;

        List<Commune> communes = new ArrayList<Commune>();
        try {             
            ResultSet results = qexec.execSelect();
            
            // Boucle sur les résultats
            while (results.hasNext()) {
            	QuerySolution row = results.nextSolution();
            	
            	communes.add(new Commune(row.getLiteral("nom_commune").getString(),
            			row.getLiteral("code_insee").getString(), 
            			row.getLiteral("code_dep").getString()));
            }
        }
        finally {
             qexec.close() ;
        }
		return communes;    	
	}
   
	/* Filter sur une liste de valeurs pour un champ donn?? (impl??ment?? pour code_insee seulement) */
	public List<Object> queryByFieldValues(String field, Set<String> values) {
		String prfx1 = "PREFIX gdc: <" + Config.projectNs + "d2rq#>";
        String prfx2 = "PREFIX rdf: <" + RDF.getURI() + ">" ;

        // Conversion du set en liste pour acc??s par index
        List<String> valuesIdx = new ArrayList<String>(values);
        
        // Construction du FILTER
        String filter = "FILTER(";
        String operator = "";
        for (int i = 0; i < values.size(); i++) {
        	if (i > 0)
        		operator = "||";
        	else
        		operator = "";
        	filter += operator + " regex(?code_insee, \"" + valuesIdx.get(i) + "\", \"i\") " + NL;
        }
        filter += ")";
        
        // requete SPARQL
        String Q = prfx1 + NL + prfx2 + NL +   
       		 "SELECT ?code_insee ?nom_commune ?code_dep WHERE {" + NL +
                "  ?commune gdc:com_nom ?nom_commune;" + NL +
                "           gdc:com_cdep ?code_dep;" + NL +
                "           gdc:com_insee ?code_insee" + NL +
       	     	filter +
          		"}";  
        
        System.out.println(">> Requ??te :");
        Query query = QueryFactory.create(Q);
        query.serialize(new IndentedWriter(System.out, true));
        QueryExecution qexec = QueryExecutionFactory.create(query, d2rqModel);

        List<Object> communes = new ArrayList<Object>();
        try {             
        	ResultSet results = qexec.execSelect();
            //ResultSetFormatter.out(results);
            // Boucle sur les r??sultats
            //results.
            while (results.hasNext()) {
            	QuerySolution row = results.nextSolution();
            	
            	communes.add(new Commune(row.getLiteral("nom_commune").getString(),
            			row.getLiteral("code_insee").getString(), 
            			row.getLiteral("code_dep").getString()));
            }
        }
        finally {
             qexec.close() ;
        }
		return communes;    	
	}
	
   public static void main(String[] args)  {         
        Model d2rqModel = new ModelD2RQ(Config.d2rqMapFile);
        
     	String prfx1 = "PREFIX gdc: <" + Config.projectNs + "d2rq#>";
        String prfx2 = "PREFIX rdf: <" + RDF.getURI() + ">" ;
        
         // requete SPARQL
         String Q = prfx1 + NL + prfx2 + NL +   
        		 "SELECT ?nom_ville ?code_dep ?nom_dep ?res_dep WHERE {" +
                 "  ?ville gdc:com_nom ?nom_ville;" +
                 "         gdc:com_dep ?res_dep;" +
                 "         gdc:com_cdep ?code_dep ." +
                 "  ?res_dep gdc:dep_nom ?nom_dep" +
                 //"  FILTER ( ?nom_dep = ?cdep)" + 
        	     "  FILTER(regex(?nom_dep, \"Hérault\", \"i\")) " +
                 // Limite pour la durée d'exécution
           		 "} LIMIT 1000";  
         
         Query query = QueryFactory.create(Q) ;
         //query.serialize(new IndentedWriter(System.out, true)) ;
         System.out.println("");
       
         QueryExecution qexec = QueryExecutionFactory.create(query, d2rqModel) ;

         System.out.println("Q1 : communes de l'Hérault") ;
         System.out.println("==================================");
         try {             
             ResultSet results = qexec.execSelect() ;
             ResultSetFormatter.out(results);
         }
         finally {
              qexec.close() ;
         }    	
         
   }

}
