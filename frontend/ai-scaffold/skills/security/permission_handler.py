from typing import Dict, Any


class PermissionHandler:
    def __init__(self):
        self.permissions = {
            "admin": ["user:read", "user:write", "user:delete", "order:read", "order:write"],
            "user": ["user:read", "order:read", "order:write"],
            "guest": ["user:read"]
        }

    def check_permission(self, role: str, permission: str) -> Dict[str, Any]:
        allowed_permissions = self.permissions.get(role, [])
        return {
            "allowed": permission in allowed_permissions,
            "role": role,
            "permission": permission
        }
