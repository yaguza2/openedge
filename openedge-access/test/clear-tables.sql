# clear tables for oeaccess_user

delete oeaccess_user, oeaccess_group, oeaccess_group_permission,
  oeaccess_resource, oeaccess_user_attribs, oeaccess_user_group,
  oeaccess_user_permission
from oeaccess_user, oeaccess_group, oeaccess_group_permission,
  oeaccess_resource, oeaccess_user_attribs, oeaccess_user_group,
  oeaccess_user_permission;