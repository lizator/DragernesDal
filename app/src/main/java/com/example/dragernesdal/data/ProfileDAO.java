package com.example.dragernesdal.data;


import com.example.dragernesdal.data.model.ProfileDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileDAO {
    private final SQLDatabaseIO db = new SQLDatabaseIO("andro", "andro", "127.0.0.1", 3306);

    public ProfileDAO() {
    }

    public ProfileDTO getProfileByEmail(String email) throws Exception{
        try {
            db.connect();
            ResultSet rs = db.query("SELECT * FROM user WHERE email = '" + email + "'");
            db.close();

            rs.next();
            ProfileDTO user = new ProfileDTO();
            setUser(rs, user);
            return user;

        } catch (SQLException e) {
            throw new SQLException("Error in Database");
        }

    }

    public boolean getConnected() throws SQLException {
        db.connect();
        return true;
    }

    private void setUser(ResultSet rs, ProfileDTO user) throws SQLException {
        user.setId(rs.getInt("idUser"));
        user.setFirstName(rs.getString("firstName"));
        user.setLastName(rs.getString("secoundName"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getInt("phone"));
        user.setPassHash(rs.getString("passHash"));
        user.setSalt(rs.getString("salt"));
        user.setAdmin(rs.getBoolean("admin"));
    }

}
