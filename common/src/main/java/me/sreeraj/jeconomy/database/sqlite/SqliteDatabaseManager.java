package me.sreeraj.jeconomy.database.sqlite;

import me.sreeraj.jeconomy.JEconomy;
import me.sreeraj.jeconomy.config.JEconomyConfig;
import me.sreeraj.jeconomy.database.DatabaseManager;
import static me.sreeraj.jeconomy.JEconomyUtils.logPrint;
import static me.sreeraj.jeconomy.JEconomy.MOD_ID;

import java.io.File;
import java.sql.*;


public class SqliteDatabaseManager implements DatabaseManager
{
    public static String url;

    public static void createNewDatabase(File file) {
        url = "jdbc:sqlite:" + file.getPath().replace('\\', '/');

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        createNewTable();
    }

    public Connection connect() {
        Connection conn = null;
        try {
            logPrint("["+MOD_ID+"] DatabaseManger(addPLayer) -> "+  url);
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void createNewTable() {
        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
            for (String query : JEconomy.TableReg) {
                stmt.execute(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void addPlayer(String uuid, String name) {
        String sql = "INSERT INTO JEconomy(UUID,Name,Money) VALUES(?,?,?)";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, uuid);
            pstmt.setString(2, name);
            pstmt.setInt(3, JEconomyConfig.startingMoney);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            updateName(uuid, name);
        }
    }

    @Override
    public void removePlayer(String uuid, String name) {
        String sql = "DELETE FROM JEconomy WHERE UUID=? AND Name=?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, uuid);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            updateName(uuid, name);
        }
    }

    @Override
    public void updateName(String uuid, String name) {
        String sql = "UPDATE JEconomy SET Name = ? WHERE UUID = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, uuid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setName(String uuid, String name) {
        String sql = "UPDATE JEconomy SET Name = ? WHERE UUID != ? AND Name = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "a");
            pstmt.setString(2, uuid);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNameFromUUID(String uuid) {
        String sql = "SELECT UUID, Name FROM JEconomy WHERE UUID = '" + uuid + "'";

        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            rs.next();
            return rs.getString("Name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public int getBalanceFromUUID(String uuid) {
        String sql = "SELECT UUID, Money FROM JEconomy WHERE UUID = '" + uuid + "'";

        try (Connection conn = this.connect(); Statement stmt  = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            rs.next();
            return rs.getInt("Money");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getBalanceFromName(String name) {
        String sql = "SELECT Name, Money FROM JEconomy WHERE Name = '" + name + "'";

        try (Connection conn = this.connect(); Statement stmt  = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            rs.next();
            return rs.getInt("Money");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean setBalance(String uuid, int money) {
        String sql = "UPDATE JEconomy SET Money = ? WHERE UUID = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (money >= 0 && money < Integer.MAX_VALUE) {
                pstmt.setInt(1, money);
                pstmt.setString(2, uuid);
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setAllBalance(int money) {
        String sql = "UPDATE JEconomy SET Money = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (money >= 0 && money < Integer.MAX_VALUE) {
                pstmt.setInt(1, money);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean changeBalance(String uuid, int money) {
        String sql = "UPDATE JEconomy SET Money = ? WHERE UUID = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int bal = getBalanceFromUUID(uuid);
            if (bal + money >= 0 && bal + money < Integer.MAX_VALUE) {
                pstmt.setInt(1, bal + money);
                pstmt.setString(2, uuid);
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public void changeAllBalance(int money) {
        String sql = "UPDATE JEconomy SET Money = Money + " + money + " WHERE " + Integer.MAX_VALUE + " > Money + " + money + " AND 0 <= Money + " + money;

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String top(String uuid, int page) {
        String sql = "SELECT UUID, Name, Money FROM JEconomy ORDER BY Money DESC";
        String rankings = "";
        int i = 0;
        int playerRank = 0;
        int repeats = 0;

        try (Connection conn = this.connect(); Statement stmt  = conn.createStatement(); ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next() && (repeats < 10 || playerRank == 0)) {
                if (repeats / 10 + 1 == page) {
                    rankings = rankings.concat(rs.getRow() + ") " + rs.getString("Name") + ": $" + rs.getInt("Money") + "\n");
                    i++;
                }
                repeats++;
                if (uuid.equals(rs.getString("uuid"))) {
                    playerRank = repeats;
                }
            }
            if (i < 10) {
                rankings = rankings.concat("---End--- \n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rankings.concat("Your rank is: " + playerRank);
    }

    @Override
    public String rank(int rank) {
        int repeats = 0;
        String sql = "SELECT Name FROM JEconomy ORDER BY Money DESC";
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next() ) {
                repeats++;
                if (repeats == rank) {
                    return rs.getString("Name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No Player";
    }

    @Override
    public int playerRank(String uuid) {
        String sql = "SELECT UUID FROM JEconomy ORDER BY Money DESC";
        int repeats = 1;

        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            rs.next();
            while (!rs.getString("UUID").equals(uuid)) {
                rs.next();
                repeats++;
            }
            return repeats;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
