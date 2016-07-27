CREATE TABLE users_preferences (
    user_preferences_id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_user INTEGER REFERENCES users (id_user) UNIQUE,
    user_modify_measurement_position integer
)