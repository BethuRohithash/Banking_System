package com.cognizant.accounts.constants;

public class AccountsConstants {

	private AccountsConstants() {
		
	}
	// Status Codes
    public static final String SUCCESS_CODE = "200";
    public static final String CREATED_CODE = "201";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String NOT_FOUND_CODE = "404";
    public static final String SERVER_ERROR_CODE = "500";

    // Status Messages
    public static final String ACCOUNT_CREATED = "Account has been created successfully.";
    public static final String ACCOUNT_NOT_CREATED = "Account has not created successfully.";
    public static final String UPDATE_CREATED = "Account Information changed successfully.";
    public static final String UPDATE_NOT_CREATED = "Account Infromation Not Changed Pls Try Again!!!.";
    public static final String ACCOUNT_UPDATED = "Account balance updated successfully.";
    public static final String ACCOUNT_DEACTIVATED = "Account has been deactivated.";
    public static final String ACCOUNT_NOT_FOUND = "No account found with the provided ID.";
}
