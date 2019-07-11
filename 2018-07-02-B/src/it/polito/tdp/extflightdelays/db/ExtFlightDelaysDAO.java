package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Adiacenza;
import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"),0);
				result.add(airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> getVertex(Integer m, Map<Integer, Airport> idMap) {
		String sql = "SELECT a.ID, a.IATA_CODE,a.AIRPORT,a.CITY,a.STATE,a.COUNTRY,a.LATITUDE,a.LONGITUDE,a.TIMEZONE_OFFSET, COUNT(f.ID) as cnt " + 
				"FROM flights f, airports a " + 
				"WHERE (f.ORIGIN_AIRPORT_ID=a.ID OR f.DESTINATION_AIRPORT_ID=a.ID) " + 
				"GROUP BY a.id " + 
				"HAVING COUNT(f.ID)>=? ";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("a.ID"), rs.getString("a.IATA_CODE"), rs.getString("a.AIRPORT"),
						rs.getString("a.CITY"), rs.getString("a.STATE"), rs.getString("a.COUNTRY"), rs.getDouble("a.LATITUDE"),
						rs.getDouble("a.LONGITUDE"), rs.getDouble("a.TIMEZONE_OFFSET"), rs.getDouble("cnt"));
				result.add(airport);
				idMap.put(airport.getId(), airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Adiacenza> getEdges() {
		String sql = "SELECT a1.ID, a2.ID, AVG(f.ELAPSED_TIME) as peso, COUNT(f.id) AS cnt " + 
				"FROM flights f, airports a1, airports a2 " + 
				"WHERE (f.ORIGIN_AIRPORT_ID=a1.ID AND f.DESTINATION_AIRPORT_ID=a2.ID) " + 
				"AND a1.ID!=a2.ID " + 
				"GROUP BY a1.id,a2.ID " + 
				"HAVING COUNT(f.ID)>=1 ";
		List<Adiacenza> result = new ArrayList<Adiacenza>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Adiacenza a = new Adiacenza(rs.getInt("a1.ID"),rs.getInt("a2.ID"),rs.getDouble("peso"), rs.getInt("cnt"));
				result.add(a);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}
