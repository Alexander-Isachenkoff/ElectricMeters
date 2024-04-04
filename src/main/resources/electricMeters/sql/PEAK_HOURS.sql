select *
from PEAK_HOURS
where cast(strftime('%Y', DATE) as Integer) = ?
  and cast(strftime('%m', DATE) as Integer) = ?