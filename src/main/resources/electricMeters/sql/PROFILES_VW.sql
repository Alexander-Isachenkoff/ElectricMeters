select *,
       cast(strftime('%Y', DATE) as Integer) as YEAR,
       cast(strftime('%m', DATE) as Integer) as MONTH
from PROFILES_VW
where YEAR = coalesce(?, YEAR)
  and MONTH = coalesce(?, MONTH)