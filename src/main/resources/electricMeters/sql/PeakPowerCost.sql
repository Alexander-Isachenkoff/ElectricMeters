select (CAP_RATE * POWER / 1000) as POWER_COST
from (select sum(aPos) /
             (select count(1)
              from PEAK_HOURS
              where cast(strftime('%Y', PEAK_HOURS.DATE) as Integer) = STR.YEAR
                and cast(strftime('%m', PEAK_HOURS.DATE) as Integer) = STR.MONTH) as POWER,
             (select CAP_RATE
              from TAR1_MONTHLY_RATE
              where RATE_TYPE_ID = ? -- ID типа ставки
                and YEAR = STR.YEAR
                and MONTH = STR.MONTH)                                            as CAP_RATE
      from PROFILE_STRS_WITH_DEDUCTION STR
               join PEAK_HOURS on (PEAK_HOURS.DATE = STR.DATE) and (PEAK_HOURS.HOUR - 1 = STR.HOUR)
      where STR.YEAR = ? -- Год
        and STR.MONTH = ? -- Месяц
     )
