select * from VERIFICATIONS
where DATE >= coalesce(?, '1900-01-01') and DATE <= coalesce(?, '3000-12-31')