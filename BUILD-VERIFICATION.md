# Build verification

## Project fixes applied

The project was reviewed for Spring Data JPA derived-query mismatches between entity relationships and repository method names.

The following relationship traversals were corrected:

- `Theater.city.id` -> `findByCity_Id(...)`
- `Seat.theater.id` -> `findByTheater_Id(...)`
- `Show.theater.id` -> `findByTheater_IdAndStartsAtBetweenOrderByStartsAtAsc(...)`
- `Show.movie.id` -> `findByMovie_IdAndStartsAtBetweenOrderByStartsAtAsc(...)`
- `Booking.user.id` -> `findByUser_IdOrderByCreatedAtDesc(...)`
- `BookingSeat.booking.id` -> `findByBooking_Id(...)`
- `Payment.booking.id` -> `findByBooking_Id(...)`

All usages in the seeder and services were updated to match the repository contracts.

## Local build

The project uses Spring Boot 3.5.6, Java 17, and is compatible with Gradle 8.14.x.

From the project root run:

```powershell
gradle clean build --refresh-dependencies
```

Then run:

```powershell
gradle bootRun
```

If you want a project-local Gradle Wrapper, generate it once with:

```powershell
gradle wrapper --gradle-version 8.14.1
```

After that the project can be built with:

```powershell
.\gradlew.bat clean build
```

A full Gradle build was not executed in the model execution environment because a Gradle distribution was not available there. Therefore this file does not claim a successful build that was not actually run.
