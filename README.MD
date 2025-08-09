# Debt Payout Processing Service

This service imports daily payout files from government sources (currently supporting **Wakanda** format), parses them into structured payouts, sends them to an external debt collection API, and persists any failed deliveries for later retry.

It also includes manual trigger endpoints for end-to-end testing and operational support.

## Tech stack :
- Java 21 
- Spring boot
- In memory h2 DB

## In memory DB link
http://localhost:8080/h2-console/

## To run the manual trigger apis after running the application:
POST http://localhost:8080/payouts/process-payout
Content-Type: application/json
Accept: application/json

####
POST http://localhost:8080/payouts/retry-payout
Content-Type: application/json
Accept: application/json

## please note that you need to add new files or rename the existing one with the date -1 day of current date so that the job can retrieve a file to work on
## Files directory : payout-files-directory
