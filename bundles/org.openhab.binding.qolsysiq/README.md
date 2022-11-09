# Qolsys IQ Binding

This binding controls a [Qolsys IQ] (https://qolsys.com/security/) panel.  This allows both monitoring of alarms and zone status as well as arming, disarming and triggering alarms.

## Supported Things

_Please describe the different supported things / devices including their ThingTypeUID within this section._
_Which different types are supported, which models were tested etc.?_
_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

- `bridge`: Short description of the Bridge, if any
- `sample`: Short description of the Thing with the ThingTypeUID `sample`

## Discovery

### Qolsys IQ Panel (Bridge) Discovery

The Qolsys IQ Panel must be manually added using a host name or ip address along with a secure access token from the panel settings.  To enable 3rd party control and retrieve the access token follow the following steps on the security panel:

Settings --> Advanced Settings --> Installation --> Dealer Settings -> 6 Digit User Code (enabled)

Settings --> Advanced Settings --> Installation --> Devices --> Wi-Fi Devices --> Control4 (enabled)

Panel will reboot

Settings --> Advanced Settings --> Installation --> Devices --> Wi-Fi Devices --> Reveal Secure Token (token to use)

### Partitions (Bridge) and Zones (Thing) Discovery

Once a panel is added, partitions will be automatically discovered and appear in the inbox.

Zones will be automatically discovered and appear in the inbox once thier parent partition has been added.

## Binding Configuration

_If your binding requires or supports general configuration settings, please create a folder ```cfg``` and place the configuration file ```<bindingId>.cfg``` inside it._
_In this section, you should link to this file and provide some information about the options._
_The file could e.g. look like:_

```
# Configuration for the QolsysIQ Binding
#
# Default secret key for the pairing of the QolsysIQ Thing.
# It has to be between 10-40 (alphanumeric) characters.
# This may be changed by the user for security reasons.
secret=openHABSecret
```

_Note that it is planned to generate some part of this based on the information that is available within ```src/main/resources/OH-INF/binding``` of your binding._

_If your binding does not offer any generic configurations, you can remove this section completely._

## Thing Configuration

_Describe what is needed to manually configure a thing, either through the UI or via a thing-file._
_This should be mainly about its mandatory and optional configuration parameters._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

### `sample` Thing Configuration

| Name            | Type    | Description                           | Default | Required | Advanced |
|-----------------|---------|---------------------------------------|---------|----------|----------|
| hostname        | text    | Hostname or IP address of the device  | N/A     | yes      | no       |
| password        | text    | Password to access the device         | N/A     | yes      | no       |
| refreshInterval | integer | Interval the device is polled in sec. | 600     | no       | yes      |

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

| Channel | Type   | Read/Write | Description                 |
|---------|--------|------------|-----------------------------|
| control | Switch | RW         | This is the control channel |

## Full Example

_Provide a full usage example based on textual configuration files._
_*.things, *.items examples are mandatory as textual configuration is well used by many users._
_*.sitemap examples are optional._

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
