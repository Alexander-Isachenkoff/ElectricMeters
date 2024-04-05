-- Итоговое потребление на месяц
select sum(aPos) as VALUE
from PROFILE_STRS_WITH_DEDUCTION
where YEAR  = ? -- Год
  and MONTH = ? -- Месяц