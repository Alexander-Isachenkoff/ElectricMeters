select * from REF_METERS_VW
where (not IS_ARCHIVE)
   or (? = 0)