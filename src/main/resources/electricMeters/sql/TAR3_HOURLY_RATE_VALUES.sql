select * from TAR3_HOURLY_RATE_VALUES
where TAR2_ID =
      (select ID from TAR2_VOLTAGE_RATES TAR2
       where TAR2.TAR1_ID = (select ID from TAR1_MONTHLY_RATE where YEAR = ? and MONTH = ? and RATE_TYPE_ID = ?)
         and VOLTAGE_LEVEL_ID = ?)