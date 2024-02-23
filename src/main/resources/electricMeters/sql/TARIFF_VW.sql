select * from TARIFF_VW
where (? is NULL or YEAR = ?)
  and (? is NULL or MONTH = ?)
  and (? is NULL or TYPE_ID = ?)
  and (? is NULL or VL_ID = ?)