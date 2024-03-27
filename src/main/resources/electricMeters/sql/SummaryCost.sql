select * from SUMMARY_COST
where MONTH = ?              -- Месяц
    and YEAR = ?             -- Год
    and RATE_TYPE_ID = ?     -- ID типа ставки
    and VOLTAGE_LEVEL_ID = ? -- ID уровня напряжения