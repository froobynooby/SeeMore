# SeeMore

## About
SeeMore is a simple Paper plugin that sets a player's server-side view distance according to their client-side render distance.

## Configuration

```yaml
# Configuration for SeeMore.

# Please don't change this!
version: 1

# The delay (in ticks) before a player's view distance is lowered after their client settings change.
#  * This stops players overloading the server by constantly changing their view distance.
update-delay: 600

# These settings can be specified per world.
#  * Note: If a world is not listed here or if a setting is missing, it will use the settings listed under the default
#    section.
world-settings:
  default:
    # The maximum view distance a player in this world can have.
    # Set to -1 to use the server's configured view distance for this world.
    maximum-view-distance: -1
```

## Building
If you would like to build the plugin yourself you can follow these steps.

1\. Install dependency NabConfiguration to maven local
```bash
git clone https://github.com/froobynooby/nab-configuration
cd nab-configuration
./gradlew clean install
```
2\. Clone SeeMore and build
```bash
git clone https://github.com/froobynooby/SeeMore
cd SeeMore
./gradlew clean build
```

3\. Find jar in `SeeMore/build/libs`
