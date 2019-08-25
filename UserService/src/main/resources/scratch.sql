select * from arec_role
where role_name = 'REPORT_USER';

select * from arec_user
where user_name = 'testuser1';

select * from arec_user_role;

select u.user_id,
  u.user_name,
  r.role_id,
  r.role_name,
  ur.ur_id
from arec_user u,
  arec_user_role ur,
  arec_role r
where u.user_id = ur.ur_user
and r.role_id = ur.ur_role;

select a.authority_id,
  a.authority_name,
  r.role_id,
  r.role_name,
  ra.ra_id
from arec_authority a,
  arec_role r,
  arec_role_auth ra
where a.authority_id = ra.ra_authority
and r.role_id = ra.ra_role
order by role_name;
