select * from HOURLY_RATE_VALUES
where HOURLY_POWER_RATE_ID =
      (select ID from HOURLY_POWER_RATE
       where POWER_RATE_ID = (select ID from POWER_RATE where YEAR = ? and MONTH = ? and POWER_RATE_TYPE_ID = ?)
         and VOLTAGE_LEVEL_ID = ?)