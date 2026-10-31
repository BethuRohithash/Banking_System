package com.cognizant.transaction.constants;


public class TransactionConstants {

    private TransactionConstants() {
        // Prevent instantiation
    }

    // Status Codes
    public static final String SUCCESS_CODE = "200";
    public static final String CREATED_CODE = "201";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String NOT_FOUND_CODE = "404";
    public static final String SERVER_ERROR_CODE = "500";

    // Status Messages
    public static final String TRANSACTION_SUCCESS = "Transaction completed successfully.";
    public static final String TRANSACTION_FAILED = "Transaction failed. Please try again.";
    public static final String TRANSACTION_CREATED = "Transaction has been created successfully.";
    public static final String TRANSACTION_UPDATED = "Transaction Updated Successfully";
    public static final String TRANSACTION_UPDATION_FAILED = "Updatation failed";
    public static final String TRANSACTION_DELETION = "Transaction Cancelled Successfully";
    public static final String TRANSACTION_DELETION_FAILED = "Deletion failed";
    public static final String TRANSACTION_NOT_FOUND = "No transaction found with the provided ID.";
}

