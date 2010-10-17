package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * <h1>Group Server Utility</h1>
 * 
 * Group removes and additions to / from db are done here.
 * 
 * @author Kalle
 */
public class GroupUtil {

	private static Logger logger = Logger.getLogger(GroupUtil.class.getName());

	private Connection con = null;

	/**
	 * Constructor for this server class.
	 * 
	 * @param con
	 *            the con
	 */
	public GroupUtil(Connection con) {
		this.con = con;

	}

	/**
	 * Remove groupid from all persons in database.
	 * 
	 * @return in SukuData as resu the # of removed groupid's
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData removeAllGroups() throws SukuException {
		SukuData resp = new SukuData();
		ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			Statement stm = con.createStatement();
			ResultSet rs = stm
					.executeQuery("select pid from unit where groupid is not null");
			while (rs.next()) {
				int pid = rs.getInt(1);
				pidv.add(pid);
			}
			rs.next();
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}

			int lukuri = stm
					.executeUpdate("update unit set groupid = null where groupid is not null");
			if (lukuri != resp.pidArray.length) {
				resp.resu = "GROUPS REMOVED[" + resp.pidArray.length
						+ "]; RESULT[" + lukuri + "]";
			}
			stm.close();
			return resp;

		} catch (SQLException e) {
			logger.log(Level.WARNING, "SQL error in all remove", e);
			e.printStackTrace();
			throw new SukuException("REMOVE GROUP", e);
		}

	}

	/**
	 * Removes group from persons in array.
	 * 
	 * @param pids
	 *            the pids
	 * @return response as a SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData removeSelectedGroups(int[] pids) throws SukuException {
		SukuData resp = new SukuData();

		ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			PreparedStatement stm = con
					.prepareStatement("update unit set groupid = null where pid = ? and groupid is not null");
			// int lukuri =
			// stm.executeUpdate("update unit set groupid = null where groupid is not null");
			for (int i = 0; i < pids.length; i++) {
				stm.setInt(1, pids[i]);
				int lukuri = stm.executeUpdate();
				if (lukuri == 1) {
					pidv.add(pids[i]);
				}

			}
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			return resp;

		} catch (SQLException e) {
			logger.log(Level.WARNING, "SQL error in selected remove", e);
			e.printStackTrace();
			throw new SukuException("REMOVE GROUP", e);
		}

	}

	/**
	 * remove group from persons in view.
	 * 
	 * @param viewid
	 *            the viewid
	 * @return response as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData removeViewGroups(int viewid) throws SukuException {
		SukuData resp = new SukuData();

		ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			PreparedStatement stm = con
					.prepareStatement("select pid from unit "
							+ "where pid in (select pid from viewunits where vid = ?) "
							+ "and groupid is not null ");
			stm.setInt(1, viewid);
			ResultSet rs = stm.executeQuery();

			while (rs.next()) {
				int pid = rs.getInt(1);
				pidv.add(pid);
			}
			rs.next();
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			stm = con.prepareStatement("update unit set groupid = null "
					+ "where pid in (select pid from viewunits where vid = ?) "
					+ "and groupid is not null ");
			stm.setInt(1, viewid);
			int lukuri = stm.executeUpdate();
			if (lukuri != resp.pidArray.length) {
				resp.resu = "GROUPS REMOVED[" + resp.pidArray.length
						+ "]; RESULT[" + lukuri + "]";
			}

			stm.close();
			return resp;

		} catch (SQLException e) {
			logger.log(Level.WARNING, "SQL error in selected remove", e);
			e.printStackTrace();
			throw new SukuException("REMOVE GROUP", e);
		}

	}

	/**
	 * Removes the group.
	 * 
	 * @param group
	 *            the group
	 * @return response as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData removeGroup(String group) throws SukuException {
		SukuData resp = new SukuData();

		ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			PreparedStatement stm = con
					.prepareStatement("select pid from unit where groupid = ? ");
			stm.setString(1, group);
			ResultSet rs = stm.executeQuery();

			while (rs.next()) {
				int pid = rs.getInt(1);
				pidv.add(pid);
			}
			rs.next();
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			stm = con
					.prepareStatement("update unit set groupid = null where groupid = ? ");
			stm.setString(1, group);
			int lukuri = stm.executeUpdate();
			if (lukuri != resp.pidArray.length) {
				resp.resu = "GROUPS REMOVED[" + resp.pidArray.length
						+ "]; RESULT[" + lukuri + "]";
			}
			stm.close();

			return resp;

		} catch (SQLException e) {
			logger.log(Level.WARNING, "SQL error in selected remove", e);
			e.printStackTrace();
			throw new SukuException("REMOVE GROUP", e);
		}
	}

	/**
	 * Add group to listed persons if group is null.
	 * 
	 * @param pidArray
	 *            the pid array
	 * @param group
	 *            the group
	 * @return result in a SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addSelectedGroups(int[] pidArray, String group)
			throws SukuException {
		SukuData resp = new SukuData();

		ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			PreparedStatement stm = con
					.prepareStatement("update unit set groupid = ? where pid = ? and groupid is null");
			// int lukuri =
			// stm.executeUpdate("update unit set groupid = null where groupid is not null");
			for (int i = 0; i < pidArray.length; i++) {
				stm.setString(1, group);
				stm.setInt(2, pidArray[i]);
				int lukuri = stm.executeUpdate();
				if (lukuri == 1) {
					pidv.add(pidArray[i]);
				}

			}
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			return resp;

		} catch (SQLException e) {
			logger.log(Level.WARNING, "SQL error in selected add group", e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP", e);
		}
	}

	/**
	 * Add group to persons in view if group is null.
	 * 
	 * @param vid
	 *            the vid
	 * @param group
	 *            the group
	 * @return result as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addViewGroups(int vid, String group) throws SukuException {
		SukuData resp = new SukuData();

		ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			PreparedStatement stm = con
					.prepareStatement("select pid from unit "
							+ "where pid in (select pid from viewunits where vid = ?) "
							+ "and groupid is null ");
			stm.setInt(1, vid);
			ResultSet rs = stm.executeQuery();

			while (rs.next()) {
				int pid = rs.getInt(1);
				pidv.add(pid);
			}
			rs.next();
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			stm = con.prepareStatement("update unit set groupid = ? "
					+ "where pid in (select pid from viewunits where vid = ?) "
					+ "and groupid is null ");
			stm.setString(1, group);
			stm.setInt(2, vid);
			int lukuri = stm.executeUpdate();
			if (lukuri != resp.pidArray.length) {
				resp.resu = "GROUPS ADDED[" + resp.pidArray.length
						+ "]; RESULT[" + lukuri + "]";
			}
			stm.close();

			return resp;

		} catch (SQLException e) {
			logger.log(Level.WARNING, "SQL error in view add group", e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP", e);
		}
	}

	/**
	 * add group to persons and his/her descendants.
	 * 
	 * @param pid
	 *            the pid
	 * @param group
	 *            the group
	 * @param gent
	 *            the gent
	 * @param includeSpouses
	 *            the include spouses
	 * @return as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addDescendantsToGroup(int pid, String group, String gent,
			boolean includeSpouses) throws SukuException {
		SukuData resp = new SukuData();
		resp.resuCount = 0;
		ArrayList<Integer> pidv = new ArrayList<Integer>();
		int gen = 0;
		if (gent != null && !gent.isEmpty()) {
			gen = Integer.parseInt(gent);
		}

		try {
			int from = 0;
			int to = 0;
			String sql = "select pid,groupid from unit where pid=?";
			PreparedStatement stm = con.prepareStatement(sql);
			stm.setInt(1, pid);
			boolean includeSubject = false;
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {

				pidv.add(pid);
				to = pidv.size();
				includeSubject = rs.getString(2) == null;

			} else {
				resp.resu = "GROUP DESCENDANT NO SUCH PERSON " + pid;
			}
			rs.close();
			int currGen = 0;
			do {
				int firstChild = pidv.size();
				for (int i = from; i < to; i++) {
					sql = "select bid "
							+ "from child as c inner join unit as u on bid=pid "
							+ "where aid=? and groupid is null ";
					stm = con.prepareStatement(sql);
					stm.setInt(1, pidv.get(i));
					rs = stm.executeQuery();

					while (rs.next()) {
						pidv.add(rs.getInt(1));
					}
					rs.close();

				}
				int lastChild = pidv.size();
				if (includeSpouses) {
					for (int i = from; i < to; i++) {
						sql = "select bid "
								+ "from spouse as c inner join unit as u on bid=pid "
								+ "where aid=? and groupid is null ";
						stm = con.prepareStatement(sql);
						stm.setInt(1, pidv.get(i));
						rs = stm.executeQuery();

						while (rs.next()) {
							pidv.add(rs.getInt(1));
						}
						rs.close();
					}
				}
				from = firstChild;
				to = lastChild;
				currGen++;
			} while (to > from && (gen == 0 || currGen < gen));
			if (!includeSubject) {
				pidv.remove(0);
			}
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				sql = "update unit set groupid = ? where pid = ?";
				stm = con.prepareStatement(sql);
				stm.setString(1, group);
				int pidc = pidv.get(i);
				stm.setInt(2, pidc);
				stm.executeUpdate();
				resp.pidArray[i] = pidc;
				resp.resuCount++;
			}
			stm.close();

			return resp;

		} catch (SQLException e) {
			logger.log(Level.WARNING, "SQL error in view with descendants ", e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP", e);
		}
	}

	/**
	 * Add group to person and his ancestors.
	 * 
	 * @param pid
	 *            the pid
	 * @param group
	 *            the group
	 * @param gent
	 *            the gent
	 * @return as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addAncestorsToGroup(int pid, String group, String gent)
			throws SukuException {
		SukuData resp = new SukuData();
		resp.resuCount = 0;
		ArrayList<Integer> pidv = new ArrayList<Integer>();
		int gen = 0;
		if (gent != null && !gent.isEmpty()) {
			gen = Integer.parseInt(gent);
		}

		try {
			int from = 0;
			int to = 0;
			String sql = "select pid,groupid from unit where pid=?";
			PreparedStatement stm = con.prepareStatement(sql);
			stm.setInt(1, pid);
			boolean includeSubject = false;
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {

				pidv.add(pid);
				to = pidv.size();
				includeSubject = rs.getString(2) == null;

			} else {
				resp.resu = "GROUP ANCESTOR NO SUCH PERSON " + pid;
			}
			rs.close();
			int currGen = 0;
			do {
				int firstChild = pidv.size();
				for (int i = from; i < to; i++) {
					sql = "select bid "
							+ "from parent as c inner join unit as u on bid=pid "
							+ "where aid=? and groupid is null ";
					stm = con.prepareStatement(sql);
					stm.setInt(1, pidv.get(i));
					rs = stm.executeQuery();

					while (rs.next()) {
						pidv.add(rs.getInt(1));
					}
					rs.close();

				}
				int lastChild = pidv.size();

				from = firstChild;
				to = lastChild;
				currGen++;
			} while (to > from && (gen == 0 || currGen < gen));
			if (!includeSubject) {
				pidv.remove(0);
			}
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				sql = "update unit set groupid = ? where pid = ?";
				stm = con.prepareStatement(sql);
				stm.setString(1, group);
				int pidc = pidv.get(i);
				stm.setInt(2, pidc);
				stm.executeUpdate();
				resp.pidArray[i] = pidc;
				resp.resuCount++;
			}
			stm.close();

			return resp;

		} catch (SQLException e) {
			logger.log(Level.WARNING, "SQL error in view with descendants ", e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP", e);
		}
	}
}
