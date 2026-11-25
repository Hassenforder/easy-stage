# easy-stage


## Name

easy-stage : Android application from my easy brand


## Description

Small Android application to manage seats in buildings

- You can register buildings with rooms
- In all rooms you can describe the structure (row x column) and record the type/state of a seat
- In case of an event, you can fill the state of the seats with 'people'

## Technologies covered

- Kotlin
- KSP
- Compose
- Coroutines
- Flow
- Room (database ORM)
- Architecture (database-dao-repository-viewmodel)
- Hilt (injection framework)
- Navigation 3
- Coil (Async Image)
- External App call (Document, Camera)

## Entities

- Building (name, picture)
- Chamber (buildingId, name, picture, nbRow, nbColumn)
- Seat (chamberId, x, y, type, state)

## Relations 

Building-Chamber 1-1 (by primaryKey)
Chamber-Seat 1-N (by primaryKey)

## Special tricks

- Use a grid to display a chamber with seats.
-- Constraints on seats, all seats have to be created to fill the grid.
-- Constraints on seats, x and y have be to uniq
-- Seat has a key to reorder them according to the order to fill the grid the right way
-- chrink a chamber means hide seats and not delete them.
-- expand a chamber means add seats fast as flow can be reactivated faster so creation can dupp seats .
 

- No heavy VM tricks

## Dependencies

- Mainly my beloved android lib (all standard required libs)
- No other special libs

### In libs.version.toml
    [versions]
    hassenforder-android = "XX"

    [libraries]
    fr-uha-android = { module = "fr.uha.hassenforder:easy.android", version.ref = "hassenforder.android" }

### In settings-gradle.kts

    dependencyResolutionManagement {
       repositories {
            ...
            maven(url = "https://maven.pkg.github.com/hassenforder/easy-android") {
                credentials {
                    username = providers.gradleProperty("github-user").get()
                    password = providers.gradleProperty("github-pat").get()
                }
            }
            maven(
                url = "https://jitpack.io"
            )
            ...
        }
    }

## License

Copyright © 2026 Hassenforder

Software released under the MIT license.

See the LICENSE file for more information.

https://opensource.org/licenses/MIT

## Project status

Ongoing
