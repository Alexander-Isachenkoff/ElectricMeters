SELECT DATE,
       T_0 * [T_0:1] / 1000   as T_0,
       T_1 * [T_1:1] / 1000   as T_1,
       T_2 * [T_2:1] / 1000   as T_2,
       T_3 * [T_3:1] / 1000   as T_3,
       T_4 * [T_4:1] / 1000   as T_4,
       T_5 * [T_5:1] / 1000   as T_5,
       T_6 * [T_6:1] / 1000   as T_6,
       T_7 * [T_7:1] / 1000   as T_7,
       T_8 * [T_8:1] / 1000   as T_8,
       T_9 * [T_9:1] / 1000   as T_9,
       T_10 * [T_10:1] / 1000 as T_10,
       T_11 * [T_11:1] / 1000 as T_11,
       T_12 * [T_12:1] / 1000 as T_12,
       T_13 * [T_13:1] / 1000 as T_13,
       T_14 * [T_14:1] / 1000 as T_14,
       T_15 * [T_15:1] / 1000 as T_15,
       T_16 * [T_16:1] / 1000 as T_16,
       T_17 * [T_17:1] / 1000 as T_17,
       T_18 * [T_18:1] / 1000 as T_18,
       T_19 * [T_19:1] / 1000 as T_19,
       T_20 * [T_20:1] / 1000 as T_20,
       T_21 * [T_21:1] / 1000 as T_21,
       T_22 * [T_22:1] / 1000 as T_22,
       T_23 * [T_23:1] / 1000 as T_23
FROM (SELECT SUMMARY_PROFILE.*, TAR3.*
      FROM SUMMARY_PROFILE
               left join TAR3_HOURLY_RATE_VALUES TAR3
                         on TAR2_ID =
                            (select ID
                             from TAR2_VOLTAGE_RATES TAR2
                             where TAR2.TAR1_ID = (select ID
                                                   from TAR1_MONTHLY_RATE TAR1
                                                   where TAR1.YEAR = SUMMARY_PROFILE.YEAR
                                                     and TAR1.MONTH = SUMMARY_PROFILE.MONTH
                                                     and TAR1.RATE_TYPE_ID = ?) -- ID типа ставки
                               and TAR2.VOLTAGE_LEVEL_ID = ?) -- ID уровня напряжения
                             and cast(strftime('%d', DATE) as Integer) = TAR3.DAY_OF_MONTH)
WHERE cast(strftime('%m', DATE) as Integer) = ? -- Месяц
  and cast(strftime('%Y', DATE) as Integer) = ? -- Год