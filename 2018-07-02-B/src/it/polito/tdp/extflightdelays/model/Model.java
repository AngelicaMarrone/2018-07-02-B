package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	//inserire tipo di dao

		private ExtFlightDelaysDAO dao;

		

		//scelta valore mappa

		private Map<Integer,Airport> idMap;

		

		//scelta tipo valori lista

		private List<Airport> vertex;

		

		//scelta tra uno dei due edges

		private List<Adiacenza> edges;

		

		//scelta tipo vertici e tipo archi

		private Graph<Airport,DefaultWeightedEdge> graph;
		
		private Map<DefaultWeightedEdge, Integer> numeri;


		public Model() {

			

			//inserire tipo dao

			dao  = new ExtFlightDelaysDAO();

			//inserire tipo values

			idMap = new HashMap<Integer,Airport>();
			
			numeri= new HashMap<DefaultWeightedEdge, Integer>();

		}
	
	public List<Airport> creaGrafo(Integer m) {
		//scelta tipo vertici e archi

				graph = new SimpleWeightedGraph<Airport,DefaultWeightedEdge>(DefaultWeightedEdge.class);

				

				//scelta tipo valori lista

				vertex = new ArrayList<Airport>(dao.getVertex(m, idMap));

				Graphs.addAllVertices(graph,vertex);

				

				edges = new ArrayList<Adiacenza>(dao.getEdges());

				

				for(Adiacenza a : edges) {

					

					//CASO BASE POTRESTI DOVER AGGIUNGERE CONTROLLI

					Airport source = idMap.get(a.getId1());

					Airport target = idMap.get(a.getId2());

					double peso = a.getPeso();
					
					
					if(source != null && target != null)

					{
						DefaultWeightedEdge e= graph.getEdge(target, source);
						
						if (e==null)
					{DefaultWeightedEdge n= Graphs.addEdge(graph,source,target,peso);
						numeri.put(n, a.getNum());

					System.out.println("AGGIUNTO ARCO TRA: "+source.toString()+" e "+target.toString());}
						else {	
					double nuovoPeso= calcolaNuovoPeso(e,peso, a.getNum());
					graph.setEdgeWeight(e, nuovoPeso);}
					
					}

					

				}

				

				System.out.println("#vertici: "+graph.vertexSet().size());

				System.out.println("#archi: "+graph.edgeSet().size());

				return vertex;
				
		
	}

	private double calcolaNuovoPeso(DefaultWeightedEdge e, double peso, int i) {
		double peso1= graph.getEdgeWeight(e);
		double peso2= peso;
		int num2= i;
		int num1= numeri.get(e);
		
		double media= ((peso1*num1)+ (peso2*num2))/(num1+num2);
		
		
		return media;
	}

	public String trovaComponenteConnessa(Airport a) {
		
		List<Airport> vicini= Graphs.neighborListOf(graph, a);
		List<ViciniPeso> vp= new ArrayList<ViciniPeso>(); 
		
		
		for( Airport v: vicini)
		{
			
			DefaultWeightedEdge e= graph.getEdge(a, v);
			double peso= graph.getEdgeWeight(e);
			ViciniPeso p= new ViciniPeso(v,peso);
			vp.add(p);
			
			
		}
		
		Collections.sort(vp);
		
		String ris= "Elenco vicini con peso crescente:\n";
		
		for (ViciniPeso p: vp)
		{
			ris+= p.getA().getAirportName()+ " "+p.getPeso()+ "\n";
		}
		
		return ris;
	}

}
