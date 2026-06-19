package org.example.dao;

import org.example.domain.AuditTrail;
import org.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditTrailDAO {

    public List<AuditTrail> getAuditLogs() {
        List<AuditTrail> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_trail ORDER BY action_timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AuditTrail log = new AuditTrail(
                        rs.getInt("audit_id"),
                        rs.getString("table_name"),
                        rs.getString("action_type"),
                        rs.getString("details"),
                        rs.getTimestamp("action_timestamp")
                );
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching audit logs!");
            e.printStackTrace();
        }
        return logs;
    }
}
