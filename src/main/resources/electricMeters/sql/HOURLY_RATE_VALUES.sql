select val.* from HOURLY_RATE_VALUES val
where (
    select VOLTAGE_LEVEL_ID from HOURLY_POWER_RATE hpr
    where hpr.ID = val.HOURLY_POWER_RATE_ID
    and (select ID from POWER_RATE where YEAR = ? and MONTH = ? and POWER_RATE_TYPE_ID = ?) = hpr.POWER_RATE_ID
) = ?