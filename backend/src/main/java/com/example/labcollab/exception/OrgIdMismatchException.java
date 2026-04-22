package com.example.labcollab.exception;

public class OrgIdMismatchException extends BadRequestException {
    public OrgIdMismatchException() {
        super("X-Org-Id mismatch with path orgId");
    }
}
