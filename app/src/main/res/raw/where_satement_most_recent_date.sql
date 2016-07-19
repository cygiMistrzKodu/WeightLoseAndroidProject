measurement_id  IN
(
select measurement_id
FROM
  (
    Select measurement_id
    FROM measurement_data
    WHERE id_user = ?
    order by datetime(date_time) desc
    limit 1
   ) thelatesEntryByDate
);