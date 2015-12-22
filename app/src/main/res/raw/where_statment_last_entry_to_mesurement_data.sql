measurement_id  IN
(
select measurement_id
FROM
  (
    Select measurement_id
    FROM measurement_data
    WHERE id_user = ?
    order by measurement_id DESC
    limit 1
   ) thelatesEntry
);