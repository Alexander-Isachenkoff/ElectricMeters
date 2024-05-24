select *
from VERIFICATIONS
where exists(select 1 from VERIFICATION_METERS where VERIFICATION_ID = VERIFICATIONS.ID and METER_ID = ?)
order by DATE desc