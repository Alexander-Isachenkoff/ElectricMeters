-- Итоговое потребление на месяц
select sum(aPos) as VALUE
from PROFILE_STRS_WITH_DEDUCTION
where cast(strftime('%m', DATE) as Integer) = ? -- месяц
  and cast(strftime('%Y', DATE) as Integer) = ? -- год