# Architecture and design decisions

## Overview

Mini Doodle is a lightweight meeting scheduling platform that allows users to manage their availability through time slots and schedule meetings based on those available slots.

The application is implemented using Spring Boot and follows a layered architecture to separate concerns and keep business logic isolated from infrastructure details.

---

## Architecture

The application follows a standard layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
DB
```

### Controller layer

Responsible for exposing REST endpoints and handling HTTP requests.

Controllers:

- UserController
- SlotController
- MeetingController

Controllers contain no business logic and delegate operations to services.

---

### Service layer

Contains all business rules and orchestration logic.

Examples:

- creating and updating slots
- validating time ranges
- preventing overlapping slots
- scheduling meetings
- cancelling meetings
- managing slot statuses

Transaction management is handled using Spring's `@Transactional`.

---

### Repository layer

Uses Spring Data JPA to handle persistence.

Repositories:

- UserRepository
- TimeSlotRepository
- MeetingRepository

A custom repository query is used to detect overlapping slots.

---

## Domain model

### User

Represents a platform user.

Attributes:

- id
- name
- email

A user can own multiple time slots.

---

### TimeSlot

Represents a period in a user's calendar.

Attributes:

- id
- startTime
- endTime
- status
- user

Statuses:

- FREE
- BUSY
- BOOKED

A slot may be converted into a meeting.

---

### Meeting

Represents a scheduled meeting.

Attributes:

- id
- title
- description
- startTime
- endTime
- participants
- slot

A meeting is created from a single free time slot.

---

## Business rules

### Time range validation

The start time of a slot must be before the end time.

Valid:

```text
10:00 - 11:00
```

Invalid:

```text
11:00 - 10:00
```

---

### Overlapping slots

Users can't create overlapping slots.

Existing slot:

```text
10:00 - 11:00
```

Rejected slot:

```text
10:30 - 11:30
```

This is enforced in the service layer and backed by a repository query.

---

### Meeting scheduling

A meeting can only be created from a slot with status:

```text
FREE
```

When a meeting is successfully created:

```text
FREE -> BOOKED
```

---

### Meeting cancellation

When a meeting is canceled:

```text
BOOKED -> FREE
```

The associated slot becomes available again.

---

## Error handling

The application uses custom exceptions:

- NotFoundException
- InvalidTimeRangeException
- SlotOverlapException
- SlotNotAvailableException

A global exception handler converts exceptions into meaningful HTTP responses.

Examples:

```http
404 Not Found
```

```json
{
  "message": "User not found: 1"
}
```

```http
400 Bad Request
```

```json
{
  "message": "Overlapping slot detected"
}
```

---

## Monitoring

Spring Boot Actuator is enabled.

Available endpoints:

```text
/actuator/health
/actuator/metrics
```

Custom application metrics:

```text
meetings.created
slots.created
```

These metrics provide visibility into system usage and scheduling activity.

---

## Testing strategy

The project contains multiple testing layers.

### Controller tests

Implemented using:

- MockMvc
- Mockito

Purpose:

- verify HTTP endpoints
- verify status codes
- verify request/response payloads

---

### Service tests

Implemented using:

- JUnit 5
- Mockito

Purpose:

- validate business rules
- verify slot overlap detection
- verify meeting scheduling logic
- verify cancellation flow

---

### Repository tests

Implemented for custom JPA queries.

Purpose:

- validate overlapping slot detection query
