package it.polito.tdp.extflightdelays.model;

public class ViciniPeso implements Comparable<ViciniPeso>{
	
	private Airport a;
	private Double peso;

	public ViciniPeso(Airport a, Double peso) {
		super();
		this.a = a;
		this.peso = peso;
	}

	public Airport getA() {
		return a;
	}

	public void setA(Airport a) {
		this.a = a;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}

	@Override
	public int compareTo(ViciniPeso altro) {
		
		if ((this.peso-altro.getPeso())>0)
		return 1;
		
		return -1;
	}
	
	
}
