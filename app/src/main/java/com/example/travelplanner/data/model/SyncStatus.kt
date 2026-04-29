package com.example.travelplanner.data.model

/**
 * List of possible synchronization statuses for the object with the server
 *  “pending” — awaiting submission,
 *  “synced” — successfully synchronized,
 *  “error” — network error.
 */
enum class SyncStatus {
    PENDING,
    SYNCED,
    ERROR
}