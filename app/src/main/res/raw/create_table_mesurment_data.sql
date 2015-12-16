CREATE TABLE measurement_data (
    measurement_id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_user INTEGER REFERENCES users (id_user),
    date_time varchar(30),
    weight real
)