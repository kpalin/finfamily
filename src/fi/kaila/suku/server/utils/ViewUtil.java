package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;


/**
 * <h1>Group Server Utility</h1>
 * 
 * Group removes and additions to / from db are done here
 * 
 * 
 * @author Kalle
 *
 */
public class ViewUtil {

	
	private static Logger logger= Logger.getLogger(ViewUtil.class.getName());
	
	

	private Connection con=null;

	public ViewUtil(Connection con) {
		this.con=con;

	}
	
	public SukuData removeView(int viewId){
		SukuData resu = new SukuData();
		try {
			String sql = "delete from viewunits where vid = ?";
			
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, viewId);
			resu.resuCount = pst.executeUpdate();
				
			sql = "delete from views where vid = ?";
			
			pst = con.prepareStatement(sql);
			pst.setInt(1, viewId);
			pst.executeUpdate();
			
			
		} catch (SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING,"remove view failed",e);
			e.printStackTrace();
		}
		
		
		return resu;
	}

	public SukuData addView(String viewname) {
		SukuData resu = new SukuData();
		try {
			Statement stm = con.createStatement();
			stm = con.createStatement();
			int vid=0;
			ResultSet rs = stm.executeQuery("select nextval('viewseq')");

			if (rs.next()){
				vid = rs.getInt(1);
			} else {
				throw new SQLException ("Sequence viewseq error");
			} 
			rs.close();
			
			String sql = "insert into views (vid,name) values (?,?)";
			
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, vid);
			pst.setString(2, viewname);
			pst.executeUpdate();
			
			
		} catch (SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING,"add view failed",e);
			e.printStackTrace();
		}
		
		
		return resu;
	}

	public SukuData getViews(int pid) {
		SukuData resu = new SukuData();
		try {
			String sql = "select name from views where vid in " +
					"(select vid from viewunits where pid = ?)";
			PreparedStatement stm = con.prepareStatement(sql);
			
			stm.setInt(1, pid);
			ResultSet rs = stm.executeQuery();
			
			Vector<String> vv = new Vector<String>();
			
			while (rs.next()){
				vv.add(rs.getString("name"));
			}
			rs.close();
			resu.generalArray = vv.toArray(new String[0]);
		
			
			
		} catch (SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING,"add view failed",e);
			e.printStackTrace();
		}
		
		
		return resu;
	}

	public SukuData addViewUnits(int vid, int[] pidArray,boolean emptyView) {
		SukuData resu = new SukuData();
		resu.resuCount=0;
		try {
			String sql;
			PreparedStatement stm;
			if (emptyView) {
				sql = "delete from viewunits where vid = ?";
				stm = con.prepareStatement(sql);
				stm.setInt(1,vid);
				stm.executeUpdate();
				
			}
			
			sql = "insert into viewunits (vid,pid) values (?,?)";
			stm = con.prepareStatement(sql);
			
			for (int i = 0; i < pidArray.length; i++) {
				stm.setInt(1, vid);
				stm.setInt(2, pidArray[i]);
				stm.executeUpdate();
				resu.resuCount++;
			}
			stm.close();
		
			
			
		} catch (SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING,"add view failed",e);
			e.printStackTrace();
		}
		
		
		return resu;
	}

	public SukuData addViewDesc(int viewId, int pid, String gent,boolean withSpouses, boolean emptyView) throws SukuException {
		SukuData resp = new SukuData();
		Vector<Integer>pidv = new Vector<Integer>();
		resp.resuCount=0;
		int gen=0;
		if (gent != null && !gent.equals("")){
			gen = Integer.parseInt(gent);
		}
		
		try {
			String sql;
			PreparedStatement stm;
			if (emptyView) {
				sql = "delete from viewunits where vid = ?";
				stm = con.prepareStatement(sql);
				stm.setInt(1,viewId);
				stm.executeUpdate();
				
			}
			
			
			int from=0;
			int to=0;
			sql = "select pid from unit where pid=?";
			stm = con.prepareStatement(sql);
			stm.setInt(1, pid);
			
			ResultSet rs = stm.executeQuery();
			if (rs.next()){
				pidv.add(pid);
				to=pidv.size();
			} else {
				resp.resu="VIEW DESCENDANT NO SUCH PERSON " + pid;
			}
			rs.close();
			int currgen=0;
			do {
				int firstChild=pidv.size();
				for (int i = from; i < to; i++) {
					sql = "select bid " +
							"from child as c inner join unit as u on bid=pid " +
							"where aid=?  ";
					stm = con.prepareStatement(sql);
					stm.setInt(1, pidv.get(i));
					rs = stm.executeQuery();
					
					while (rs.next()){
						pidv.add(rs.getInt(1));
					}
					rs.close();
					
				}
				int lastChild=pidv.size();
				if (withSpouses) {
					for (int i = from; i < to; i++) {
						sql = "select bid " +
								"from spouse as c inner join unit as u on bid=pid " +
								"where aid=?  ";
						stm = con.prepareStatement(sql);
						stm.setInt(1, pidv.get(i));
						rs = stm.executeQuery();
						
						while (rs.next()){
							pidv.add(rs.getInt(1));
						}
						rs.close();
					}	
				}
				from = firstChild;
				to = lastChild;
				currgen++;
			} while (to > from && (gen == 0 || currgen < gen));
			
			
			
			resp.pidArray = new int[pidv.size()];
			sql = "insert into viewunits (vid,pid) values (?,?) ";
			stm = con.prepareStatement(sql);
			for (int i = 0; i < pidv.size(); i++) {
				stm.setInt(1, viewId);
				

				int pidc = pidv.get(i);
				stm.setInt(2,pidc);
				stm.executeUpdate();
				resp.pidArray[i] = pidc;
				resp.resuCount++;
			}
			stm.close();
			
			
			return resp;
			
		} catch (SQLException e) {
			logger.log(Level.WARNING,"SQL error in view with descendants ",e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP",e);
		}
	}

	public SukuData emptyView(int vid) {
		SukuData resu = new SukuData();
		try {
			String sql;
			PreparedStatement stm;
		
			sql = "delete from viewunits where vid = ?";
			stm = con.prepareStatement(sql);
			stm.setInt(1,vid);
			stm.executeUpdate();
				
			
		} catch (SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING,"delete from view failed",e);
			e.printStackTrace();
		}
		
		
		return resu;
	}

	public SukuData removeViewUnits(int vid, int[] pidArray) {
		SukuData resu = new SukuData();
		try {
			String sql;
			PreparedStatement stm;
			
			
			sql = "delete from viewunits where vid=? and pid = ?";
			stm = con.prepareStatement(sql);
			
			for (int i = 0; i < pidArray.length; i++) {
				stm.setInt(1, vid);
				stm.setInt(2, pidArray[i]);
				stm.executeUpdate();
				
			}
			stm.close();
		
			
			
		} catch (SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING,"delete from view failed",e);
			e.printStackTrace();
		}
		
		
		return resu;
	}

	public SukuData addViewAnc(int viewId, int pid, String gent, boolean emptyView) throws SukuException {
		SukuData resp = new SukuData();
		Vector<Integer>pidv = new Vector<Integer>();
		resp.resuCount=0;
		int gen=0;
		if (gent != null && !gent.equals("")){
			gen = Integer.parseInt(gent);
		}
		
		try {
			String sql;
			PreparedStatement stm;
			if (emptyView) {
				sql = "delete from viewunits where vid = ?";
				stm = con.prepareStatement(sql);
				stm.setInt(1,viewId);
				stm.executeUpdate();
				
			}
			
			
			int from=0;
			int to=0;
			sql = "select pid from unit where pid=?";
			stm = con.prepareStatement(sql);
			stm.setInt(1, pid);
			
			ResultSet rs = stm.executeQuery();
			if (rs.next()){
				pidv.add(pid);
				to=pidv.size();
			} else {
				resp.resu="VIEW ANCESTORS NO SUCH PERSON " + pid;
			}
			rs.close();
			
			int currgen=0;
			do {
				int firstPare=pidv.size();
				for (int i = from; i < to; i++) {
					sql = "select bid " +
							"from parent as c inner join unit as u on bid=pid " +
							"where aid=?  ";
					stm = con.prepareStatement(sql);
					stm.setInt(1, pidv.get(i));
					rs = stm.executeQuery();
					
					while (rs.next()){
						pidv.add(rs.getInt(1));
					}
					rs.close();
					
				}
				int lastPare=pidv.size();
			
				from = firstPare;
				to = lastPare;
				currgen++;
			} while (to > from && (gen == 0 || currgen < gen));
			
			
			
			resp.pidArray = new int[pidv.size()];
			sql = "insert into viewunits (vid,pid) values (?,?) ";
			stm = con.prepareStatement(sql);
			for (int i = 0; i < pidv.size(); i++) {
				stm.setInt(1, viewId);
				

				int pidc = pidv.get(i);
				stm.setInt(2,pidc);
				stm.executeUpdate();
				resp.pidArray[i] = pidc;
				resp.resuCount++;
			}
			stm.close();
			
			
			return resp;
			
		} catch (SQLException e) {
			logger.log(Level.WARNING,"SQL error in view with descendants ",e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP",e);
		}
	}
}
