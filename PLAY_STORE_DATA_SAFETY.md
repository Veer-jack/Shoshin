# Shoshin - Play Store Data Safety Answers

## Does your app collect or share any of the required user data types?
YES

## Data Types Collected:

### Location
- Collected: YES
- Approximate location: YES
- Precise location: YES
- Why: Checkpoint verification (verifying user is at gym/location)
- Shared with third parties: NO
- Required or optional: OPTIONAL (user can skip location checkpoints)
- Encrypted in transit: YES
- User can request deletion: YES

### Photos and Videos
- Collected: YES
- Photos: YES
- Why: Checkpoint proof photos
- Shared with third parties: NO
- Required or optional: OPTIONAL (user can skip photo proof)
- Encrypted in transit: YES
- User can request deletion: YES

### Contacts
- Collected: YES
- Why: Suggest friends who are on Shoshin
- Shared with third parties: NO
- Required or optional: OPTIONAL (user can decline contacts permission)
- Encrypted in transit: YES
- User can request deletion: YES

### Personal Info
- Name: YES (display name for profile)
- Phone number: YES (authentication)
- Why: Account creation and identification
- Shared with third parties: NO
- Required or optional: REQUIRED for account
- Encrypted in transit: YES
- User can request deletion: YES

### App Activity
- App interactions: YES
- In-app search history: NO
- Why: Analytics to improve app
- Shared with third parties: NO (Firebase Analytics internal only)
- Required or optional: REQUIRED
- Encrypted in transit: YES

## Security Practices:
- Data encrypted in transit: YES (HTTPS/TLS)
- Data encrypted at rest: YES (Firebase encryption)
- Users can request data deletion: YES (in-app + email)
- App follows Families Policy: NO (not primarily for children)
- Independent security review: NO
