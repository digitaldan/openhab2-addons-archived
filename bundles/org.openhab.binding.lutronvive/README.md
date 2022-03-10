# LutronVive Binding

This binding connects to a Lutron Vive Hub

## Supported Things


- `hub`: A Vive Hub which acts as a bridge for connected things
- `area`: A Vive area which allows control of lights and reports lights and occupancy status

## Discovery

Hub discovery is manual at the moment.

Areas are auto discovered when a Hub is added

## Binding Configuration

None

## Thing Configuration

hostname=something
username=admin
password=something

### `sample` Thing Configuration

| Name            | Type    | Description                           | Default | Required | Advanced |
|-----------------|---------|---------------------------------------|---------|----------|----------|
| hostname        | text    | Hostname or IP address of the device  | N/A     | yes      | no       |
| password        | text    | Password to access the device         | N/A     | yes      | no       |
| username        | text    | Username to access the device         | N/A     | yes      | no       |

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

| Channel | Type   | Read/Write | Description                 |
|---------|--------|------------|-----------------------------|
| switch | Switch | RW         | This is the switch channel |
| dimmer | Dimmer | RW         | This is the dimmer channel |
| occupancy | Switch | R         | This is the occupancy channel |

## Full Example

_Provide a full usage example based on textual configuration files._
_*.things, *.items examples are mandatory as textual configuration is well used by many users._
_*.sitemap examples are optional._

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
