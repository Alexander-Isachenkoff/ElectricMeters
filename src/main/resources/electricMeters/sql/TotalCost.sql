select coalesce(sum(T_0) +
                sum(T_1) +
                sum(T_2) +
                sum(T_3) +
                sum(T_4) +
                sum(T_5) +
                sum(T_6) +
                sum(T_7) +
                sum(T_8) +
                sum(T_9) +
                sum(T_10) +
                sum(T_11) +
                sum(T_12) +
                sum(T_13) +
                sum(T_14) +
                sum(T_15) +
                sum(T_16) +
                sum(T_17) +
                sum(T_18) +
                sum(T_19) +
                sum(T_20) +
                sum(T_21) +
                sum(T_22) +
                sum(T_23), 0) as SUMMARY_COST
from (select *
      from SUMMARY_COST
      where MONTH = ?        -- Месяц
        and YEAR = ?         -- Год
        and RATE_TYPE_ID = ? -- ID типа ставки
        and VOLTAGE_LEVEL_ID = ? -- ID уровня напряжения
     )