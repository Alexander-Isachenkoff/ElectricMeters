select sum(aPos) /
       (select count(1)
        from PEAK_HOURS
        where cast(strftime('%Y', PEAK_HOURS.DATE) as Integer) = STR.YEAR
          and cast(strftime('%m', PEAK_HOURS.DATE) as Integer) = STR.MONTH) as POWER
from PROFILE_STRS_WITH_DEDUCTION STR
         join PEAK_HOURS on (PEAK_HOURS.DATE = STR.DATE) and (PEAK_HOURS.HOUR - 1 = STR.HOUR)
where STR.YEAR = ?  -- Год
  and STR.MONTH = ? -- Месяц
